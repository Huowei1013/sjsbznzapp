package com.thirdsdks.filedeal;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by zhaotao on 2020/12/30 19:04.
 * 下载工具类
 */

public class FileDownUtil {

    private String fileDir = "";
    private String fileName = "";
    private String url = "";
    private static OkHttpClient downloadClient;
    private Request request;
    private OnCompleteListener mListener;
    private SimpleResponse mSimpleResponse;
    private Context context;
    public static String fileServer;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                // 失败
                if (mListener != null) mListener.onFailed();
            } else if (msg.what == 1) {
                // 成功
                if (mListener != null) mListener.onSuccess(new File(fileDir, fileName));
                if (mSimpleResponse != null)
                    mSimpleResponse.onResponse(new File(fileDir, fileName));
            }
        }
    };

    public FileDownUtil(Context c) {
        this.context = c;
        if (downloadClient == null) downloadClient = new OkHttpClient();
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            fileDir = c.getExternalFilesDir(null).getAbsolutePath();
        } else {
            fileDir = c.getFilesDir().getAbsolutePath();
        }
    }

    /**
     * 设置文件路径
     * <p></p>
     *
     * @param url string
     * @return this
     */
    public FileDownUtil url(String url) {
        this.url = url;
        return this;
    }

    /**
     * 设置缓存存储
     * <p></p>
     *
     * @return this
     */
    public FileDownUtil cacheDir() {
        this.fileDir = this.context.getCacheDir().getAbsolutePath();
        return this;
    }

    /**
     * 设置文件存储路径
     * <p></p>
     *
     * @param dir string
     * @return this
     */
    public FileDownUtil fileDir(String dir) {
        this.fileDir = dir;
        return this;
    }

    /**
     * 设置文件名
     *
     * @param fileName name
     * @return this
     */
    public FileDownUtil fileName(String fileName) {
//        fileName = removeDot(fileName);
        this.fileName = fileName;
        return this;
    }

    /**
     * 设置监听
     * <p></p>
     *
     * @param l listener
     * @return this
     */
    public FileDownUtil listener(OnCompleteListener l) {
        this.mListener = l;
        return this;
    }

    /**
     * 设置Cookie
     * <p></p>
     *
     * @param app context
     * @return this
     */
    public FileDownUtil addCookies(Application app) {
        HashSet preferences = (HashSet) app.getSharedPreferences("cookies", 0).getStringSet("cookie", new HashSet());
        Iterator var6 = preferences.iterator();
        Request.Builder builder = new Request.Builder();
        while (var6.hasNext()) {
            String cookie = (String) var6.next();
            if (!cookie.equals("")) {
                builder.addHeader("Cookie", cookie.substring(0, cookie.indexOf(";")));
            }
        }
        request = builder.url(url).build();
        return this;
    }

    /**
     * 文件下载
     *
     * @return this
     */
    public FileDownUtil download() {
        if (TextUtils.isEmpty(fileName) || TextUtils.isEmpty(url)) {
        }
        File file = new File(fileDir, fileName);
        if (!file.exists()) {
            if (request == null) request = new Request.Builder().url(url).build();
            downloadClient.newCall(request).enqueue(mCallback);
        } else {
            mHandler.sendEmptyMessage(1);
        }
        return this;
    }

    /**
     * 文件下载
     *
     * @param response 简单监听
     */
    public void download(SimpleResponse response) {
        this.mSimpleResponse = response;
        if (TextUtils.isEmpty(fileName) || TextUtils.isEmpty(url))
            return;
        File file = new File(fileDir, fileName);
        if (!file.exists()) {
            if (!url.contains("http")) {
                if (TextUtils.isEmpty(fileServer)) {
                    return;
                } else {
                    url = fileServer + url;
                }
            }
            if (request == null) request = new Request.Builder().url(url).build();
            downloadClient.newCall(request).enqueue(mCallback);
        } else {
            mHandler.sendEmptyMessage(1);
        }
    }

    /**
     * 文件下载
     *
     * @param listener 成功失败监听
     */
    public void download(OnCompleteListener listener) {
        this.mListener = listener;
        if (TextUtils.isEmpty(fileName) || TextUtils.isEmpty(url))
            return;
        File file = new File(fileDir, fileName);
        if (!file.exists()) {
            if (request == null) request = new Request.Builder().url(url).build();
            downloadClient.newCall(request).enqueue(mCallback);
        } else {
            mHandler.sendEmptyMessage(1);
        }
    }

    /**
     * 监听
     */
    public interface OnCompleteListener {
        void onSuccess(File resource);

        void onFailed();
    }

    public interface SimpleResponse {
        void onResponse(File resource);
    }

    /**
     * okHttp 回掉
     */
    private Callback mCallback = new Callback() {

        @Override
        public void onFailure(Call call, IOException e) {
            mHandler.sendEmptyMessage(0);
            e.printStackTrace();
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            InputStream is = null;
            byte[] buf = new byte[2048];
            int len = 0;
            FileOutputStream fos = null;
            try {
                is = response.body().byteStream();
                long total = response.body().contentLength();
                File file = new File(fileDir, fileName);
                fos = new FileOutputStream(file);
                long sum = 0;
                while ((len = is.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                    sum += len;
                    int progress = (int) (sum * 1.0f / total * 100);
                    Message msg = Message.obtain();
                    msg.what = 2;
                    msg.arg1 = progress;
                    mHandler.sendMessage(msg);
                }
                fos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null)
                        is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (fos != null)
                        fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mHandler.sendEmptyMessage(1);
            }
        }
    };
}
