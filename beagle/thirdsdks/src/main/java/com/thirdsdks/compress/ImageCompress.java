package com.thirdsdks.compress;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.File;
import java.util.List;

import id.zelory.compressor.Compressor;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zhaotao on 2020/12/30 16:38.
 * 图片压缩
 */

public class ImageCompress {
    /**
     * 异步压缩
     *
     * @param context
     * @param width     显示图片宽
     * @param height    显示图片高
     * @param imageFile
     */
    public static void asynCompressToFile(Context context, int width, int height, File imageFile, final CompressFile compressFile) {
        new Compressor(context).setMaxWidth(400).setMaxHeight(600)
                .setQuality(85)
                .compressToFileAsFlowable(imageFile)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(File file) {
                        compressFile.accept(file);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    /**
     * 异步压缩
     *
     * @param context
     * @param width
     * @param height
     * @param fileList
     * @param compressFile
     */
    public void asynCompressToFiles(final Context context, final int width, final int height, final List<File> fileList, CompressFile compressFile) {
        if (fileList != null && !fileList.isEmpty()) {
            for (File file : fileList) {
                asynCompressToFile(context, width, height, file, compressFile);
            }
        }
    }

    public static void asynCompressToFile(Context context, File imageFile, final CompressFile compressFile) {
        if (screenWidth == 0) {
            screenWidth = getScreenWidth(context);
        }
        if (screenHeight == 0) {
            screenHeight = getScreenHeight(context);
        }
        //宽高默认为屏幕的宽高
        asynCompressToFile(context, screenWidth, screenHeight, imageFile, compressFile);

    }

    public void asynCompressToFiles(final Context context, final List<File> fileList, CompressFile compressFile) {
        if (screenWidth == 0) {
            screenWidth = getScreenWidth(context);
        }
        if (screenHeight == 0) {
            screenHeight = getScreenHeight(context);
        }
        //宽高默认为屏幕的宽高
        asynCompressToFiles(context, screenWidth, screenHeight, fileList, compressFile);
    }

    public interface CompressFile {
        void accept(File file);
    }

    private static int screenWidth;

    private static int screenHeight;

    private static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    private static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

}
