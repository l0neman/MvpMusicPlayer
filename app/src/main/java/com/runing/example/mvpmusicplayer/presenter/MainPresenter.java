package com.runing.example.mvpmusicplayer.presenter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.View;
import android.widget.TextView;

import com.runing.example.mvpmusicplayer.apdater.MusicAdapter;
import com.runing.example.mvpmusicplayer.contract.MainContract;
import com.runing.example.mvpmusicplayer.data.bean.Music;
import com.runing.example.mvpmusicplayer.data.bean.MusicState;
import com.runing.example.mvpmusicplayer.service.MusicService;
import com.runing.example.mvpmusicplayer.ui.DetailActivity;
import com.runing.example.mvpmusicplayer.ui.SearchActivity;
import com.runing.example.mvpmusicplayer.util.DataUtils;

import java.io.Serializable;
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
public final class MainPresenter implements MainContract.Presenter,
        MusicService.MusicCallBack {
    /**
     * 搜索界面请求码
     */
    private static final int SEARCH_REQUEST_CODE = 0;
    /**
     * 音乐列表
     */
    private List<Music> mMusics;
    /**
     * 主视图
     */
    private final MainContract.View mMainView;
    /**
     * 服务
     */
    private MusicService mMusicService;
    /**
     * 延迟启动搜索
     */
    private boolean mEnterSearchDelay = false;

    private MainPresenter(MainContract.View mMainView) {
        this.mMainView = mMainView;
    }

    /**
     * 服务连接
     */
    private final ServiceConnection mMusicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMusicService = ((MusicService.Binder) service).getService();
            mMusicService.addMusicCallBack(MainPresenter.this);
            mMainView.canAction();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public static MainPresenter newInstance(MainContract.View mMainView) {
        MainPresenter mainPresenter = new MainPresenter(mMainView);
        mMainView.setPresenter(mainPresenter);
        return mainPresenter;
    }

    @Override
    public void setOnProgressListener(MusicService.OnProgressListener onProgressListener) {
        mMusicService.setOnProgressListener(onProgressListener);
    }

    @Override
    public void playMusic() {
        mMusicService.play();
    }

    @Override
    public void nextMusic() {
        mMusicService.nextMusic();
    }

    @Override
    public void pauseMusic() {
        mMusicService.pause();
    }

    @Override
    public void playSpecified(int position) {
        int location = mMusicService.playSpecified(position);
        boolean isNoChange = location == MusicService.INDEX_DEFAULT;
        mMainView.updateMusic(MusicService.PlayState.SWITCH, isNoChange ?
                null : mMusics.get(location), position);
    }

    @Override
    public void exitApp() {
        ((Activity) mMainView).finish();
        if (mMusicService != null) {
            mMusicService.stopSelf();
        }
    }

    @Override
    public void recycleUi() {
        if (mMusicService != null) {
            ((Context) mMainView).unbindService(mMusicConnection);
            mMusicService.cancelProgressListener();
            mMusicService.removeMusicCallBack(this);
        }
    }

    @Override
    public void enterSearch() {
        if (isInitDataFinish()) {
            Activity activity = (Activity) this.mMainView;
            Intent intent = new Intent(activity, SearchActivity.class);
            intent.putExtra(SearchPresenter.MUSIC_LIST, (Serializable) mMusics);
            activity.startActivityForResult(intent, SEARCH_REQUEST_CODE);
        } else {
            mEnterSearchDelay = true;
        }
    }

    @Override
    public void enterDetail(View image) {
        Activity activity = (Activity) this.mMainView;
        Intent intent = new Intent(activity, DetailActivity.class);
        if (image == null) {
            activity.startActivity(intent);
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            //设置共享元素动画
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(activity,
                    image, "MusicImage");
            activity.startActivity(intent, options.toBundle());
        } else {
            activity.startActivity(intent);
        }
    }

    @Override
    public void handBackResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            switch (requestCode) {
                case SEARCH_REQUEST_CODE:
                    if (resultCode == SearchPresenter.RESULT_ID) {
                        long position = data.getLongExtra(SearchPresenter.ID_KEY, -1);
                        playSpecified(DataUtils.findIndex(mMusics, position));
                    }
                    break;
            }
        }
    }

    /**
     * 开启服务
     */
    private void startService() {
        Context mainActivity = (Context) mMainView;
        Intent intent = new Intent(mainActivity, MusicService.class);
        mainActivity.bindService(intent, mMusicConnection, Context.BIND_AUTO_CREATE);
        mainActivity.startService(intent);
    }

    /**
     * 加载数据完毕
     *
     * @return yes or no
     */
    private boolean isInitDataFinish() {
        return mMusics != null;
    }

    @Override
    public void start() {
        startService();
    }

    @Override
    public void onInitMusicData(List<Music> musics) {
        this.mMusics = musics;
        if (mEnterSearchDelay) {
            enterSearch();
            mEnterSearchDelay = false;
        }
        mMainView.showMusicList(musics);
        mMainView.restoreMusic(mMusicService.getCurrentMusicState());
    }

    @Override
    public void onChangeCurrMusic(MusicService.PlayState state, int position) {
        boolean isNoChange = position == MusicService.INDEX_DEFAULT;
        mMainView.updateMusic(state, isNoChange ? null : mMusics.get(position), position);
    }

    @Override
    public void onChangeMusicMode(MusicService.PlayMode mode) {
        //null
    }

}
