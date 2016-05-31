package com.runing.example.mvpmusicplayer.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RemoteViews;

import com.runing.example.mvpmusicplayer.R;
import com.runing.example.mvpmusicplayer.contract.NotifyContract;
import com.runing.example.mvpmusicplayer.data.bean.Music;
import com.runing.example.mvpmusicplayer.service.MusicService;
import com.runing.example.mvpmusicplayer.util.BitmapUtils;

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
public class MusicNotification implements NotifyContract.View {

    private Context mContext;
    /**
     * 通知
     */
    private Notification mMusicNotification;

//    private NotifyContract.Presenter mPresenter;
    /**
     * 通知上的远程View
     */
    private RemoteViews mContentView;
    /**
     * 专辑图片尺寸
     */
    private int mMusicImgLength;
    /**
     * 通知管理
     */
    NotificationManager mNotificationManager;

    public MusicNotification(Context context) {
        this.mContext = context;
    }

    private void init() {
        initRemoteViews();
        initNotification();
    }

    /**
     * 初始化远程View
     */
    private void initRemoteViews() {
        mContentView = new RemoteViews(mContext.getPackageName(),
                R.layout.notification_music_layout);
        mMusicImgLength = mContext.getResources().getDimensionPixelSize(R.dimen.notification_height);

        setPlayBtnOnClickListener();
        setPauseBtnOnClickListener();
        setPreBtnOnClickListener();
        setNextBtnOnClickListener();
        setCloseBtnOnClickListener();
    }

    /**
     * 从新产生RemoteView
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void produceRemoteViews() {
        mContentView = null;
        mContentView = new RemoteViews(mContext.getPackageName(),
                R.layout.notification_music_layout);
        mMusicNotification.bigContentView = mContentView;
    }

    /**
     * 设置播放按钮监听
     */
    private void setPlayBtnOnClickListener() {
        Intent intent = new Intent(NotifyContract.Presenter.ACTION_PLAY);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mContentView.setOnClickPendingIntent(R.id.iv_play, pendingIntent);
    }

    /**
     * 设置暂停按钮监听
     */
    private void setPauseBtnOnClickListener() {
        Intent intent = new Intent(NotifyContract.Presenter.ACTION_PAUSE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mContentView.setOnClickPendingIntent(R.id.iv_pause, pendingIntent);
    }

    /**
     * 设置上一曲按钮监听
     */
    private void setPreBtnOnClickListener() {
        Intent intent = new Intent(NotifyContract.Presenter.ACTION_PRE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mContentView.setOnClickPendingIntent(R.id.iv_pre, pendingIntent);
    }

    /**
     * 设置下一曲按钮监听
     */
    private void setNextBtnOnClickListener() {
        Intent intent = new Intent(NotifyContract.Presenter.ACTION_NEXT);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mContentView.setOnClickPendingIntent(R.id.iv_next, pendingIntent);
    }

    /**
     * 设置关闭按钮监听
     */
    private void setCloseBtnOnClickListener() {
        Intent intent = new Intent(NotifyContract.Presenter.ACTION_CLOSE);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mContentView.setOnClickPendingIntent(R.id.iv_close, pendingIntent);
    }

    /**
     * 初始化通知显示
     */
    @SuppressLint("NewApi") // API >= 16
    private void initNotification() {

        Intent intent = new Intent(mContext, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mMusicNotification = new Notification.Builder(mContext)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(false)
                .setContentIntent(pendingIntent)
                .build();
        mMusicNotification.flags = Notification.FLAG_NO_CLEAR;
        mMusicNotification.bigContentView = mContentView;

        mNotificationManager = (NotificationManager) mContext.getSystemService(
                Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mMusicNotification);
    }

    @Override
    public void setPresenter(NotifyContract.Presenter presenter) {
//        this.mPresenter = presenter;
    }

    @Override
    public void firstShow() {
        init();
    }

    @Override
    public void updateMusic(@NonNull MusicService.PlayState state, @Nullable Music music) {
        produceRemoteViews();
        updateMusicView(music);
        switch (state) {
            case PLAY:
                showPauseBtn();
                break;
            case PAUSE:
                showPlayBtn();
                break;
            case SWITCH:
                showPauseBtn();
                break;
            case STOP:
                showPlayBtn();
                break;
        }
        mNotificationManager.notify(1, mMusicNotification);
    }

    @Override
    public void closeNotify() {
        mNotificationManager.cancel(1);
    }

    /**
     * 更新音乐相关ui
     *
     * @param music 音乐实例
     */
    private void updateMusicView(Music music) {
        if (music != null) {
            installMusicImage(music.getAlbum_id());
            setMusicNameViewText(music.getTitle());
            setMusicArtistViewText(music.getArtist());
        }
    }

    /**
     * 设置专辑图片
     */
    private void installMusicImage(final long albumId) {
        setMusicImageBitmap(BitmapUtils.decodeSampledBitmapFromFD(mContext,
                albumId, mMusicImgLength, mMusicImgLength));
    }

    /**
     * 设置音乐标题
     */
    private void setMusicNameViewText(String name) {
        mContentView.setTextViewText(R.id.tv_name, name);
    }

    /**
     * 设置艺术家
     */
    private void setMusicArtistViewText(String name) {
        mContentView.setTextViewText(R.id.tv_singer, name);
    }

    /**
     * 设置音乐图片
     */
    private void setMusicImageBitmap(Bitmap bitmap) {
        mContentView.setImageViewBitmap(R.id.iv_image, bitmap);
    }

    /**
     * 显示播放按钮
     */
    private void showPlayBtn() {
        mContentView.setViewVisibility(R.id.iv_pause, View.GONE);
        mContentView.setViewVisibility(R.id.iv_play, View.VISIBLE);
    }

    /**
     * 显示暂停按钮
     */
    private void showPauseBtn() {
        mContentView.setViewVisibility(R.id.iv_pause, View.VISIBLE);
        mContentView.setViewVisibility(R.id.iv_play, View.GONE);
    }

}
