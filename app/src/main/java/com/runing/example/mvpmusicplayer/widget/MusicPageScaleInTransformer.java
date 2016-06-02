package com.runing.example.mvpmusicplayer.widget;

import android.support.v4.view.ViewPager;
import android.view.View;

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
public class MusicPageScaleInTransformer implements ViewPager.PageTransformer {

    private static final float DEFAULT_SCALE = 0.6F;

    @Override
    public void transformPage(View page, float position) {
        if (position < -1) {
            page.setScaleX(DEFAULT_SCALE);
            page.setScaleY(DEFAULT_SCALE);
        } else if (position <= 1) {
            float mScale = DEFAULT_SCALE;
            if (position < 0) {
                float scale = DEFAULT_SCALE + (1 - mScale) * (1 + position);
                page.setScaleX(scale);
                page.setScaleY(scale);
            } else {
                float scale = DEFAULT_SCALE + (1 - mScale) * (1 - position);
                page.setScaleX(scale);
                page.setScaleY(scale);
            }
        } else {
            page.setScaleX(DEFAULT_SCALE);
            page.setScaleY(DEFAULT_SCALE);
        }
    }
}
