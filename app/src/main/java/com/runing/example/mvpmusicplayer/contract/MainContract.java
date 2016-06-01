package com.runing.example.mvpmusicplayer.contract;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.BaseAdapter;

import com.runing.example.mvpmusicplayer.base.BasePresenter;
import com.runing.example.mvpmusicplayer.base.BaseView;
import com.runing.example.mvpmusicplayer.data.bean.Music;
import com.runing.example.mvpmusicplayer.data.bean.MusicState;
import com.runing.example.mvpmusicplayer.service.MusicService;

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
public interface MainContract {

    /**
     * 主页视图
     */
    interface View extends BaseView<Presenter> {

        String ACTION_START_KEY = "START_ACTION";
        /**
         * 进入Search界面
         */
        int ACTION_START_SEARCH = 0;

        /**
         * 进入详细接口
         */
        int ACTION_START_DETAIL = 1;

        /**
         * 显示音乐列表
         *
         * @param musicList 音乐数据
         */
        void showMusicList(List<Music> musicList);

        /**
         * 还原状态
         *
         * @param state 当前音乐状态
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
         * 允许响应
         */
        void canAction();

    }

    /**
     * 主页Presenter
     */
    interface Presenter extends BasePresenter {
        /**
         * 设置监听
         *
         * @param onProgressListener 进度
         */
        void setOnProgressListener(MusicService.OnProgressListener onProgressListener);

        /**
         * 播放
         */
        void playMusic();

        /**
         * 下一曲
         */
        void nextMusic();

        /**
         * 暂停
         */
        void pauseMusic();

        /**
         * 退出应用
         */
        void exitApp();

        /**
         * 回收
         */
        void recycleUi();

        /**
         * 播放指定音乐
         *
         * @param id 音乐id
         */
        void playSpecified(int id);

        /**
         * 进入搜索
         */
        void enterSearch();

        /**
         * 进入详细页面
         */
        void enterDetail(@Nullable android.view.View image);

        /**
         * 处理返回结果
         *
         * @param requestCode 请求码
         * @param resultCode  结果码
         * @param data        意图
         */
        void handBackResult(int requestCode, int resultCode, Intent data);
    }

}
