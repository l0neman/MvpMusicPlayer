package com.runing.example.mvpmusicplayer.contract;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.runing.example.mvpmusicplayer.base.BasePresenter;
import com.runing.example.mvpmusicplayer.base.BaseView;
import com.runing.example.mvpmusicplayer.data.bean.Music;
import com.runing.example.mvpmusicplayer.data.bean.MusicState;
import com.runing.example.mvpmusicplayer.service.MusicService;

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
 * along with MvpMusicPlayer.  If not, see <http://www.gnu.org/licenses/>.
 */
public interface DetailContract {

    interface View extends BaseView<Presenter> {

        void initMusicPager(List<Music> musicList);

        /**
         * 还原音乐显示
         *
         * @param state 状态
         */
        void restoreMusic(MusicState state);

        /**
         * 更新音乐
         *
         * @param state 状态
         * @param music 音乐实例
         */
        void updateMusic(@NonNull MusicService.PlayState state,
                         @Nullable Music music, int position);

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
    }

    interface Presenter extends BasePresenter {

        /**
         * 设置监听
         *
         * @param onProgressListener 进度
         */
        void setOnProgressListener(MusicService.OnProgressListener onProgressListener);

        /**
         * 设置到指定进度
         *
         * @param progress 进度
         */
        void seekTo(int progress);

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
         * 回收
         */
        void recycleUi();

        /**
         * 播放指定音乐
         *
         * @param position 音乐id
         */
        void playSpecified(int position);
    }
}
