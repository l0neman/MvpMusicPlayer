package com.runing.example.mvpmusicplayer.presenter;

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
public class NotifyPresenter implements NotifyContract.Presenter, MusicService.MusicCallBack {

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

    private NotifyPresenter(MusicService mMusicService, NotifyContract.View mNotifyView) {
        this.mMusicService = mMusicService;
        this.mNotifyView = mNotifyView;
        mNotifyView.setPresenter(this);
    }

    public static NotifyPresenter newInstance(MusicService mMusicService,
                                              NotifyContract.View mNotifyView) {
        NotifyPresenter presenter = new NotifyPresenter(mMusicService, mNotifyView);
        mNotifyView.setPresenter(presenter);
        return presenter;
    }

    @Override
    public void start() {
        //添加回调
        mMusicService.addMusicCallBack(this);
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
        boolean isNoChange = location == MusicService.INDEX_DEFAULT;
        mNotifyView.updateMusic(MusicService.PlayState.PLAY,
                isNoChange ? null : mMusics.get(location));
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
    public void close() {
        mIsNotify = false;
        recycleUi();
        mMusicService.stopMusic();
    }

    @Override
    public void recycleUi() {
        //移除回调
        mMusicService.removeMusicCallBack(this);
        mNotifyView.closeNotify();
    }

    /**
     * 服务回调数据
     *
     * @param musics 音乐数据
     */
    @Override
    public void onInitMusicData(List<Music> musics) {
        this.mMusics = musics;
    }

    /**
     * 服务回调更新状态
     *
     * @param state    播放状态
     * @param position 位置
     */
    @Override
    public void onChangeCurrMusic(MusicService.PlayState state, int position) {
        boolean isNoChange = position == MusicService.INDEX_DEFAULT;
        mNotifyView.updateMusic(state, isNoChange ? null : mMusics.get(position));
    }

    @Override
    public void onChangeMusicMode(MusicService.PlayMode mode,List<Music> randomMusics) {
        // null
    }

}
