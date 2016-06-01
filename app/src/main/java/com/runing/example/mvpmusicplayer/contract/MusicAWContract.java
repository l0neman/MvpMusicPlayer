package com.runing.example.mvpmusicplayer.contract;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.runing.example.mvpmusicplayer.base.BasePresenter;
import com.runing.example.mvpmusicplayer.base.BaseView;
import com.runing.example.mvpmusicplayer.data.bean.Music;
import com.runing.example.mvpmusicplayer.data.bean.MusicState;
import com.runing.example.mvpmusicplayer.service.MusicService;

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
public interface MusicAWContract {

    interface View extends BaseView<Presenter> {

        String ACTION_PLAY = "music.aw.click.play";
        String ACTION_PAUSE = "music.aw.click.pause";
        String ACTION_PRE = "music.aw.click.pre";
        String ACTION_NEXT = "music.aw.click.next";
        String ACTION_MODE_LOOP = "music.aw.click.mode.loop";
        String ACTION_MODE_ONE = "music.aw.click.mode.one";
        String ACTION_MODE_RANDOM = "music.aw.click.mode.random";
        String ACTION_SEARCH = "music.aw.click.search";
        String ACTION_DETAIL = "music.aw.click.detail";

        void restoreMusic(MusicState state);

        /**
         * 更新音乐
         *
         * @param state 状态
         * @param music 音乐实例
         */
        void updateMusic(@NonNull MusicService.PlayState state, @Nullable Music music);

        /**
         * 通知改变模式
         *
         * @param mode 模式
         */

        void updatePlayMode(@NonNull MusicService.PlayMode mode);

        /**
         * 允许响应
         */
        void canAction();

        void clearView();
    }

    interface Presenter extends BasePresenter {

        /**
         * 设置播放模式
         *
         * @param mPlayMode 模式
         */
        void setPlayMode(MusicService.PlayMode mPlayMode);

        /**
         * 播放
         */
        void playMusic();

        /**
         * 请求播放
         */
        void requestPlay();

        /**
         * 请求还原
         */
        void requestRestore();

        /**
         * 上一曲
         */
        void preMusic();

        /**
         * 下一曲
         */
        void nextMusic();

        /**
         * 暂停
         */
        void pauseMusic();

        /**
         * 进入搜索
         */
        void enterSearch();

        /**
         * 进入详情
         */
        void enterDetail();

        /**
         * 回收
         */
        void recycleUi();
    }
}
