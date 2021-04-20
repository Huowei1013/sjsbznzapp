package com.bysj.znzapp.persenter.fragment;

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
import com.bysj.znzapp.bean.response.BannerListInfo;
import com.bysj.znzapp.config.HttpConfig;
import com.bysj.znzapp.fragment.ApplicationFragment;

import java.util.List;

import okhttp3.Call;

/**
 * @Author: huowei
 * @CreateDate: 2021/3/22 19:02
 * @Describe:
 * @Version: 1.0.0
 */
public class MainFragmentPresenter extends MvpPresenter {
    private ApplicationFragment applicationFragment;

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        applicationFragment = (ApplicationFragment) MvpManager.findFragment(this);
//        initBannerData();
    }

    // 初始化轮播图
    public void initBannerData() {
        // 加载轮播图图片
        OkHttpUtils.get()
                .url(HttpConfig.getBannersList)
                .build()
                .execute(new ResponseCallBack<List<BannerListInfo>>(new Class[]{List.class, BannerListInfo.class}) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                    }

                    @Override
                    public void onResponse(Response<List<BannerListInfo>> response, int i) {
                        if (TextUtils.equals(response.getSuccess(), "1")) {
                            System.out.println(response.getData().size());
                            List<BannerListInfo> list = response.getData();
                            applicationFragment = (ApplicationFragment) MvpManager.findFragment(MainFragmentPresenter.this);
                        }
                    }
                });
    }

    /* 以下为空函数 */
    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

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
}
