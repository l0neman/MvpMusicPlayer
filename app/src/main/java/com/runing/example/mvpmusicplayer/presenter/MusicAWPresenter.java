package com.runing.example.mvpmusicplayer.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.runing.example.mvpmusicplayer.contract.MainContract;
import com.runing.example.mvpmusicplayer.contract.MusicAWContract;
import com.runing.example.mvpmusicplayer.data.bean.Music;
import com.runing.example.mvpmusicplayer.data.bean.MusicState;
import com.runing.example.mvpmusicplayer.service.MusicService;
import com.runing.example.mvpmusicplayer.ui.MainActivity;

import java.util.List;

/**
 * Created by runing on 2016/5/18.
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
public class MusicAWPresenter implements MusicAWContract.Presenter, MusicService.UpdateCallBack {

    /**
     * 音乐列表
     */
    private List<Music> mMusics;

    private MusicService mMusicService;

    private Context mContext;

    private MusicAWContract.View mAWView;

    private boolean mPlayDelay;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            init();
            return true;
        }
    });

    private MusicAWPresenter(Context mContext, MusicAWContract.View mAWView) {
        this.mAWView = mAWView;
        this.mContext = mContext;
    }

    public static MusicAWPresenter newInstance(Context mContext, MusicAWContract.View awView) {
        MusicAWPresenter presenter = new MusicAWPresenter(mContext, awView);
        awView.setPresenter(presenter);
        return presenter;
    }

    @Override
    public void setPlayMode(MusicService.PlayMode mPlayMode) {
        mMusicService.setPlayMode(mPlayMode);
    }

    @Override
    public void playMusic() {
        mMusicService.play();
    }

    @Override
    public void requestPlay() {
        connectionService();
    }

    @Override
    public void requestRestore() {
        if(isInitService()){
            init();
        }
    }

    @Override
    public void preMusic() {
        mMusicService.preMusic();
    }

    @Override
    public void nextMusic() {
        mMusicService.nextMusic();
    }

    @Override
    public void pauseMusic() {
        mMusicService.pause();
    }

    @Override
    public void enterSearch() {
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(MainContract.View.ACTION_START_KEY, MainContract.View.ACTION_START_SEARCH);
        mContext.startActivity(intent);
    }

    @Override
    public void enterDetail() {
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(MainContract.View.ACTION_START_KEY, MainContract.View.ACTION_START_DETAIL);
        mContext.startActivity(intent);
    }

    @Override
    public void recycleUi() {
        if (mMusicService != null) {
            mMusicService.setMusicAWPresenter(null);
        }
        mAWView.clearView();
    }

    @Override
    public void start() {
        // noting
    }

    private void init() {
        mMusicService.setMusicAWPresenter(null);
        mMusicService.setMusicAWPresenter(this);
        mAWView.canAction();
    }

    private void connectionService() {
        if (!isInitService()) {
            mPlayDelay = true;
            startService();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    do {
                        if (isInitService()) {
                            mHandler.sendEmptyMessage(0);
                        }
                    } while (mMusicService == null);
                }
            }).start();
        } else {
            init();
        }
    }

    /**
     * 服务是否初始化
     */
    private boolean isInitService() {
        return (mMusicService = MusicService.getGlobalService()) != null;
    }

    /**
     * 开启服务
     */
    private void startService() {
        Intent intent = new Intent(mContext, MusicService.class);
        mContext.startService(intent);
    }

    @Override
    public void onInitMusicData(List<Music> musics) {
        this.mMusics = musics;
        MusicState musicState = mMusicService.getCurrentMusicState();
        mAWView.restoreMusic(musicState);
        if (mPlayDelay) {
            playMusic();
            mPlayDelay = false;
        }
    }

    @Override
    public void onChangeMusic(MusicService.PlayState state, int position) {
        boolean isNoChange = position == MusicService.INDEX_FAILED;
        mAWView.updateMusic(state, isNoChange ? null : mMusics.get(position));
    }

    @Override
    public void onChangeMode(MusicService.PlayMode mode) {
        mAWView.updatePlayMode(mode);
    }
}
