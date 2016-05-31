package com.runing.example.mvpmusicplayer.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.runing.example.mvpmusicplayer.apdater.SearchAdapter;
import com.runing.example.mvpmusicplayer.contract.SearchContract;
import com.runing.example.mvpmusicplayer.data.bean.Music;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by runing on 2016/5/15.
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
public class SearchPresenter implements SearchContract.Presenter {
    /**
     * 获取音乐列表
     */
    public static final String MUSIC_LIST = "music_info";
    /**
     * 获取id的key
     */
    public static final String ID_KEY = "id_key";
    /**
     * 结果码
     */
    public static final int RESULT_ID = 0;

    private SearchContract.View mSearchView;
    /**
     * 适配器
     */
    private SearchAdapter mAdapter;
    /**
     * 需要检索的数据
     */
    private List<Music> mCopyMusics;
    /**
     * 源数据
     */
    private List<Music> mMusics;

    public SearchPresenter(SearchContract.View mSearchView) {
        this.mSearchView = mSearchView;
        mSearchView.setPresenter(this);
        mCopyMusics = new ArrayList<>();
    }

    public static SearchPresenter newInstance(SearchContract.View mSearchView) {
        return new SearchPresenter(mSearchView);
    }

    @Override
    @SuppressWarnings("unchecked") //已实现序列化
    public void handData(Intent data) {
        mMusics = (List<Music>) data.getSerializableExtra(MUSIC_LIST);
        deepCopy(mCopyMusics, mMusics);
        mAdapter = new SearchAdapter(((Context) mSearchView), mCopyMusics);
    }

    @Override
    public void startSearch(String search) {
        mCopyMusics.clear();
        if (search.equals("")) {
            deepCopy(mCopyMusics, mMusics);
        } else {
            for (Music music : mMusics) {
                String trim = search.trim();
                if (music.getTitle().contains(trim)
                        || music.getArtist().contains(trim)) {
                    mCopyMusics.add(music);
                }
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void handListSelect(long musicId) {
        Activity activity = (Activity) mSearchView;
        Intent data = new Intent();
        data.putExtra(ID_KEY, musicId);
        activity.setResult(RESULT_ID, data);
        activity.finish();
    }

    @Override
    public void start() {
        mSearchView.showSearchList(mAdapter);
    }

    /**
     * 深拷贝Music集合
     *
     * @param aims   目标
     * @param source 源
     */
    private void deepCopy(@NonNull List<Music> aims, @NonNull List<Music> source) {
        aims.clear();
        for (Music music : source) {
            aims.add(music.clone());
        }
    }
}
