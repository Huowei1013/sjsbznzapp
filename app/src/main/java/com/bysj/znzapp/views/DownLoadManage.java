package com.bysj.znzapp.views;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import androidx.core.content.FileProvider;

import com.beagle.component.logger.LogCat;
import com.bysj.znzapp.BuildConfig;
import com.bysj.znzapp.base.BaseApp;
import com.bysj.znzapp.config.HttpConfig;
import com.thirdsdks.filedeal.ToastUtil;

import java.io.File;

/**
 * @Author: huowei
 * @CreateDate: 2021/3/22 18:48
 * @Describe: 下载相关
 * @Version: 1.0.0
 */
public class DownLoadManage {

    private static DownloadManager mDownloadManage;
    private static long mTaskId = 0;
    public static String downloadFilepath;
    private static boolean isDownApk = true; // 是否能下载，默认能下载

    public static DownloadManager getInstance(Context context) {
        if (mDownloadManage == null) {
            synchronized (DownLoadManage.class) {
                if (mDownloadManage == null) {
                    mDownloadManage = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                }
            }
        }
        return mDownloadManage;
    }

    public static long downFile(String url, String name, BroadcastReceiver receiver, Context context) {
        String rootUrl = HttpConfig.fileDownloadServer + url;

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(rootUrl));
        request.setAllowedOverRoaming(true);
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        String mimiString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(name));
        request.setMimeType(mimiString);
        request.setDestinationInExternalPublicDir(File.separator + "zhichengDownload" + File.separator, name);

        long mTaskId = getInstance(context).enqueue(request);
        context.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        return mTaskId;
    }

    public static void downApk(String url, String name, boolean anew) {
        if (isDownApk) {
            isDownApk = false;
            if (anew) {
                ToastUtil.showToast(BaseApp.getApp(), "文件被删除了，重新下载");
            } else {
                ToastUtil.showToast(BaseApp.getApp(), "通知栏查看下载进度");
            }
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setAllowedOverRoaming(true);
            request.setMimeType("application/vnd.android.package-archive");
            request.setDestinationInExternalPublicDir(File.separator + "zhichengDownload" + File.separator, name);

            mTaskId = getInstance(BaseApp.getApp()).enqueue(request);
            BaseApp.getApp().registerReceiver(broadcastReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        } else {
            ToastUtil.showToast(BaseApp.getApp(), "下载中，通知栏查看下载进度");
        }
    }

    private static BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(mTaskId);
            Cursor c = DownLoadManage.getInstance(context).query(query);
            if (c.moveToFirst()) {
                int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                switch (status) {
                    case DownloadManager.STATUS_PAUSED:
                        break;
                    case DownloadManager.STATUS_PENDING:
                        break;
                    case DownloadManager.STATUS_RUNNING:
                        break;
                    case DownloadManager.STATUS_SUCCESSFUL:
                        if (BaseApp.getApp() != null) ToastUtil.showToast(BaseApp.getApp(), "下载完成");
                        downloadFilepath = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                        if (downloadFilepath != null) {
                            install(downloadFilepath);
                            isDownApk = true;
                        }
                        break;
                    case DownloadManager.STATUS_FAILED:
                        isDownApk = true;
                        if (BaseApp.getApp() != null) {
                            ToastUtil.showToast(BaseApp.getApp(), "下载失败，请重新下载");
                        }
                        break;
                }
            }else { // 被用户删除了，会进入这里
                if (TextUtils.equals("android.intent.action.DOWNLOAD_COMPLETE", intent.getAction())){
                    isDownApk = true;
                    LogCat.e( "--------------------status-----------" + intent.getAction());
                }
            }
        }
    };

    public static void install(String path) {
        if (Build.VERSION.SDK_INT >= 24) {// 判读版本是否在7.0以上
            File file = new File(path);
            if (file.exists()) {
                Uri apkUri = FileProvider.getUriForFile(BaseApp.getApp(), BuildConfig.APPLICATION_ID + ".fileProvider", file);// 在AndroidManifest中的android:authorities值
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);// 添加这一句表示对目标应用临时授权该Uri所代表的文件
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                BaseApp.getApp().startActivity(intent);
            }
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            BaseApp.getApp().startActivity(intent);
        }
    }
}
