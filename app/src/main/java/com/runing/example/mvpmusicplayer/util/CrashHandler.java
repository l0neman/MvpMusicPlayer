package com.runing.example.mvpmusicplayer.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import com.runing.example.mvpmusicplayer.service.MusicService;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

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
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = "CrashHandler";

    private static final String PATH = Environment.getExternalStorageDirectory()
            .getPath() + "/MvpMusicPlayer/log/";
    private static final String FILE_NAME = "crash";
    private static final String FILE_NAME_SUFFIX = ".trace";

    private Thread.UncaughtExceptionHandler mSystemExceptionHandler;
    private Context mContext;

    private static class Holder {
        @SuppressLint("StaticFieldLeak") //持有Application Context
        private static CrashHandler CRASH_HANDLER = new CrashHandler();
    }

    private CrashHandler() {
    }

    public void init(Context context) {
        mSystemExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        this.mContext = context.getApplicationContext();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public static CrashHandler getInstance() {
        return Holder.CRASH_HANDLER;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Toast.makeText(mContext, "程序出现异常!", Toast.LENGTH_SHORT).show();
        dumpExceptionToSDCard(ex);
        ex.printStackTrace();
        MusicService globalService = MusicService.getGlobalService();
        if (globalService != null) {
            globalService.onDestroy();
        }
        if (mSystemExceptionHandler != null) {
            mSystemExceptionHandler.uncaughtException(thread, ex);
        }
        Process.killProcess(Process.myPid());
    }

    /**
     * 将Crash信息写入SD卡
     *
     * @param throwable 异常
     */
    private void dumpExceptionToSDCard(Throwable throwable) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return;
        }
        File dir = new File(PATH);
        System.out.println(PATH);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                return;
            }
        }
        long current = System.currentTimeMillis();
        String time = SimpleDateFormat.getInstance().format(new Date(current));
        File file = new File(PATH + FILE_NAME + FILE_NAME_SUFFIX);

        try {
            PrintWriter pw = new PrintWriter(file);
            pw.println(time);
            writePhoneInfo(pw);
            pw.println();
            throwable.printStackTrace(pw);
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "dump crash info failed!");
        }
    }

    private void writePhoneInfo(PrintWriter pw) throws PackageManager.NameNotFoundException {
        PackageManager pm = mContext.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(),
                PackageManager.GET_ACTIVITIES);
        //应用版本
        pw.print("App version: ");
        pw.print(pi.versionName);
        pw.print('_');
        pw.println(pi.versionCode);
        //系统版本
        pw.print("Os Version: ");
        pw.print(Build.VERSION.RELEASE);
        pw.print("_");
        pw.println(Build.VERSION.SDK_INT);
        //制造商
        pw.print("Vendor: ");
        pw.println(Build.MANUFACTURER);
        //手机型号
        pw.print("Model: ");
        pw.println(Build.MODEL);
        //cpu架构
        pw.print("CPU ABI: ");
        pw.println(Build.CPU_ABI);
    }

}
