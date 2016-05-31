package com.runing.example.mvpmusicplayer.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.runing.example.mvpmusicplayer.contract.NotifyContract;
import com.runing.example.mvpmusicplayer.data.bean.Music;
import com.runing.example.mvpmusicplayer.service.MusicService;

import java.util.List;

/**
 * Created by runing on 2016/5/17.
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
public class NotifyPresenter implements NotifyContract.Presenter {

    private NotifyContract.View mNotifyView;
    /**
     * 服务
     */
    private MusicService mMusicService;
    /**
     * 音乐列表
     */
    private List<Music> mMusics;
    /**
     * 是否显示通知
     */
    private boolean mIsNotify;
    /**
     * 广播接收器
     */
    private BroadcastReceiver mNotifyReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                //播放
                case ACTION_PLAY:
                    playMusic();
                    break;
                //暂停
                case ACTION_PAUSE:
                    pauseMusic();
                    break;
                //上一曲
                case ACTION_PRE:
                    preMusic();
                    break;
                //下一曲
                case ACTION_NEXT:
                    nextMusic();
                    break;
                //关闭
                case ACTION_CLOSE:
                    mIsNotify = false;
                    recycleUi();
                    mMusicService.stopMusic();
                    break;
                default:
                    throw new IllegalArgumentException("action missing!");
            }
        }
    };

    private NotifyPresenter(MusicService mMusicService, NotifyContract.View mNotifyView) {
        this.mMusicService = mMusicService;
        this.mNotifyView = mNotifyView;
    }

    public static NotifyPresenter newInstance(MusicService mMusicService, NotifyContract.View mNotifyView) {
        NotifyPresenter presenter = new NotifyPresenter(mMusicService, mNotifyView);
        mNotifyView.setPresenter(presenter);
        return presenter;
    }

    /**
     * 注册广播接收器
     */
    private void registerBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_PLAY);
        filter.addAction(ACTION_PAUSE);
        filter.addAction(ACTION_PRE);
        filter.addAction(ACTION_NEXT);
        filter.addAction(ACTION_CLOSE);
        mMusicService.registerReceiver(mNotifyReceiver, filter);
    }

    /**
     * 解注册
     */
    private void unRegisterBroadcast() {
        mMusicService.unregisterReceiver(mNotifyReceiver);
    }

    @Override
    public void start() {
        registerBroadcast();
        mNotifyView.firstShow();
        mIsNotify = true;
    }

    /**
     * 通知是否显示
     *
     * @return yes or no?
     */
    public boolean isNotify() {
        return mIsNotify;
    }

    @Override
    public void playMusic() {
        int location = mMusicService.play();
        boolean isNoChange = location == MusicService.INDEX_FAILED;
        mNotifyView.updateMusic(MusicService.PlayState.PLAY, isNoChange ? null : mMusics.get(location));
    }

    @Override
    public void preMusic() {
        int location = mMusicService.preMusic();
        mNotifyView.updateMusic(MusicService.PlayState.SWITCH, mMusics.get(location));
    }

    @Override
    public void nextMusic() {
        int location = mMusicService.nextMusic();
        mNotifyView.updateMusic(MusicService.PlayState.SWITCH, mMusics.get(location));
    }

    @Override
    public void pauseMusic() {
        mMusicService.pause();
        mNotifyView.updateMusic(MusicService.PlayState.PAUSE, null);
    }

    @Override
    public void stopMusic() {
        mMusicService.stopMusic();
    }

    @Override
    public void recycleUi() {
        mNotifyView.closeNotify();
        unRegisterBroadcast();
    }

    /**
     * 服务回调数据
     *
     * @param musics 音乐数据
     */
    public void onInitMusicData(List<Music> musics) {
        this.mMusics = musics;
    }

    /**
     * 服务回调更新状态
     *
     * @param state    播放状态
     * @param position 位置
     */
    public void onChangeMusic(MusicService.PlayState state, int position) {
        boolean isNoChange = position == MusicService.INDEX_FAILED;
        mNotifyView.updateMusic(state, isNoChange ? null : mMusics.get(position));
    }
}
