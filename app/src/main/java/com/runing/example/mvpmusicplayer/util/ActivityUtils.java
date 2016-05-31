package com.runing.example.mvpmusicplayer.util;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

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
public final class ActivityUtils {

    private ActivityUtils() {
        throw new AssertionError("no instance!");
    }

    /**
     * 添加Fragment
     *
     * @param manager  Fragment管理
     * @param fragment fragment实例
     * @param viewId
     */
    public static void addFragment(@NonNull FragmentManager manager,
                                   @NonNull Fragment fragment, int viewId) {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(viewId, fragment);
        transaction.addToBackStack("");
        transaction.commit();
    }

    /**
     * 移除
     * @param manager
     * @param viewId
     */
    public static void removeFragment(@NonNull FragmentManager manager, int viewId) {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.remove(manager.findFragmentById(viewId));
        transaction.commit();
    }

}
