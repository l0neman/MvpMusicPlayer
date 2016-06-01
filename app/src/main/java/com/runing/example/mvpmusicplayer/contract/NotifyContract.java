package com.runing.example.mvpmusicplayer.contract;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.runing.example.mvpmusicplayer.base.BasePresenter;
import com.runing.example.mvpmusicplayer.base.BaseView;
import com.runing.example.mvpmusicplayer.data.bean.Music;
import com.runing.example.mvpmusicplayer.service.MusicService;

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
public interface NotifyContract {

    interface View extends BaseView<Presenter> {
        /**
         * 播放事件
         */
        String ACTION_PLAY = "ACTION_PLAY";

        /**
         * 暂停事件
         */
        String ACTION_PAUSE = "ACTION_PAUSE";

        /**
         * 上一曲事件
         */
        String ACTION_PRE = "ACTION_PRE";

        /**
         * 下一曲事件
         */
        String ACTION_NEXT = "ACTION_NEXT";
        /**
         * 关闭事件
         */
        String ACTION_CLOSE = "ACTION_CLOSE";

        /**
         * 首次
         */
        void firstShow();

        /**
         * 更新音乐
         *
         * @param state 状态
         * @param music 音乐实例
         */
        void updateMusic(@NonNull MusicService.PlayState state, @Nullable Music music);

        /**
         * 关闭通知
         */
        void closeNotify();
    }

    interface Presenter extends BasePresenter {

        /**
         * 播放
         */
        void playMusic();

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
         * 关闭
         */
        void close();

        /**
         * 回收
         */
        void recycleUi();
    }
}
