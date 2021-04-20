package com.bysj.znzapp.persenter.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.beagle.component.app.MvpManager;
import com.beagle.component.app.MvpPresenter;
import com.beagle.okhttp.OkHttpUtils;
import com.beagle.okhttp.callback.Response;
import com.beagle.okhttp.callback.ResponseCallBack;
import com.bysj.znzapp.activity.MainActivity;
import com.bysj.znzapp.bean.response.ConfigInfo;
import com.bysj.znzapp.bean.response.VersionInfo;
import com.bysj.znzapp.config.HttpConfig;
import com.bysj.znzapp.views.VersionUtil;
import com.thirdsdks.filedeal.FileDownUtil;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.MediaType;

/**
 * @Author: huowei
 * @CreateDate: 2021/3/22 18:27
 * @Describe:
 * @Version: 1.0.0
 */
public class MainActivityPresenter extends MvpPresenter {

    private MainActivity mainActivity;

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        mainActivity = (MainActivity) activity;
        getConfig();// 获取配置
    }

    @Override
    public void onActivityResumed(Activity activity) {
        getVersion(VersionUtil.getVersion(activity));
    }

    // 未读消息数
    public void navisCount() {
        OkHttpUtils.postString()
                .tag(mainActivity)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content("")
                .url(HttpConfig.getUnReadMessageCount).build().execute(new ResponseCallBack<Integer>(Integer.class) {
            @Override
            public void onResponse(Response<Integer> response, int i) {
                if (TextUtils.equals(response.getSuccess(), "1")) {
                    Integer navisCount = response.getData();
                    Activity activity = MvpManager.findActivity(MainActivityPresenter.this);
                    if (activity != null) {
                        MainActivity mainActivity = (MainActivity) activity;
                        mainActivity.setNavisCount(navisCount);
                    }
                }
            }
        });
    }

    // 获取配置
    public void getConfig() {
        OkHttpUtils.postString()
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .url(HttpConfig.getConfig)
                .tag(mainActivity)
                .content("")
                .build()
                .execute(new ResponseCallBack<ConfigInfo>(ConfigInfo.class) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                    }

                    @Override
                    public void onResponse(Response<ConfigInfo> response, int id) {
                        if (TextUtils.equals(response.getSuccess(), "1")) {
                            if (response.getData() != null) {
                                mainActivity.setConfig(response.getData());
                                HttpConfig.fileUploadServer = response.getData().getFileUploadServer();
                                HttpConfig.fileDownloadServer = response.getData().getFileDownloadServer();
                                FileDownUtil.fileServer = response.getData().getFileDownloadServer();
                            }
                        }
                    }
                });
    }

    // 获取版本
    public void getVersion(String clientVersion) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("clientVersion", clientVersion);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String appVersion = clientVersion;
        OkHttpUtils.postString()
                .addHeader("terminalType", "3")
                .addHeader("terminalVersion", android.os.Build.VERSION.RELEASE)
                .addHeader("appVersion", appVersion)
                .tag(mainActivity)
                .url(HttpConfig.checkVersion)
                .content(jsonObject.toString())
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new ResponseCallBack<VersionInfo>(VersionInfo.class) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                    }

                    @Override
                    public void onResponse(Response<VersionInfo> versionResponse, int i) {
                        if (TextUtils.equals(versionResponse.getSuccess(), "1")) {
                            VersionInfo version = versionResponse.getData();
                            if (version != null && mainActivity != null) {
                                mainActivity.checkVersion(version);
                            }
                        }
                    }
                });
    }

    /* 以下为空函数 */
    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
