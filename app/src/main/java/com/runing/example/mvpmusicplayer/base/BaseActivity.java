package com.runing.example.mvpmusicplayer.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by runing on 2016/5/13.
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
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onBeforeCreateView(savedInstanceState);
        setContentView(onContentViewId());
        onAfterCreateView(savedInstanceState);
    }

    protected void onBeforeCreateView(Bundle savedInstanceState) {
    }

    protected void onAfterCreateView(Bundle savedInstanceState) {
    }

    @SuppressWarnings("unchecked") //一定是View类型
    protected <T extends View> T findCaseViewById(int id) {
        return (T) findViewById(id);
    }

    /**
     * @return contentView id
     */
    protected abstract int onContentViewId();

}
