package com.runing.example.mvpmusicplayer;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.runing.example.mvpmusicplayer.util.CrashHandler;

import java.util.Stack;

/**
 * Created by runing on 2016/6/4.
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

public class MMPApplication extends Application implements Application.ActivityLifecycleCallbacks {
    /**
     * activity栈
     */
    private Stack<Activity> mActivityStack = new Stack<>();
    /**
     * 单例
     */
    private static MMPApplication mMMApplication;

    public static MMPApplication getInstance() {
        return mMMApplication;
    }

    @Override
    public void onCreate() {
        mMMApplication = this;
        CrashHandler handler = CrashHandler.getInstance();
        handler.init(this);

        registerActivityLifecycleCallbacks(this);
    }

    /**
     * 退出所有activity
     */
    public void exitAllActivity() {
        unregisterActivityLifecycleCallbacks(this);
        while (!mActivityStack.isEmpty()) {
            Activity activity = mActivityStack.pop();
            if (activity != null) {
                activity.finish();
            }
        }
        mActivityStack.clear();
        registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (activity != null) {
            mActivityStack.add(activity);
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (activity != null) {
            mActivityStack.remove(activity);
        }
    }
}
