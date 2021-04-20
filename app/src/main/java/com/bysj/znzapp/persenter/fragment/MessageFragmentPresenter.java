package com.bysj.znzapp.persenter.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;

import com.beagle.component.app.MvpManager;
import com.beagle.component.app.MvpPresenter;
import com.beagle.okhttp.OkHttpUtils;
import com.beagle.okhttp.callback.Response;
import com.beagle.okhttp.callback.ResponseCallBack;
import com.bysj.znzapp.activity.MainActivity;
import com.bysj.znzapp.bean.response.MessageGroup;
import com.bysj.znzapp.config.HttpConfig;
import com.bysj.znzapp.fragment.MessageFragment;
import com.bysj.znzapp.adapter.MessageAdapter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;

/**
 * @Author: huowei
 * @CreateDate: 2021/3/23 10:43
 * @Describe:
 * @Version: 1.0.0
 */
public class MessageFragmentPresenter extends MvpPresenter {

    private MessageFragment messageFragment;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        messageFragment = (MessageFragment) MvpManager.findFragment(this);
    }

    // 获取消息
    public void getData() {
        if (messageFragment == null) {
            messageFragment = (MessageFragment) MvpManager.findFragment(this);
            if (messageFragment == null) return;
        }
        messageFragment.showMyDialog();
        OkHttpUtils.get()
                .url(HttpConfig.getMessageByGroup)
                .build()
                .execute(new ResponseCallBack<List<MessageGroup>>(new Class[]{List.class, MessageGroup.class}) {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        if (messageFragment != null) {
                            messageFragment.closeMyDialog();
                        }
                    }

                    @Override
                    public void onResponse(Response<List<MessageGroup>> response, int i) {
                        if (TextUtils.equals(response.getSuccess(), "1")) {
                            List<MessageGroup> mData = response.getData();
                            if (messageFragment != null) {
                                messageFragment.update(mData);
                                messageFragment.closeMyDialog();
                            }
                        }
                    }
                });
    }

    // 标记已读
    public void markAll() {
        if (messageFragment == null) {
            messageFragment = (MessageFragment) MvpManager.findFragment(this);
            if (messageFragment == null) return;
        }
        messageFragment.showMyDialog();
        JSONObject jsonObject = new JSONObject();
        OkHttpUtils.postString()
                .url(HttpConfig.markAllMessagekAsRead)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(jsonObject.toString())
                .build()
                .execute(new ResponseCallBack<Object>(Object.class) {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        if (messageFragment != null) {
                            messageFragment.closeMyDialog();
                        }
                    }

                    @Override
                    public void onResponse(Response<Object> expDetailResponse, int i) {
                        if (TextUtils.equals(expDetailResponse.getSuccess(), "1")) {
                            if (messageFragment != null) {
                                messageFragment.refresh();
                                messageFragment.closeMyDialog();
                                if (messageFragment.getActivity() != null) {
                                    ((MainActivity) messageFragment.getActivity()).updateMessage();
                                }
                            }
                        }
                    }
                });
    }

    private List<MessageGroup> processData(List<MessageGroup> data) {
        if (data != null) {
            List<MessageGroup> temp = new ArrayList<>();
            String bType = null;
            for (int i = 0; i < data.size(); i++) {
                bType = data.get(i).getType();
                if (TextUtils.equals(bType, MessageAdapter.TYPE_CHECK)
                        || TextUtils.equals(bType, MessageAdapter.TYPE_EXP)
                        || TextUtils.equals(bType, MessageAdapter.TYPE_WORKLOG)
                        || TextUtils.equals(bType, MessageAdapter.TYPE_NOTICE)
                        || TextUtils.equals(bType, MessageAdapter.TYPE_DYMANIC)
                        || TextUtils.equals(bType, MessageAdapter.TYPE_EVENT)) {
                    temp.add(data.get(i));
                }
            }
            return temp;
        }
        return data;
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

