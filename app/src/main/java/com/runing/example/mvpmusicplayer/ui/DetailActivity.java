package com.runing.example.mvpmusicplayer.ui;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.runing.example.mvpmusicplayer.R;
import com.runing.example.mvpmusicplayer.base.BaseActivity;
import com.runing.example.mvpmusicplayer.contract.DetailContract;
import com.runing.example.mvpmusicplayer.data.bean.Music;
import com.runing.example.mvpmusicplayer.data.bean.MusicState;
import com.runing.example.mvpmusicplayer.presenter.DetailPresenter;
import com.runing.example.mvpmusicplayer.service.MusicService;
import com.runing.example.mvpmusicplayer.util.BitmapUtils;
import com.runing.example.mvpmusicplayer.util.TimeUtils;

import java.util.List;

/**
 * Created by runing on 2016/5/16.
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
 * along with MvpMusicPlayer.  If not, see <http://www.gnu.org/lice nses/>.
 */
public class DetailActivity extends BaseActivity implements DetailContract.View
        , View.OnClickListener {

    private DetailContract.Presenter mPresenter;
    /**
     * 可以响应事件
     */
    private boolean mIsAction;

    private Toolbar mTitleBar;
    //    private ImageView mMusicImage;
    private SeekBar mMusicSeekBar;
    private TextView mCurrTime;
    private TextView mTotalTime;

    private ImageView mModeLoop;
    private ImageView mModeOne;
    private ImageView mModeRandom;
    private ImageView mPlay;
    private ImageView mPause;
    //    private ImageView mPlayList;
    private ViewPager mMusicPager;

    private View[] mMusicPages;

    private int mMusicImgLength;

    private int delayPosition = -1;

    private Toast mToast;

    private boolean mIsUpdateProgress = true;

    @Override
    protected int onContentViewId() {
        return R.layout.activity_detail;
    }

    @SuppressLint("ShowToast")
    @Override
    protected void onAfterCreateView(Bundle savedInstanceState) {
        DetailPresenter.newInstance(this);
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

        mTitleBar = findCaseViewById(R.id.tb_detail);
        setSupportActionBar(mTitleBar);
        mTitleBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                } else {
                    finish();
                }
            }
        });

        ImageView mPre = findCaseViewById(R.id.iv_pre);
        ImageView mNext = findCaseViewById(R.id.iv_next);

        mCurrTime = findCaseViewById(R.id.tv_curr_time);
        mTotalTime = findCaseViewById(R.id.tv_total_time);
//        mMusicImage = findCaseViewById(R.id.iv_image);
        mMusicSeekBar = findCaseViewById(R.id.sb_progress);
        mModeLoop = findCaseViewById(R.id.iv_mode_loop);
        mModeOne = findCaseViewById(R.id.iv_mode_one);
        mModeRandom = findCaseViewById(R.id.iv_mode_random);
        mPlay = findCaseViewById(R.id.iv_play);
        mPause = findCaseViewById(R.id.iv_pause);
//        mPlayList = findCaseViewById(R.id.iv_play_list);
        mMusicPager = findCaseViewById(R.id.vp_music);

        mPre.setOnClickListener(this);
        mNext.setOnClickListener(this);
        mModeLoop.setOnClickListener(this);
        mModeOne.setOnClickListener(this);
        mModeRandom.setOnClickListener(this);
        mPlay.setOnClickListener(this);
        mPause.setOnClickListener(this);
