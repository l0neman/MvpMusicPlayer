package com.runing.example.mvpmusicplayer.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by runing on 2016/6/1.
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
public abstract class BaseRVAdapter<T extends RecyclerView.ViewHolder> extends
        RecyclerView.Adapter<T> {
    /**
     * 数据
     */
    private List<?> mData;

    private OnItemOnClickListener mOnItemOnClickListener;

    /**
     * item点击事件
     *
     * @param onItemOnClickListener 监听
     */
    public void setOnItemOnClickListener(OnItemOnClickListener onItemOnClickListener) {
        this.mOnItemOnClickListener = onItemOnClickListener;
    }

    public BaseRVAdapter(List<?> mData) {
        this.mData = mData;
    }

    /**
     * item数据
     *
     * @param position 位置
     * @return object
     */
    public Object getItemData(int position) {
        return mData.get(position);
    }

    public interface OnItemOnClickListener {

        void onItemClick(View itemView, int position);
    }

    @Override
    public void onBindViewHolder(final T holder, int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemOnClickListener != null) {
                    mOnItemOnClickListener.onItemClick(v, holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
