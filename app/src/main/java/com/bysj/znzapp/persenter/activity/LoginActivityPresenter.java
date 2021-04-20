package com.bysj.znzapp.persenter.activity;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.beagle.component.app.MvpManager;
import com.beagle.component.app.MvpPresenter;
import com.beagle.okhttp.OkHttpUtils;
import com.beagle.okhttp.callback.Response;
import com.beagle.okhttp.callback.ResponseCallBack;
import com.bysj.znzapp.activity.LoginActivity;
import com.bysj.znzapp.base.BaseApp;
import com.bysj.znzapp.bean.UserInfo;
import com.bysj.znzapp.config.HttpConfig;
import com.bysj.znzapp.data.UserInfoProvide;
import com.bysj.znzapp.utils.SharedPreferencesUtil;
import com.thirdsdks.filedeal.ToastUtil;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import org.json.JSONObject;

import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;

/**
 * @Author: huowei
 * @CreateDate: 2021/3/22 17:24
 * @Describe:
 * @Version: 1.0.0
 */
public class LoginActivityPresenter extends MvpPresenter implements PermissionListener {
    public static final String TAG = "LoginActivity";
    private LoginActivity loginActivity;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (activity instanceof LoginActivity) {
            loginActivity = (LoginActivity) activity;
            // 获取权限（存在onSucceed、onFailed方法）
            //TODO 将来初始化操作放到与权限放到引导页
            AndPermission.with(activity)
                    .requestCode(100)
                    .permission(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                    .callback(this)
                    .start();
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (loginActivity == null) {
            if (activity instanceof LoginActivity) {
                loginActivity = (LoginActivity) activity;
            }
        }
    }
    // 账号登陆
    public void loginByAccount(final String username, final String password) {
        BaseApp.getApp().clearCookie();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userName", username);
            jsonObject.put("passWord", password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (loginActivity == null) loginActivity = (LoginActivity) MvpManager.findActivity(LoginActivityPresenter.this);
        loginActivity.showMyDialog();
        OkHttpUtils.postString()
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .url(HttpConfig.login)
                .content(jsonObject.toString())
                .build()
                .execute(new ResponseCallBack<UserInfo>(UserInfo.class) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        e.printStackTrace();
                        loginActivity.closeMyDialog();
                    }

                    @Override
                    public void onResponse(Response<UserInfo> response, int id) {
                        if (TextUtils.equals(response.getSuccess(), "1")) {
                            UserInfoProvide.setUserInfo(response.getData());// 保存登陆信息到内存中
                            SharedPreferencesUtil.putString(loginActivity, "username", username);
                            SharedPreferencesUtil.putString(loginActivity, "password", password);
                            //极光注册
//                            PushControlService.startPushService(loginActivity, response.getData().getAccount());
                            loginActivity.goToMainActivity();
                        } else if (TextUtils.equals(response.getSuccess(), "0")) {
                            ToastUtil.showToast(loginActivity, response.getErrMsg());
                        }
                        loginActivity.closeMyDialog();
                    }
                });
    }

    // 使用验证码登陆
    public void loginByCode(final String phone, String code) {

    }

    // 其他应用授权登陆
//    public void getTicketToken(String ticket) {
//        if (loginActivity == null) loginActivity = (LoginActivity) MvpManager.findActivity(LoginActivityPresenter.this);
//        loginActivity.showMyDialog();
//        OkHttpUtils.get()
//                .url(HttpConfig.getTicketToken)
//                .addParams("ticket", ticket)
//                .build()
//                .execute(new ResponseCallBack<UserInfo>(UserInfo.class) {
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//                        super.onError(call, e, id);
//                        loginActivity.closeMyDialog();
//                    }
//
//                    @Override
//                    public void onResponse(Response<UserInfo> response, int id) {
//                        if (TextUtils.equals(response.getSuccess(), "1")) {
//                            UserInfoProvide.setUserInfo(response.getData());//保存登陆信息到内存中
//                            SharedPreferencesUtil.putString(loginActivity, "username", "");
//                            SharedPreferencesUtil.putString(loginActivity, "password", "");
//                            SharedPreferencesUtil.putString(loginActivity, "zz2zx", "");
//                            PushControlService.startPushService(loginActivity, response.getData().getAccount());
//                            loginActivity.goToMainActivity();
//                        } else if (TextUtils.equals(response.getSuccess(), "0")) {
//                            ToastUtil.showToast(loginActivity, response.getErrMsg());
//                        }
//                        loginActivity.closeMyDialog();
//                    }
//                });
//    }

    /* 以下为空函数 */
    @Override
    public void onActivityResumed(@NonNull Activity activity) {

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

    @Override
    public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
        Log.i(TAG,"the permission init is succeed!");
        //TODO 将来初始化操作放到与权限放到引导页
//        MeetingFunc.onAppCreate();
    }

    @Override
    public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
        Log.i(TAG,"the permission init is error!");
        AndPermission.defaultSettingDialog(loginActivity, 100).show();

    }
}
