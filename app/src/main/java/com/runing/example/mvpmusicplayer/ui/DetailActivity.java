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
import android.widget.FrameLayout;
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
import com.runing.example.mvpmusicplayer.widget.MusicPageScaleInTransformer;

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
    private SeekBar mMusicSeekBar;
    private TextView mCurrTime;
    private TextView mTotalTime;

    private ImageView mModeLoop;
    private ImageView mModeOne;
    private ImageView mModeRandom;
    private ImageView mPlay;
    private ImageView mPause;
    /**
     * 专辑背景
     */
    private FrameLayout mMusicBg;
    private ViewPager mMusicPager;
    /**
     * 专辑图片尺寸
     */
    private int mMusicImgLength;
    /**
     * 提示
     */
    private Toast mToast;
    /**
     * 进度更新标志
     */
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
        mMusicSeekBar = findCaseViewById(R.id.sb_progress);
        mModeLoop = findCaseViewById(R.id.iv_mode_loop);
        mModeOne = findCaseViewById(R.id.iv_mode_one);
        mModeRandom = findCaseViewById(R.id.iv_mode_random);
        mPlay = findCaseViewById(R.id.iv_play);
        mPause = findCaseViewById(R.id.iv_pause);
        mMusicBg = findCaseViewById(R.id.fl_image_back);
        mMusicPager = findCaseViewById(R.id.vp_music);

        mPre.setOnClickListener(this);
        mNext.setOnClickListener(this);
        mModeLoop.setOnClickListener(this);
        mModeOne.setOnClickListener(this);
        mModeRandom.setOnClickListener(this);
        mPlay.setOnClickListener(this);
        mPause.setOnClickListener(this);
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

    /**
     * ViewPager适配器
     */
    private class MusicPagerAdapter extends PagerAdapter {

        private List<Music> mData;

        MusicPagerAdapter(List<Music> data) {
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
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView page = generatePageImageView();
            page.setImageBitmap(BitmapUtils.decodeSampledBitmapFromFD(
                    DetailActivity.this, mData.get(position).getAlbum_id(),
                    mMusicImgLength, mMusicImgLength));
            container.addView(page);
            return page;
        }

        private ImageView generatePageImageView() {
            ImageView imageView = new ImageView(DetailActivity.this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return imageView;
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            super.finishUpdate(container);
        }
    }

    @Override
    public void initMusicPager(final List<Music> musicList, final int position) {
        final MusicPagerAdapter pagerAdapter = new MusicPagerAdapter(musicList);
        if (mMusicImgLength != 0) {
            setMusicPager(pagerAdapter);
            showCurrentMusic(position);
        } else {
            //获取尺寸后设置
            mMusicPager.post(new Runnable() {
                @Override
                public void run() {
                    mMusicImgLength = mMusicPager.getWidth();
                    setMusicPager(pagerAdapter);
                    showCurrentMusic(position);
                }
            });
        }
    }

    /**
     * 设置ViewPager
     *
     * @param pagerAdapter adapter
     */
    private void setMusicPager(MusicPagerAdapter pagerAdapter) {
        mMusicPager.setAdapter(pagerAdapter);
        mMusicPager.setOffscreenPageLimit(3);
        mMusicPager.setPageTransformer(false, new MusicPageScaleInTransformer());
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
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    mMusicBg.setBackgroundColor(randomColor());
                }
            }
        });
    }

    private void showCurrentMusic(final int position) {
        mMusicPager.setCurrentItem(position, false);
    }

    @Override
    public void restoreMusic(MusicState state) {
        restoreMusicView(state);
        int total = state.getTotal();
        int progress = state.getProgress();

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
        showCurrentMusic(position);
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
            mTitleBar.setTitle(music.getTitle());
            mTitleBar.setSubtitle(music.getArtist());
        }
    }

    /**
     * 随机产生颜色
     *
     * @return color
     */
    private int randomColor() {
        int red = (int) (Math.random() * 128);
        int blue = (int) (Math.random() * 128);
        int green = (int) (Math.random() * 128);
        return 0xFF000000 | red << 16 | blue << 8 | green;
    }

}
