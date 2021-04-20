package com.thirdsdks.map.track.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by zhaotao on 2021/01/05 18:13.
 */

public class NetUtil {

    /**
     * 检测网络状态是否联通
     */
    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (null != info && info.isConnected() && info.isAvailable()) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
