package com.runing.example.mvpmusicplayer.ui;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RemoteViews;

import com.runing.example.mvpmusicplayer.R;
import com.runing.example.mvpmusicplayer.contract.MusicAWContract;
import com.runing.example.mvpmusicplayer.data.bean.Music;
import com.runing.example.mvpmusicplayer.data.bean.MusicState;
import com.runing.example.mvpmusicplayer.presenter.MusicAWPresenter;
import com.runing.example.mvpmusicplayer.service.MusicService;
import com.runing.example.mvpmusicplayer.util.BitmapUtils;

/**
 * Created by runing on 2016/5/18.
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
public class MusicAppWidgetProvider extends AppWidgetProvider implements MusicAWContract.View {

    private static MusicAWContract.Presenter mPresenter;

    /**
     * 专辑图片尺寸
     */
    private static int mMusicImgLength;
    /**
     * 是否可以响应
     */
    private static boolean mIsAction;
    /**
     * 远程View
     */
    private static RemoteViews mContentView;

    /**
     * 临时上下文
     */
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        this.mContext = context;

        final String action = intent.getAction();
        switch (action) {
            //播放
            case ACTION_PLAY:
                //检查服务
                if (mIsAction) {
                    mPresenter.playMusic();
                } else {
                    init(context);
                    mPresenter.requestPlay();
                }
                break;
            //搜索
            case ACTION_SEARCH:
                mPresenter.enterSearch();
                break;
            //详情
            case ACTION_DETAIL:
                mPresenter.enterDetail();
                break;
        }
        if (mIsAction) {
            switch (action) {
                //列表循环
                case ACTION_MODE_LOOP:
                    mPresenter.setPlayMode(MusicService.PlayMode.ONE);
                    break;
                //单曲循环
                case ACTION_MODE_ONE:
                    mPresenter.setPlayMode(MusicService.PlayMode.RANDOM);
                    break;
                //随机播放
                case ACTION_MODE_RANDOM:
                    mPresenter.setPlayMode(MusicService.PlayMode.LOOP);
                    break;
                //暂停
                case ACTION_PAUSE:
                    mPresenter.pauseMusic();
                    break;
                //上一曲
                case ACTION_PRE:
                    mPresenter.preMusic();
                    break;
                //下一曲
                case ACTION_NEXT:
                    mPresenter.nextMusic();
                    break;

                default:
            }
            updateMusicAW();
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        this.mContext = context;

//        init(context);

        init(context);
        mPresenter.requestRestore();

        setMusicImageOnClickListener();
        setPlayBtnOnClickListener();
        setPauseBtnOnClickListener();
        setPreBtnOnClickListener();
        setNextBtnOnClickListener();
        setSearchBtnOnClickListener();
        setLoopBtnOnClickListener();
        setOneBtnOnClickListener();
        setRandomBtnOnClickListener();

        updateMusicAW();
    }

    /**
     * 更新组件
     */
    private void updateMusicAW() {
        AppWidgetManager manager = AppWidgetManager.getInstance(mContext);
        manager.updateAppWidget(new ComponentName(mContext,
                MusicAppWidgetProvider.class), mContentView);
    }

    @Override
    public void onDisabled(Context context) {
        if (mPresenter != null) {
            mPresenter.recycleUi();
            mPresenter = null;
        }
    }

    /**
     * 初始化
     *
     * @param context 上下文
     */
    private void init(Context context) {
        initRemoteViews(context);
        initPresenter(context);
    }

    /**
     * 初始化远程view
     *
     * @param context 上下文
     */
    private void initRemoteViews(Context context) {
        if (mMusicImgLength == 0) {
            mMusicImgLength = context.getResources()
                    .getDimensionPixelSize(R.dimen.notification_height);
        }
        if (mContentView == null) {
            mContentView = new RemoteViews(context.getPackageName(),
                    R.layout.appwidget_music_layout);
        }
    }

    /**
     * 重新创建RemoteViews
     */
    private void produceRemoteViews() {
        mContentView = null;
        mContentView = new RemoteViews(mContext.getPackageName(),
                R.layout.appwidget_music_layout);
    }

    /**
     * 初始化presenter
     *
     * @param context 上下文
     */
    private void initPresenter(Context context) {
        if (mPresenter == null) {
            mPresenter = MusicAWPresenter.newInstance(context, this);
        }
    }

    @Override
    public void restoreMusic(MusicState state) {
        updateMusic(state.getState(), state.getMusic());
        updatePlayMode(state.getMode());
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
        updateMusicAW();
    }

    @Override
    public void updatePlayMode(@NonNull MusicService.PlayMode mode) {
        switch (mode) {
            case LOOP:
                showModeLoopBtn();
                break;
            case ONE:
                showModeOneBtn();
                break;
            case RANDOM:
                showModeRandomBtn();
                break;
        }
        updateMusicAW();
    }

    @Override
    public void canAction() {
        mIsAction = true;
    }

    @Override
    public void clearView() {
        produceRemoteViews();
        showPlayBtn();
        updateMusicAW();

        mIsAction = false;
        mContentView = null;
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
        updateMusicAW();
    }

    @Override
    public void setPresenter(MusicAWContract.Presenter presenter) {
//        this.mPresenter = presenter;
    }

    /**
     * 隐藏所有模式按钮
     */
    private void hideAllModeBtn() {
        mContentView.setViewVisibility(R.id.iv_mode_loop, View.GONE);
        mContentView.setViewVisibility(R.id.iv_mode_one, View.GONE);
        mContentView.setViewVisibility(R.id.iv_mode_random, View.GONE);
    }

    /**
     * 显示循环模式按钮
     */
    private void showModeLoopBtn() {
        hideAllModeBtn();
        mContentView.setViewVisibility(R.id.iv_mode_loop, View.VISIBLE);
    }

    /**
     * 显示单曲模式按钮
     */
    private void showModeOneBtn() {
        hideAllModeBtn();
        mContentView.setViewVisibility(R.id.iv_mode_one, View.VISIBLE);
    }

    /**
     * 显示随机模式按钮
     */
    private void showModeRandomBtn() {
        hideAllModeBtn();
        mContentView.setViewVisibility(R.id.iv_mode_random, View.VISIBLE);
    }

    /**
     * 设置专辑图片
     *
     * @param albumId 专辑id
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

    private void setMusicImageOnClickListener() {
        Intent intent = new Intent(ACTION_DETAIL);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mContentView.setOnClickPendingIntent(R.id.iv_image, pendingIntent);
    }

    /**
     * 设置播放按钮监听
     */
    private void setPlayBtnOnClickListener() {
        Intent intent = new Intent(ACTION_PLAY);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mContentView.setOnClickPendingIntent(R.id.iv_play, pendingIntent);
    }

    /**
     * 设置暂停按钮监听
     */
    private void setPauseBtnOnClickListener() {
        Intent intent = new Intent(ACTION_PAUSE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mContentView.setOnClickPendingIntent(R.id.iv_pause, pendingIntent);
    }

    /**
     * 设置上一曲按钮监听
     */
    private void setPreBtnOnClickListener() {
        Intent intent = new Intent(ACTION_PRE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mContentView.setOnClickPendingIntent(R.id.iv_pre, pendingIntent);
    }

    /**
     * 设置下一曲按钮监听
     */
    private void setNextBtnOnClickListener() {
        Intent intent = new Intent(ACTION_NEXT);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mContentView.setOnClickPendingIntent(R.id.iv_next, pendingIntent);
    }

    /**
     * 设置搜索按钮监听
     */
    private void setSearchBtnOnClickListener() {
        Intent intent = new Intent(ACTION_SEARCH);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mContentView.setOnClickPendingIntent(R.id.iv_search, pendingIntent);
    }

    /**
     * 设置列表循环按钮监听
     */
    private void setLoopBtnOnClickListener() {
        Intent intent = new Intent(ACTION_MODE_LOOP);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mContentView.setOnClickPendingIntent(R.id.iv_mode_loop, pendingIntent);
    }

    /**
     * 设置单曲循环按钮监听
     */
    private void setOneBtnOnClickListener() {
        Intent intent = new Intent(ACTION_MODE_ONE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mContentView.setOnClickPendingIntent(R.id.iv_mode_one, pendingIntent);
    }

    /**
     * 设置随机播放按钮监听
     */
    private void setRandomBtnOnClickListener() {
        Intent intent = new Intent(ACTION_MODE_RANDOM);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mContentView.setOnClickPendingIntent(R.id.iv_mode_random, pendingIntent);
    }
}
