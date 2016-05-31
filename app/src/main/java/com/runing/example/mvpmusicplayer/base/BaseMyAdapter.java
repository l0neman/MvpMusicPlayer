package com.runing.example.mvpmusicplayer.base;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

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
 * <p>
 * 抽象ListViewAdapter
 *
 * @param <T>
 */
public abstract class BaseMyAdapter<T extends BaseMyAdapter.ViewHolder> extends BaseAdapter {

    private List<?> mData;

    public BaseMyAdapter(List<?> mData) {
        this.mData = mData;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 默认getView实现
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = onCreateViewHolder(parent, position);
            convertView = holder.itemView;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        @SuppressWarnings("unchecked") //T extends Holder
        T holder1 = (T) holder;
        onBindViewHolder(holder1, position);
        return holder.itemView;
    }

    /**
     * 初始化ViewHolder
     *
     * @param parent   父视图
     * @param position 位置
     * @return ViewHolder
     */
    protected abstract T onCreateViewHolder(ViewGroup parent, int position);

    /**
     * 关联数据
     *
     * @param holder   View缓存
     * @param position 位置
     */
    protected abstract void onBindViewHolder(T holder, int position);

    /**
     * 匹配的ViewHolder
     */
    public static class ViewHolder {
        private View itemView;

        public View getItemView() {
            return itemView;
        }

        public ViewHolder(@NonNull View itemView) {
            this.itemView = itemView;
        }
    }
}
