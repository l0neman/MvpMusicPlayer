package com.runing.example.mvpmusicplayer.contract;

import android.content.Intent;
import android.os.Bundle;
import android.widget.BaseAdapter;

import com.runing.example.mvpmusicplayer.base.BasePresenter;
import com.runing.example.mvpmusicplayer.base.BaseView;

/**
 * Created by runing on 2016/5/14.
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
public interface SearchContract {

    interface View extends BaseView<Presenter> {

        /**
         * 显示搜索音乐列表
         *
         * @param adapter
         */
        void showSearchList(BaseAdapter adapter);

    }

    interface Presenter extends BasePresenter {
        /**
         * 处理数据
         *
         * @param data 数据
         */
        void handData(Intent data);

        /**
         * 开始搜索
         *
         * @param search 关键字
         */
        void startSearch(String search);

        /**
         * 处理选中
         *
         * @param position 位置
         */
        void handListSelect(long position);
    }

}
