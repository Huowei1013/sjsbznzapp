package com.bysj.znzapp.persenter.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.beagle.component.app.MvpManager;
import com.beagle.component.app.MvpPresenter;
import com.beagle.okhttp.OkHttpUtils;
import com.beagle.okhttp.callback.Response;
import com.beagle.okhttp.callback.ResponseCallBack;
import com.bysj.znzapp.bean.UserInfo;
import com.bysj.znzapp.config.HttpConfig;
import com.bysj.znzapp.data.UserInfoProvide;
import com.bysj.znzapp.fragment.MyFragment;

import java.util.HashMap;

import okhttp3.Call;
import okhttp3.MediaType;

/**
 * @Author: huowei
 * @CreateDate: 2021/3/23 12:06
 * @Describe:
 * @Version: 1.0.0
 */
public class MyFragmentPresenter extends MvpPresenter {

    private MyFragment myFragment;
    private HashMap<String, Boolean> hasGroup = new HashMap<>();

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        myFragment = (MyFragment) MvpManager.findFragment(this);
        getUserInfo();
    }

    // 获取用户信息
    public void getUserInfo() {
        if (myFragment == null) {
            myFragment = (MyFragment) MvpManager.findFragment(this);
            if (myFragment == null) return;
        }
        myFragment.showMyDialog();
        com.alibaba.fastjson.JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userName", UserInfoProvide.getUserInfo().getUserName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtils.postString()
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .url(HttpConfig.getUserInfo)
                .content(jsonObject.toString())
                .build()
                .execute(new ResponseCallBack<UserInfo>(UserInfo.class) {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        if (myFragment != null) {
                            myFragment.closeMyDialog();
                        }
                    }

                    @Override
                    public void onResponse(Response<UserInfo> response, int i) {
                        if (TextUtils.equals(response.getSuccess(), "1")) {
                            UserInfoProvide.setUserInfo(response.getData());// 保存登陆信息到内存中
                            UserInfo userInfo = response.getData();
                            if (myFragment != null) {
                                if (userInfo != null) {
                                    myFragment.setName(userInfo.getUserName());
//                                    myFragment.setDepartment(userInfo.getDepartName());
//                                    myFragment.setPost(userInfo.getPost());
                                    myFragment.setPhoto(userInfo.getAvatar());
                                    myFragment.closeMyDialog();
                                }
                                myFragment.closeMyDialog();
                            }
                        }
                    }
                });
    }

    /* 以下为空函数 */
    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

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
