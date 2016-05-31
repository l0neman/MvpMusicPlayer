package com.runing.example.mvpmusicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.runing.example.mvpmusicplayer.contract.NotifyContract;
import com.runing.example.mvpmusicplayer.data.bean.Music;
import com.runing.example.mvpmusicplayer.data.bean.MusicState;
import com.runing.example.mvpmusicplayer.data.biz.MusicGet;
import com.runing.example.mvpmusicplayer.presenter.MusicAWPresenter;
import com.runing.example.mvpmusicplayer.presenter.NotifyPresenter;
import com.runing.example.mvpmusicplayer.ui.MusicNotification;

import java.io.IOException;
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
public final class MusicService extends Service {

    /**
     * 数据保存配置名称
     */
    private static final String SHARE_SAVE_NAME = "music_save";
    /**
     * MusicId数据保存key
     */
    private static final String MUSIC_ID_KAY = "mMusicId";
    /**
     * MusicId数据保存key
     */
    private static final String MUSIC_POSITION_KEY = "mPosition";
    /**
     * PlayMode数据保存key
     */
    private static final String MUSIC_PLAY_MODE_KEY = "mPlayMode";
    /**
     * 默认musicId
     */
    private static final long DEFAULT_MUSIC_ID = -1;
    /**
     * 默认position
     */
    public static final int INDEX_FAILED = -1;

    private static volatile MusicService mMusicService;
    /**
     * 数据回调
     */
    private UpdateCallBack mCallBack;
    /**
     * 音乐数据
     */
    private List<Music> mMusics;
    /**
     * 多媒体
     */
    private MediaPlayer mMediaPlayer;
    /**
     * 当前musicId
     */
    private long mMusicId = DEFAULT_MUSIC_ID;
    /**
     * 当前index
     */
    private int mIndex = -1;
    /**
     * 当前音乐实例
     */
    private Music mCurrMusic;
    /**
     * 是否处于暂停
     */
    private boolean mIsPause;
    /**
     * 当前进度
     */
    private int mCurrentProgress;
    /**
     * 进度回调
     */
    private OnProgressListener mOnProgressListener;
    /**
     * 消息处理
     */
    private Handler mHandler = new Handler();
    /**
     * 播放模式
     */
    private PlayMode mPlayMode = PlayMode.LOOP;
    /**
     * 状态栏通知presenter
     */
    private NotifyPresenter mNotifyPresenter;

    private MusicAWPresenter mMusicAWPresenter;

    public void setMusicAWPresenter(MusicAWPresenter mMusicAWPresenter) {
        this.mMusicAWPresenter = mMusicAWPresenter;
        if (mMusics != null && mMusicAWPresenter != null) {
            mMusicAWPresenter.onInitMusicData(mMusics);
        }
    }

    /**
     * 播放状态
     */
    public enum PlayState {
        /*
         * 播放
         */
        PLAY,
        /**
         * 暂停
         */
        PAUSE,
        /**
         * 切换
         */
        SWITCH,
        /**
         * 停止
         */
        STOP
    }

    /**
     * 进度任务
     */
    private Runnable mProgressRun = new Runnable() {
        @Override
        public void run() {
            if (isActive()) {
                if (mOnProgressListener != null) {
                    mOnProgressListener.progress(mCurrentProgress = mMediaPlayer.getCurrentPosition());
                }
            }
            mHandler.postDelayed(this, 1000);
        }
    };

    public interface OnProgressListener {

        /**
         * 开始
         *
         * @param total 总数
         */
        void start(int total);

        /**
         * 进行中
         *
         * @param progress 进度
         */
        void progress(int progress);

    }

    public interface UpdateCallBack {
        /**
         * 初始化音乐列表
         *
         * @param musics 数据
         */
        void onInitMusicData(List<Music> musics);

        /**
         * 音乐发生切换
         *
         * @param position 位置
         */
        void onChangeMusic(PlayState state, int position);

        void onChangeMode(PlayMode mode);
    }

    public void setDataCallBack(UpdateCallBack mCallBack) {
        this.mCallBack = mCallBack;
        if (mMusics != null && mCallBack != null) {
            mCallBack.onInitMusicData(mMusics);
        }
    }

    public void setOnProgressListener(OnProgressListener mOnProgressListener) {
        this.mOnProgressListener = mOnProgressListener;
    }

    /**
     * 取消进度监听
     */
    public void cancelProgressListener() {
        this.mOnProgressListener = null;
    }

    /**
     * 取消数据回调
     */
    public void cancelDataCallBack() {
        this.mCallBack = null;
    }

    /**
     * 播放模式
     */
    public enum PlayMode {
        /**
         * 列表循环
         */
        LOOP,
        /**
         * 随机播放
         */
        RANDOM,
        /**
         * 单曲循环
         */
        ONE

    }

    /**
     * 设置播放模式
     *
     * @param mPlayMode 模式
     */
    public void setPlayMode(PlayMode mPlayMode) {
        this.mPlayMode = mPlayMode;
        notifyPlayMode();
        if (isActive()) {
            if (mPlayMode == PlayMode.ONE) {
                mMediaPlayer.setLooping(true);
            } else {
                mMediaPlayer.setLooping(false);
            }
        }
    }

