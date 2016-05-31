package com.runing.example.mvpmusicplayer.ui;

import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.runing.example.mvpmusicplayer.R;
import com.runing.example.mvpmusicplayer.base.BaseActivity;
import com.runing.example.mvpmusicplayer.contract.SearchContract;
import com.runing.example.mvpmusicplayer.data.bean.Music;
import com.runing.example.mvpmusicplayer.presenter.SearchPresenter;

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
public class SearchActivity extends BaseActivity implements SearchContract.View
        , SearchView.OnQueryTextListener {

    private SearchContract.Presenter mPresenter;

    private SearchView mSearchView;

    private List<Music> mMusics;

    private ListView mSearchList;

    @Override
    protected void onBeforeCreateView(Bundle savedInstanceState) {
        initPresenter();
    }

    private void initPresenter() {
        SearchPresenter.newInstance(this);
    }

    @Override
    protected int onContentViewId() {
        return R.layout.activity_search;
    }

    @Override
    protected void onAfterCreateView(Bundle savedInstanceState) {
        initToolBar();
        mPresenter.handData(getIntent());

        mSearchList = findCaseViewById(R.id.lv_search_list);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.start();
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_search);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        initSearchView(item);
        return true;
    }

    private void initSearchView(MenuItem item) {
        mSearchView = (SearchView) item.getActionView();
        mSearchView.setIconified(false);
        mSearchView.setQueryHint(getResources().getText(R.string.search_hint));
        mSearchView.setOnQueryTextListener(this);
    }

    @Override
    public void showSearchList(final BaseAdapter adapter) {
        mSearchList.setAdapter(adapter);
        mSearchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Music music = (Music) adapter.getItem(position);
                mPresenter.handListSelect(music.getId());
            }
        });
    }

    @Override
    public void setPresenter(SearchContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mPresenter.startSearch(newText);
        return true;
    }
}
