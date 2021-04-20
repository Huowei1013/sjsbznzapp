package com.bysj.znzapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.TextUtils;

import com.beagle.okhttp.OkHttpUtils;
import com.beagle.okhttp.cookie.CookieJarImpl;
import com.beagle.okhttp.cookie.store.PersistentCookieStore;
import com.beagle.okhttp.cookie.store.SerializableHttpCookie;
import com.bysj.znzapp.config.HttpConfig;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;

import okhttp3.Cookie;

/**
 * @Author: huowei
 * @CreateDate: 2021/3/22 17:32
 * @Describe: Token管理
 * @Version: 1.0.0
 */
public class TokenUtils{
        // 设置token
        public static void setToken(Context context,String token) {
            boolean isExist = false;
            SharedPreferences cookiePrefs = context.getSharedPreferences("CookiePrefsFile", 0);
            Map<String, ?> prefsMap = cookiePrefs.getAll();
            for (Map.Entry<String, ?> entry : prefsMap.entrySet()) {
                if (((String) entry.getValue()) != null
                        && !((String) entry.getValue()).startsWith("cookie_")) {
                    String[] cookieNames = TextUtils.split((String) entry.getValue(), ",");
                    for (String name : cookieNames) {
                        String encodedCookie = cookiePrefs.getString("cookie_" + name, null);
                        if (encodedCookie != null) {
                            Cookie decodedCookie = decodeCookie(encodedCookie);
                            if (decodedCookie != null) {
                                if (TextUtils.equals(HttpConfig.bgToken, decodedCookie.name())) {// 存在，就覆盖旧的token
                                    Cookie cookie = new Cookie.Builder()
                                            .domain(decodedCookie.domain())
                                            .name(decodedCookie.name())
                                            .value(token)
                                            .path(decodedCookie.path())
                                            .build();
                                    try {
                                        CookieJarImpl cookieJarImpl = (CookieJarImpl) OkHttpUtils.getInstance().getOkHttpClient().cookieJar();
                                        PersistentCookieStore persistentCookieStore = (PersistentCookieStore) cookieJarImpl.getCookieStore();
                                        Uri uri = Uri.parse(HttpConfig.host);
                                        persistentCookieStore.add(uri.getHost(), cookie);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    isExist = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            if (!isExist) { // 不存在，就必须新生成
                try {
                    Uri uri = Uri.parse(HttpConfig.host);
                    Cookie cookie = new Cookie.Builder()
                            .domain(uri.getHost())
                            .name(HttpConfig.bgToken)
                            .value(token)
                            .path("/")
                            .build();
                    CookieJarImpl cookieJarImpl = (CookieJarImpl) OkHttpUtils.getInstance().getOkHttpClient().cookieJar();
                    PersistentCookieStore persistentCookieStore = (PersistentCookieStore) cookieJarImpl.getCookieStore();
                    persistentCookieStore.add(uri.getHost(), cookie);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private static Cookie decodeCookie(String cookieString) {
            byte[] bytes = hexStringToByteArray(cookieString);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            Cookie cookie = null;
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                cookie = ((SerializableHttpCookie) objectInputStream.readObject()).getCookie();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            return cookie;
        }

        private static byte[] hexStringToByteArray(String hexString) {
            int len = hexString.length();
            byte[] data = new byte[len / 2];
            for (int i = 0; i < len; i += 2) {
                data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i + 1), 16));
            }
            return data;
        }
}
