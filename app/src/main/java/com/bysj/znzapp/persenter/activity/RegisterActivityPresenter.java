package com.bysj.znzapp.persenter.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.beagle.component.app.MvpPresenter;
import com.beagle.okhttp.OkHttpUtils;
import com.beagle.okhttp.callback.Response;
import com.beagle.okhttp.callback.ResponseCallBack;
import com.bysj.znzapp.activity.RegisterActivity;
import com.bysj.znzapp.config.HttpConfig;
import com.thirdsdks.filedeal.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;

/**
 * @Author: huowei
 * @CreateDate: 2021/4/1 16:36
 * @Describe:
 * @Version: 1.0.0
 */
public class RegisterActivityPresenter  extends MvpPresenter {

    private RegisterActivity registerActivity;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        registerActivity = (RegisterActivity) activity;
    }

    public void setRegister(final String name,final String passWord,  final String phone, final String email){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userName", name);
            jsonObject.put("passWord", passWord);
            jsonObject.put("phone", phone);
            jsonObject.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        registerActivity.showMyDialog();
        OkHttpUtils.postString()
                .url(HttpConfig.userInfoSave)
                .content(jsonObject.toString())
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new ResponseCallBack<Response>(Response.class) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                        registerActivity.closeMyDialog();
                    }

                    @Override
                    public void onResponse(Response<Response> successResponse, int i) {
                        if (TextUtils.equals(successResponse.getSuccess(),"1")){
                            ToastUtil.showToast(registerActivity,"注册成功，请登录");
                            registerActivity.finish();
                        }
                        registerActivity.closeMyDialog();
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

