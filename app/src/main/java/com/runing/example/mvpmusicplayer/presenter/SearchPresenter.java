package com.runing.example.mvpmusicplayer.presenter;

import android.app.Activity;
import android.content.Intent;

import com.runing.example.mvpmusicplayer.contract.SearchContract;
import com.runing.example.mvpmusicplayer.data.bean.Music;
import com.runing.example.mvpmusicplayer.util.DataUtils;

import java.util.ArrayList;
import java.util.Collections;
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
    static final String MUSIC_LIST = "music_info";
    /**
     * 获取id的key
     */
    static final String ID_KEY = "id_key";
    /**
     * 结果码
     */
    static final int RESULT_ID = 0;

    private SearchContract.View mSearchView;
    /**
     * 需要检索的数据
     */
    private List<Music> mCopyMusics;
    /**
     * 源数据
     */
    private List<Music> mMusics = Collections.emptyList();

    private SearchPresenter(SearchContract.View mSearchView) {
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
        DataUtils.deepCopy(mCopyMusics, mMusics);
    }

    @Override
    public void startSearch(String search) {
        mCopyMusics.clear();
        if (search.equals("")) {
            DataUtils.deepCopy(mCopyMusics, mMusics);
        } else {
            for (Music music : mMusics) {
                String trim = search.trim();
                if (music.getTitle().contains(trim)
                        || music.getArtist().contains(trim)) {
                    mCopyMusics.add(music);
                }
            }
        }
        mSearchView.refreshList();
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
        mSearchView.showSearchList(mCopyMusics);
    }
}
