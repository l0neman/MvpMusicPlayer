package com.runing.example.mvpmusicplayer.util;

import android.support.annotation.NonNull;

import com.runing.example.mvpmusicplayer.data.bean.Music;
import com.runing.example.mvpmusicplayer.service.MusicService;

import java.util.List;

/**
 * Created by runing on 2016/6/2.
 * <p>
 * This file is part of MvpMusicPlayer.
 * MvpMusicPlayer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * MvpMusicPlayer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with MvpMusicPlayer.  If not, see <http://www.gnu.org/licenses/>.
 */
public class DataUtils {

    private DataUtils() {
        throw new AssertionError("no instance!");
    }

    public static class Cloneable implements java.lang.Cloneable {
        @Override
        public Object clone() throws CloneNotSupportedException {
            return super.clone();
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
        return MusicService.INDEX_DEFAULT;
    }

    /**
     * 深拷贝集合
     *
     * @param aims   目标
     * @param source 源
     */
    @SuppressWarnings("unchecked") //T extends Cloneable
    public static <T extends Cloneable> void deepCopy(@NonNull List<? super T> aims,
                                                      @NonNull List<? extends T> source) {
        try {
            aims.clear();
            for (T music : source) {
                aims.add((T) music.clone());
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }
}
