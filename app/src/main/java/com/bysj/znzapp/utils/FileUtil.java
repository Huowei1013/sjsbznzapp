package com.bysj.znzapp.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.math.BigDecimal;

/**
 * @Author: huowei
 * @CreateDate: 2021/4/11 11:58
 * @Describe:
 * @Version: 1.0.0
 */

public class FileUtil {

    /**
     * 检查是否存在SD卡
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 内部存储文件夹大小
     */
    public static String fileDirSize(Context context) throws Exception {
        long fileDirSize = getFolderSize(context.getFilesDir());
        return getFormatSize(fileDirSize);
    }

    /**
     * 清除缓存
     */
    public static String cacheSize(Context context) throws Exception {
        long cacheSize = getFolderSize(context.getCacheDir());
        if (hasSdcard()) {
            cacheSize += getFolderSize(context.getExternalCacheDir());
        }
        return getFormatSize(cacheSize);
    }

    public static void clearAllCache(Context context) {
        deleteDir(context.getCacheDir());
        deleteDir(context.getFilesDir());
        if (hasSdcard()) {
            //getExternalStorageState() 获取SD卡
            deleteDir(context.getExternalCacheDir());
        }
    }

    private static void deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            File[] fileList = dir.listFiles();
            if (fileList != null && fileList.length > 0) {
                for (File file : fileList) {
                    deleteDir(file);
                }
            }
        }
        if (dir != null && dir.isFile()) {
            dir.delete();
        }
    }

    public static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            if (file == null) {
            } else {
                if (file.isFile()) {
                    size = file.length();
                } else if (file.isDirectory()) {
                    File[] fileList = file.listFiles();
                    for (File tempFile : fileList) {
                        // 如果下面还有文件
                        if (tempFile.isDirectory()) {
                            size = size + getFolderSize(tempFile);
                        } else {
                            size = size + tempFile.length();
                        }
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 格式化单位
     *
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
//            return size + "Byte";
            return "0K";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }

}