//        mPlayList.setOnClickListener(this);
    }

    private class MusicPagerAdapter extends PagerAdapter {

        private List<Music> mData;

        public MusicPagerAdapter(List<Music> data) {
            mData = data;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mMusicPages[position]);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View page = mMusicPages[position];
            ((ImageView) page).setImageBitmap(BitmapUtils.decodeSampledBitmapFromFD(
                    DetailActivity.this, mData.get(position).getAlbum_id(),
                    mMusicImgLength, mMusicImgLength));
            container.addView(page);
            return page;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.recycleUi();
    }

    /**
     * 开启列表循环模式
     */
    private void startLoopMode() {
        mPresenter.setPlayMode(MusicService.PlayMode.LOOP);

        mToast.setText(getResources().getString(R.string.mode_play_loop));
        mToast.show();
    }

    /**
     * 开启单曲循环模式
     */
    private void startOneMode() {
        mPresenter.setPlayMode(MusicService.PlayMode.ONE);

        mToast.setText(getResources().getString(R.string.mode_play_one));
        mToast.show();
    }

    /**
     * 开启随机播放模式
     */
    private void startRandomMode() {
        mPresenter.setPlayMode(MusicService.PlayMode.RANDOM);

        mToast.setText(getResources().getString(R.string.mode_play_random));
        mToast.show();
    }

    private void hideAllMode() {
        mModeLoop.setVisibility(View.GONE);
        mModeOne.setVisibility(View.GONE);
        mModeRandom.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (mIsAction) {
            switch (v.getId()) {
                //循环播放模式
                case R.id.iv_mode_loop:
                    startOneMode();
                    break;
                //单曲循环模式
                case R.id.iv_mode_one:
                    startRandomMode();
                    break;
                //随机播放模式
                case R.id.iv_mode_random:
                    startLoopMode();
                    break;
                //上一曲
                case R.id.iv_pre:
                    mPresenter.preMusic();
                    break;
                //播放
                case R.id.iv_play:
                    mPresenter.playMusic();
                    break;
                //暂停
                case R.id.iv_pause:
                    mPresenter.pauseMusic();
                    break;
                //下一曲
                case R.id.iv_next:
                    mPresenter.nextMusic();
                    break;
                case R.id.iv_play_list:
                    break;
            }
        }
    }

    @Override
    public void setPresenter(DetailContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void initMusicPager(List<Music> musicList) {
        mMusicPages = new View[musicList.size()];
        for (int i = 0; i < mMusicPages.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mMusicPages[i] = imageView;
        }
        final MusicPagerAdapter pagerAdapter = new MusicPagerAdapter(musicList);

        if (mMusicImgLength != 0) {
            setMusicPager(pagerAdapter);
        } else {
            mMusicPager.post(new Runnable() {
                @Override
                public void run() {
                    mMusicImgLength = mMusicPager.getWidth();
                    setMusicPager(pagerAdapter);
                    if (delayPosition != -1) {
                        mMusicPager.setCurrentItem(delayPosition, false);
                        delayPosition = -1;
                    }
                }
            });
        }
    }

    private void setMusicPager(MusicPagerAdapter pagerAdapter) {
        mMusicPager.setAdapter(pagerAdapter);
        mMusicPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mPresenter.playSpecified(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void restoreMusic(MusicState state) {
        restoreMusicView(state);
        int total = state.getTotal();
        int progress = state.getProgress();

        mMusicPager.setCurrentItem(state.getPosition(), false);
        mMusicSeekBar.setMax(total);
        mMusicSeekBar.setProgress(progress);
        mTotalTime.setText(TimeUtils.millis2MSStr(total));
        mCurrTime.setText(TimeUtils.millis2MSStr(progress));

        mPresenter.setOnProgressListener(new MusicService.OnProgressListener() {
            @Override
            public void start(int total) {
                mMusicSeekBar.setMax(total);
                mTotalTime.setText(TimeUtils.millis2MSStr(total));
            }

            @Override
            public void progress(int progress) {
                if (mIsUpdateProgress) {
                    mMusicSeekBar.setProgress(progress);
                }
                mCurrTime.setText(TimeUtils.millis2MSStr(progress));
            }
        });
        mMusicSeekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_MOVE:
                        //拖拽时不更新进度
                        mIsUpdateProgress = false;
                        break;
                    case MotionEvent.ACTION_UP:
                        mIsUpdateProgress = true;
                        mPresenter.seekTo(mMusicSeekBar.getProgress());
                        break;
                }
                return false;
            }
        });
    }

    /**
     * 还原状态
     *
     * @param state 状态
     */
    private void restoreMusicView(MusicState state) {
        updatePlayMode(state.getMode());
        mMusicSeekBar.setMax(state.getTotal());
        mMusicSeekBar.setProgress(state.getProgress());
        updateMusic(state.getState(), state.getMusic(), state.getPosition());
    }

    @Override
    public void updateMusic(@NonNull MusicService.PlayState state,
                            @Nullable Music music, int position) {
        if (music != null) {
            //已经设置Adapter
            if (mMusicImgLength != 0) {
                mMusicPager.setCurrentItem(position, false);
            } else {
                //延迟设置ViewPager
                delayPosition = position;
            }
        }
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
                mMusicSeekBar.setProgress(0);
                mPlay.setVisibility(View.VISIBLE);
                mPause.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void updatePlayMode(@NonNull MusicService.PlayMode mode) {
        switch (mode) {
            case LOOP:
                hideAllMode();
                mModeLoop.setVisibility(View.VISIBLE);
                break;
            case ONE:
                hideAllMode();
                mModeOne.setVisibility(View.VISIBLE);
                break;
            case RANDOM:
                hideAllMode();
                mModeRandom.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void canAction() {
        this.mIsAction = true;
    }

    /**
     * 更新音乐信息ui
     *
     * @param music bean实例
     */
    private void updateMusicView(Music music) {
        if (music != null) {
//            installMusicImage(music.getAlbum_id());
            mTitleBar.setTitle(music.getTitle());
            mTitleBar.setSubtitle(music.getArtist());
        }
    }

    /**
     * 插图专辑图片
     *
     * @param albumId 专辑id
     */
//    private void installMusicImage(final long albumId) {
//        if (mMusicImgLength != 0) {
//            setMusicAlbumImage(albumId);
//        } else {
//            mMusicImage.post(new Runnable() {
//                @Override
//                public void run() {
//                    mMusicImgLength = mMusicImage.getWidth();
//                    setMusicAlbumImage(albumId);
//                }
//            });
//        }
//    }

    /**
     * 设置专辑图片
     *
     * @param albumId 专辑id
     */
//    private void setMusicAlbumImage(long albumId) {
//        mMusicImage.setImageBitmap(BitmapUtils.decodeSampledBitmapFromFD(this,
//                albumId, mMusicImgLength, mMusicImgLength));
//    }
}