    /**
     * 指定进度
     *
     * @param progress 进度
     */
    public void seekTo(int progress) {
        if (isActive()) {
            mCurrentProgress = progress;
            mMediaPlayer.seekTo(progress);
        }
    }

    /**
     * 初始化通知
     */
    private void initNotification() {
        NotifyContract.View mNotifyView = new MusicNotification(this);
        mNotifyPresenter = NotifyPresenter.newInstance(this, mNotifyView);
        mNotifyPresenter.onInitMusicData(mMusics);
        mNotifyPresenter.start();
        mNotifyPresenter.onChangeMusic(PlayState.STOP, mIndex);
    }

    @Override
    public void onCreate() {
        mMusicService = this;
        initMediaPlayer();
        initData();
    }

    public static MusicService getGlobalService() {
        return mMusicService;
    }

    /**
     * 获取当前音乐状态
     *
     * @return 音乐状态实例
     */
    public MusicState getCurrentMusicState() {
        MusicState musicState = new MusicState();
        musicState.setMusic(mCurrMusic);
        musicState.setMode(mPlayMode);
        musicState.setPosition(INDEX_FAILED);
        if (isActive()) {
            musicState.setTotal(mMediaPlayer.getDuration());
            musicState.setPosition(mIndex);
            musicState.setProgress(mCurrentProgress);
        }
        if (mMediaPlayer.isPlaying()) {
            musicState.setState(PlayState.PLAY);
        } else {
            musicState.setState(PlayState.PAUSE);
        }
        return musicState;
    }

    /**
     * 初始化
     */
    private void initData() {
        final MusicGet musicGet = new MusicGet();
        musicGet.getMusicList(getContentResolver(), new MusicGet.ScanCallBack() {
            @Override
            public void onOverScanned(List<Music> musics) {
                MusicService.this.mMusics = musics;
                initHistoryMusic();
                restoreMusic();
                if (mCallBack != null) {
                    mCallBack.onInitMusicData(musics);
                }
                if (mMusicAWPresenter != null) {
                    mMusicAWPresenter.onInitMusicData(musics);
                }
                mProgressRun.run();
                initNotification();
            }
        });
    }

