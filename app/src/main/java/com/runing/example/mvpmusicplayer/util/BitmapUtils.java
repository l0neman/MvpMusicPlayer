package com.runing.example.mvpmusicplayer.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import com.runing.example.mvpmusicplayer.R;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by runing on 2016/5/16.
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
public class BitmapUtils {

    private BitmapUtils() {
        throw new AssertionError("no instance!");
    }

    /**
     * 从file加载优化后的Bitmap
     */
    public static Bitmap decodeSampledBitmapFromFile(
            String path, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        //获取推荐采样值
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        //返回采样优化后的图像
        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * 从res加载优化后的Bitmap
     */
    public static Bitmap decodeSampledBitmapFromResource(
            Resources res, int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        //获取推荐采样值
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        //返回采样优化后的图像
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * 获取推荐采样值
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,
                                             int reqHeight) {
        //原始图像信息
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int haltWidth = width / 2;
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (haltWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * 从文件描述符加载位图
     *
     * @param context   上下文
     * @param album_id  专辑id
     * @param reqWidth  请求宽度
     * @param reqHeight 请求高度
     * @return 位图
     */
    public static Bitmap decodeSampledBitmapFromFD(Context context, long album_id,
                                                   int reqWidth, int reqHeight) {
        ContentResolver resolver = context.getContentResolver();
        Uri uri = ContentUris.withAppendedId(Uri
                .parse("content://media/external/audio/albumart"), album_id);
        if (uri != null) {
            ParcelFileDescriptor fileDescriptor = null;
            try {
                final BitmapFactory.Options options = new BitmapFactory.Options();
                fileDescriptor = resolver.openFileDescriptor(uri, "r");
                if (fileDescriptor != null) {
                    options.inJustDecodeBounds = true;
                    FileDescriptor fd = fileDescriptor.getFileDescriptor();
                    BitmapFactory.decodeFileDescriptor(fd, null, options);
                    //获取推荐采样值
                    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
                    options.inJustDecodeBounds = false;
                    //返回采样优化后的图像
                    return BitmapFactory.decodeFileDescriptor(fd, null, options);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (fileDescriptor != null) {
                    try {
                        fileDescriptor.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return getDefaultImage(context);
    }

    private static Bitmap getDefaultImage(Context context) {
        return BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
    }
}
