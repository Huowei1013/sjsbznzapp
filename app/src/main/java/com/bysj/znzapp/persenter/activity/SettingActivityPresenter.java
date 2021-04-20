package com.bysj.znzapp.persenter.activity;

import android.app.Activity;
import android.os.Bundle;

import com.beagle.component.app.MvpPresenter;
import com.beagle.okhttp.OkHttpUtils;
import com.beagle.okhttp.callback.StringCallback;
import com.bysj.znzapp.activity.SettingActivity;
import com.bysj.znzapp.config.HttpConfig;
import com.bysj.znzapp.utils.SharedPreferencesUtil;
import com.thirdsdks.filedeal.ToastUtil;

import okhttp3.Call;

/**
 * @Author: huowei
 * @CreateDate: 2021/4/15 13:38
 * @Describe:
 * @Version: 1.0.0
 */
public class SettingActivityPresenter extends MvpPresenter {

    private SettingActivity settingActivity;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        settingActivity = (SettingActivity) activity;
    }

    // 登出
    public void logout() {
        settingActivity.showMyDialog();
        OkHttpUtils.get()
                .url(HttpConfig.logOut)
                .tag(settingActivity)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        settingActivity.closeMyDialog();
                        ToastUtil.showToast(settingActivity, "退出失败");
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        settingActivity.closeMyDialog();
                        SharedPreferencesUtil.putString(settingActivity, "password", "");
                        SharedPreferencesUtil.putString(settingActivity, "zz2zx", "");
                        settingActivity.exitToAcitivty();
                    }
                });
    }

    /* 以下为空函数 */
    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
