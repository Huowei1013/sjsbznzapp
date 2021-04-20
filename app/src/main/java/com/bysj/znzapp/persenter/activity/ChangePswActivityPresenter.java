package com.bysj.znzapp.persenter.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.beagle.component.app.MvpPresenter;
import com.beagle.okhttp.OkHttpUtils;
import com.beagle.okhttp.callback.Response;
import com.beagle.okhttp.callback.ResponseCallBack;
import com.bysj.znzapp.activity.ChangePswActivity;
import com.bysj.znzapp.activity.LoginActivity;
import com.bysj.znzapp.base.BaseApp;
import com.bysj.znzapp.bean.response.ChangePswInfo;
import com.bysj.znzapp.config.HttpConfig;
import com.bysj.znzapp.data.UserInfoProvide;
import com.thirdsdks.filedeal.ToastUtil;

import okhttp3.Call;
import okhttp3.MediaType;

/**
 * @Author: huowei
 * @CreateDate: 2021/4/13 12:11
 * @Describe:
 * @Version: 1.0.0
 */

public class ChangePswActivityPresenter extends MvpPresenter {

    private ChangePswActivity changePSWActivity;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (activity instanceof ChangePswActivity){
            changePSWActivity = (ChangePswActivity) activity;
        }
    }

    public void ensure(String passwordA, String passwordB, String passwordC){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userName", UserInfoProvide.getUserInfo().getUserName());
            jsonObject.put("passWord",passwordA);
            jsonObject.put("newPassWord",passwordB);
            jsonObject.put("confirmPassword",passwordC);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        changePSWActivity.showMyDialog();
        OkHttpUtils.postString()
                .url(HttpConfig.editPassWord)
                .content(jsonObject.toString())
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new ResponseCallBack<Response>(Response.class) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                        changePSWActivity.closeMyDialog();
                    }

                    @Override
                    public void onResponse(Response<Response> changePSWResponse, int i) {
                        if (TextUtils.equals(changePSWResponse.getSuccess(),"1")){
                                    ToastUtil.showToast(changePSWActivity,"修改密码成功，请重新登录");
                                    changePSWActivity.finish();
                                    BaseApp.getApp().exitToAcitivty(LoginActivity.class);
                                }else {
                                    ToastUtil.showToast(changePSWActivity, changePSWResponse.getErrMsg());
                                }
                        changePSWActivity.closeMyDialog();
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