    /**
     * 恢复进度
     */
    private void restoreMusic() {
        if (mCurrentProgress != 0) {
            mIsPause = true;
            try {
                mMediaPlayer.setDataSource(mCurrMusic.getUrl());
                mMediaPlayer.prepare();
                mMediaPlayer.seekTo(mCurrentProgress);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 初始化历史音乐
     */
    private void initHistoryMusic() {
        if (hasMusicData()) {
            readMusicHistory();
            mIndex = findIndex(mMusics, mMusicId);
            if (mIndex == INDEX_FAILED) {
                setDefaultMusic();
            } else {
                mCurrMusic = mMusics.get(mIndex);
            }
        }
    }

    /**
     * 查询index
     */
    public static int findIndex(List<Music> mMusics, long mMusicId) {
        int index = 0;
        for (Music music : mMusics) {
            if (music.getId() == mMusicId) {
                return index;
            } else {
                index++;
            }
        }
        return INDEX_FAILED;
    }

    /**
     * 设置默认音乐
     */
    private void setDefaultMusic() {
        mIndex = 0;
        mCurrentProgress = 0;
        updateCurrMusic();
    }

    /**
     * 更新当前音乐
     */
    private void updateCurrMusic() {
        mCurrMusic = mMusics.get(mIndex);
        mMusicId = mCurrMusic.getId();
    }

    /**
     * 是否有数据
     *
     * @return has data ?
     */
    private boolean hasMusicData() {
        return mMusics.size() != 0;
    }

    /**
     * 初始化多媒体
     */
    private void initMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                final int position = nextMusic();
                if (mCallBack != null && mPlayMode != PlayMode.ONE) {
                    mCallBack.onChangeMusic(PlayState.SWITCH, position);
                }
                if (mNotifyPresenter.isNotify()) {
                    mNotifyPresenter.onChangeMusic(PlayState.SWITCH, position);
                }
                if (mMusicAWPresenter != null) {
                    mMusicAWPresenter.onChangeMusic(PlayState.SWITCH, position);
                }
            }
        });
    }

    /**
     * 通知音乐切换
     */
    private void notifyPlayState(PlayState state, int position) {
        if (mCallBack != null) {
            mCallBack.onChangeMusic(state, position);
        }
        if (mNotifyPresenter != null && mNotifyPresenter.isNotify()) {
            mNotifyPresenter.onChangeMusic(state, position);
        }
        if (mMusicAWPresenter != null) {
            mMusicAWPresenter.onChangeMusic(state, position);
        }
    }

    /**
     * 通知模式切换
     */
    private void notifyPlayMode() {
        if (mCallBack != null) {
            mCallBack.onChangeMode(mPlayMode);
        }
        if (mMusicAWPresenter != null) {
            mMusicAWPresenter.onChangeMode(mPlayMode);
        }
    }

    /**
     * 播放
     *
     * @return index
     */
    public int play() {
        if (mIsPause) {
            mMediaPlayer.start();
            notifyPlayState(PlayState.SWITCH, INDEX_FAILED);
            return INDEX_FAILED;
        } else {
            playNewMusic();
            notifyPlayState(PlayState.SWITCH, mIndex);
            //初始化进度
            if (mOnProgressListener != null) {
                mOnProgressListener.progress(0);
                mOnProgressListener.start(mMediaPlayer.getDuration());
            }
            //检查通知状态
            if (!mNotifyPresenter.isNotify()) {
                mNotifyPresenter.start();
                mNotifyPresenter.onChangeMusic(PlayState.PLAY, mIndex);
            }
        }
        mIsPause = false;
        return mIndex;
    }

    /**
     * 播放新的音乐
     */
    private void playNewMusic() {
        mMediaPlayer.reset();
        try {
            mMediaPlayer.setDataSource(mCurrMusic.getUrl());
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MusicService.this, "音乐播放失败!", Toast.LENGTH_SHORT).show();
        }
        //播放新音乐重新设置单曲循环
        if (mPlayMode == PlayMode.ONE) {
            mMediaPlayer.setLooping(true);
        }
        mMediaPlayer.start();
    }

    /**
     * 暂停
     */
    public void pause() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            mIsPause = true;
        }
        notifyPlayState(PlayState.PAUSE, INDEX_FAILED);
    }

    /**
     * 下一首
     *
     * @return index
     */
    public int nextMusic() {
        //检查模式
        checkMode();
        updateCurrMusic();
        mIsPause = false;
        mIsPause = false;
        return play();
    }

    /**
     * 上一首
     *
     * @return index
     */
    public int preMusic() {
        if (mIndex == 0) {
            mIndex = mMusics.size() - 1;
        } else {
            mIndex--;
        }
        mIsPause = false;
        updateCurrMusic();
        return play();
    }

    /**
     * 停止音乐
     */
    public void stopMusic() {
        mIsPause = false;
        if (isActive()) {
            mMediaPlayer.stop();
        }
        mCurrentProgress = 0;
        notifyPlayState(PlayState.STOP, INDEX_FAILED);
        if (mCallBack != null) {
            mCallBack.onChangeMusic(PlayState.STOP, INDEX_FAILED);
        }
    }

    /**
     * 模式检查
     */
    private void checkMode() {
        switch (mPlayMode) {
            case LOOP:
                mMediaPlayer.setLooping(false);
            case ONE:
                if (mIndex == mMusics.size() - 1) {
                    mIndex = 0;
                } else {
                    mIndex++;
                }
                break;
            case RANDOM: {
                mMediaPlayer.setLooping(false);
                mIndex = (int) (Math.random() * (mMusics.size() - 1));
                break;
            }
        }
    }

    /**
     * 播放指定音乐
     *
     * @param position musics position
     */
    public int playSpecified(int position) {
        //和原音乐位置不同
        if (mIndex != position) {
            mIndex = position;
            mIsPause = false;
            updateCurrMusic();
            return play();
        }//相同但处于暂停
        else if (mIsPause) {
            return play();
        }
        return INDEX_FAILED;
    }

    /**
     * 保存播放历史
     */
    private void saveMusicHistory() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARE_SAVE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(MUSIC_ID_KAY, mMusicId);
        editor.putInt(MUSIC_POSITION_KEY, mCurrentProgress);
        editor.putString(MUSIC_PLAY_MODE_KEY, mPlayMode.toString());
        editor.apply();
    }

    /**
     * 读取播放历史
     */
    private void readMusicHistory() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARE_SAVE_NAME, MODE_PRIVATE);
        mMusicId = sharedPreferences.getLong(MUSIC_ID_KAY, DEFAULT_MUSIC_ID);
        mCurrentProgress = sharedPreferences.getInt(MUSIC_POSITION_KEY, 0);
        final String mode = sharedPreferences.getString(MUSIC_PLAY_MODE_KEY, PlayMode.ONE.toString());
        mPlayMode = PlayMode.valueOf(mode);
    }

    @Override
    public void onDestroy() {
        mMusicService = null;
        saveMusicHistory();
        recycle();
    }

    /**
     * 回收资源
     */
    private void recycle() {
        mHandler.removeCallbacks(mProgressRun);
        cancelDataCallBack();
        cancelProgressListener();
        if (mNotifyPresenter.isNotify()) {
            mNotifyPresenter.recycleUi();
        }
        if (mMusicAWPresenter != null) {
            mMusicAWPresenter.recycleUi();
        }
        mMediaPlayer.release();
    }

    /**
     * 处于活动状态
     *
     * @return yes or no?
     */
    private boolean isActive() {
        return mIsPause || mMediaPlayer.isPlaying();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }

    public class Binder extends android.os.Binder {

        /**
         * 返回service实例
         */
        public MusicService getService() {
            return MusicService.this;
        }
    }

}
