package com.runing.example.mvpmusicplayer.data.biz;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.SparseArray;

import com.runing.example.mvpmusicplayer.data.bean.Music;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
public class MusicGet {

    private ScanCallBack mScanCallBack;
    /**
     * 音乐数据
     */
    private List<Music> musics = new ArrayList<>();

    /**
     * 线程池
     */
    private ExecutorService threadPool = Executors.newSingleThreadExecutor();

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            System.out.println(Thread.currentThread().getName());
            //关闭线程池
            threadPool.shutdown();
            mScanCallBack.onOverScanned(musics);
            return true;
        }
    });

    /**
     * 数据回调
     * 主线程
     */
    public interface ScanCallBack {
        void onOverScanned(List<Music> musics);
    }

    /**
     * 获取本地音乐数据
     *
     * @param resolver      查询
     * @param mScanCallBack 回调
     */
    public void getMusicList(@NonNull final ContentResolver resolver,
                             @NonNull final ScanCallBack mScanCallBack) {
        this.mScanCallBack = mScanCallBack;
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        //音乐id
                        long id = cursor.getLong(cursor
                                .getColumnIndex(MediaStore.Audio.Media._ID));
                        //音乐标题
                        String title = cursor.getString((cursor
                                .getColumnIndex(MediaStore.Audio.Media.TITLE)));
                        //艺术家
                        String artist = cursor.getString(cursor
                                .getColumnIndex(MediaStore.Audio.Media.ARTIST));
                        //时长
                        long duration = cursor.getLong(cursor
                                .getColumnIndex(MediaStore.Audio.Media.DURATION));
                        //大小
                        long size = cursor.getLong(cursor
                                .getColumnIndex(MediaStore.Audio.Media.SIZE));
                        //文件路径
                        String url = cursor.getString(cursor
                                .getColumnIndex(MediaStore.Audio.Media.DATA));
                        //音乐专辑
                        String album = cursor.getString(cursor
                                .getColumnIndex(MediaStore.Audio.Media.ALBUM));
                        //专辑id
                        long album_id = cursor.getLong(cursor
                                .getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                        //是否是音乐
                        boolean isMusic = cursor.getInt(cursor
                                .getColumnIndex(MediaStore.Audio.Media.IS_MUSIC)) != 0;
                        //大于一分钟
                        if (isMusic && duration >= 60000) {
                            Music music = new Music();
                            music.setId(id);
                            music.setTitle(title);
                            music.setArtist(artist);
                            music.setDuration(duration);
                            music.setSize(size);
                            music.setUrl(url);
                            music.setAlbum(album);
                            music.setAlbum_id(album_id);
                            music.setAlbumImage(getAlbumArt(resolver, album_id));
                            musics.add(music);
                        }
                    }
                    cursor.close();
                    mHandler.sendEmptyMessage(0);
                }
            }
        });
    }

    /**
     * 获取专辑图片
     */
    private String getAlbumArt(final ContentResolver contentResolver, long album_id) {
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[]{"album_art"};
        Cursor cur = contentResolver.query(
                Uri.parse(mUriAlbums + "/" + Long.toString(album_id)),
                projection, null, null, null);
        String album_art = null;
        if (cur != null) {
            if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
                cur.moveToNext();
                album_art = cur.getString(0);
            }
            cur.close();
        }
        return album_art;
    }
}
