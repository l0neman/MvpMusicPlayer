package com.runing.example.mvpmusicplayer.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.runing.example.mvpmusicplayer.R;
import com.runing.example.mvpmusicplayer.apdater.SearchAdapter;
import com.runing.example.mvpmusicplayer.base.BaseActivity;
import com.runing.example.mvpmusicplayer.base.BaseRVAdapter;
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

    private RecyclerView mSearchList;

    private SearchAdapter mSearchAdapter;

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
        initRecycleView();
        mPresenter.handData(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.start();
    }

    private void initRecycleView() {
        mSearchList = findCaseViewById(R.id.lv_search_list);
        mSearchList.setLayoutManager(new LinearLayoutManager(this));
        mSearchList.setItemAnimator(new DefaultItemAnimator());
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
        SearchView mSearchView = (SearchView) item.getActionView();

        SearchView.SearchAutoComplete textView =
                (SearchView.SearchAutoComplete) mSearchView.findViewById(R.id.search_src_text);
        textView.setTextColor(Color.WHITE);

        mSearchView.setIconified(false);
        mSearchView.setQueryHint(getResources().getText(R.string.search_hint));
        mSearchView.setOnQueryTextListener(this);
    }

    @Override
    public void showSearchList(List<Music> musicList) {
        mSearchAdapter = new SearchAdapter(this, musicList);
        mSearchList.setAdapter(mSearchAdapter);
        mSearchAdapter.setOnItemOnClickListener(new BaseRVAdapter.OnItemOnClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Music music = (Music) mSearchAdapter.getItemData(position);
                mPresenter.handListSelect(music.getId());
            }
        });
    }

    @Override
    public void refreshList() {
        mSearchAdapter.notifyDataSetChanged();
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
