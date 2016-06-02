package com.runing.example.mvpmusicplayer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.runing.example.mvpmusicplayer.R;
import com.runing.example.mvpmusicplayer.apdater.MusicAdapter;
import com.runing.example.mvpmusicplayer.base.BaseActivity;
import com.runing.example.mvpmusicplayer.base.BaseRVAdapter;
import com.runing.example.mvpmusicplayer.contract.MainContract;
import com.runing.example.mvpmusicplayer.data.bean.Music;
import com.runing.example.mvpmusicplayer.data.bean.MusicState;
import com.runing.example.mvpmusicplayer.presenter.MainPresenter;
import com.runing.example.mvpmusicplayer.service.MusicService;
import com.runing.example.mvpmusicplayer.util.BitmapUtils;

import java.util.List;

/**
 * Created by runing on 2016/5/13.
 * <p/>
 * This file is part of MvpMusicPlayer.
 * MvpMusicPlayer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * MvpMusicPlayer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with MvpMusicPlayer.  If not, see <http://www.gnu.org/licenses/>.
 */
public class MainActivity extends BaseActivity implements MainContract.View,
        View.OnClickListener {

    private MainContract.Presenter mPresenter;

    /**
     * 音乐列表
     */
    private RecyclerView mMusicList;

    private MusicAdapter mMusicAdapter;
    /**
     * 可以响应
     */
    private boolean mIsAction;

    private ImageView mMusicImage;
    private int mMusicImgLength;

    private TextView mMusicName;
    private TextView mArtistName;
    private ImageView mPlay;
    private ImageView mPause;
    private ProgressBar mMusicBar;

    @Override
    protected int onContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onAfterCreateView(Bundle savedInstanceState) {
        MainPresenter.newInstance(this);
        setSupportActionBar((Toolbar) findViewById(R.id.tb_main));

        mMusicList = findCaseViewById(R.id.lv_music_list);
        mMusicImage = findCaseViewById(R.id.iv_image);
        mMusicName = findCaseViewById(R.id.tv_name);
        mArtistName = findCaseViewById(R.id.tv_singer);
        mPlay = findCaseViewById(R.id.iv_play);
        mPause = findCaseViewById(R.id.iv_pause);
        ImageView mNext = findCaseViewById(R.id.iv_next);
        mMusicBar = findCaseViewById(R.id.pb_bottom);

        mMusicImage.setOnClickListener(this);
        mPlay.setOnClickListener(this);
        mPause.setOnClickListener(this);
        mNext.setOnClickListener(this);
    }

    /**
     * 跳入指定活动
     */
    private void jumpIntoActivity(Intent intent) {
        final int action = intent.getIntExtra(ACTION_START_KEY, -1);
        switch (action) {
            case MainContract.View.ACTION_START_DETAIL:
                getIntent().putExtra(ACTION_START_KEY, -1);
                mPresenter.enterDetail(null);
                break;
            case MainContract.View.ACTION_START_SEARCH:
                getIntent().putExtra(ACTION_START_KEY, -1);
                mPresenter.enterSearch();
                break;
            default:
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        jumpIntoActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.start();
        jumpIntoActivity(getIntent());
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.recycleUi();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mIsAction) {
            switch (item.getItemId()) {
                case R.id.action_exit:
                    mPresenter.exitApp();
                    break;
                case R.id.action_search:
                    mPresenter.enterSearch();
                    break;
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void showMusicList(List<Music> musicList) {
        mMusicAdapter = new MusicAdapter(this, musicList);
        mMusicList.setLayoutManager(new LinearLayoutManager(this));
        mMusicList.setItemAnimator(new DefaultItemAnimator());
        mMusicList.setAdapter(mMusicAdapter);
        mMusicAdapter.setOnItemOnClickListener(new BaseRVAdapter.OnItemOnClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                mPresenter.playSpecified(position);
            }
        });
    }

    /**
     * 设置选择项
     */
    private void selectListItem(int position) {
        if (position != MusicService.INDEX_DEFAULT) {
            mMusicAdapter.select(position);
        }
    }

    @Override
    public void restoreMusic(MusicState state) {

        selectListItem(state.getPosition());
        restoreMusicView(state);
        mPresenter.setOnProgressListener(new MusicService.OnProgressListener() {
            @Override
            public void start(int total) {
                mMusicBar.setMax(total);
                mMusicBar.setProgress(0);
            }

            @Override
            public void progress(int progress) {
                mMusicBar.setProgress(progress);
            }
        });
    }

    private void restoreMusicView(MusicState state) {
        mMusicBar.setMax(state.getTotal());
        mMusicBar.setProgress(state.getProgress());
        updateMusic(state.getState(), state.getMusic(), state.getPosition());
    }

    @Override
    public void updateMusic(@NonNull MusicService.PlayState state,
                            @Nullable Music music, int position) {
        selectListItem(position);
        updateMusicView(music);
        switch (state) {
            case PLAY:
                mPlay.setVisibility(View.GONE);
                mPause.setVisibility(View.VISIBLE);
                break;
            case PAUSE:
                mPlay.setVisibility(View.VISIBLE);
                mPause.setVisibility(View.GONE);
                break;
            case SWITCH:
                mPlay.setVisibility(View.GONE);
                mPause.setVisibility(View.VISIBLE);
                break;
            case STOP:
                mMusicBar.setProgress(0);
                mPlay.setVisibility(View.VISIBLE);
                mPause.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 更新底栏
     *
     * @param music 音乐信息
     */
    private void updateMusicView(Music music) {
        if (music != null) {
            installMusicImage(music.getAlbum_id());
            mMusicName.setText(music.getTitle());
            mArtistName.setText(music.getArtist());
        }
    }

    /**
     * 初始化专辑图片
     */
    private void installMusicImage(final long albumId) {
        if (mMusicImgLength != 0) {
            setMusicAlbumImage(albumId);
        } else {
            mMusicImage.post(new Runnable() {
                @Override
                public void run() {
                    mMusicImgLength = mMusicImage.getWidth();
                    setMusicAlbumImage(albumId);
                }
            });
        }
    }

    /**
     * 设置专辑图片
     *
     * @param albumId 专辑id
     */
    private void setMusicAlbumImage(long albumId) {
        mMusicImage.setImageBitmap(BitmapUtils.decodeSampledBitmapFromFD(this,
                albumId, mMusicImgLength, mMusicImgLength));
    }

    @Override
    public void canAction() {
        mIsAction = true;
    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void onClick(View v) {
        if (mIsAction) {
            switch (v.getId()) {
                case R.id.iv_play:
                    mPresenter.playMusic();
                    break;
                case R.id.iv_pause:
                    mPresenter.pauseMusic();
                    break;
                case R.id.iv_next:
                    mPresenter.nextMusic();
                    break;
                case R.id.iv_image:
                    mPresenter.enterDetail(mMusicImage);
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.handBackResult(requestCode, resultCode, data);
    }
}
