package com.bysj.znzapp.persenter.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;

import com.beagle.component.app.MvpPresenter;
import com.beagle.component.json.JsonUtil;
import com.beagle.okhttp.OkHttpUtils;
import com.beagle.okhttp.callback.Response;
import com.beagle.okhttp.callback.ResponseCallBack;
import com.bysj.znzapp.activity.InfoActivity;
import com.bysj.znzapp.bean.UserInfo;
import com.bysj.znzapp.bean.response.SuccessInfo;
import com.bysj.znzapp.config.HttpConfig;
import com.bysj.znzapp.data.UserInfoProvide;
import com.thirdsdks.filedeal.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;

/**
 * @Author: huowei
 * @CreateDate: 2021/4/11 13:44
 * @Describe:
 * @Version: 1.0.0
 */
public class InfoActivityPresenter extends MvpPresenter {

    private InfoActivity infoActivity;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        infoActivity = (InfoActivity) activity;
    }

    // 保存个人信息
    public void submit(final String num, final String mail, final List<String> path, final String name){
//        infoActivity.showMyDialog();
//        new UploadComponent.Builder(HttpConfig.updateMyInfo, new UploadComponent.Param() {
//            @Override
//            public String reqJson(List<UploadFile> idFileList) {
//                MyInfo info = new MyInfo();
//                info.setPhone(num);
//                info.setEmail(mail);
//                info.setToken(UserInfoProvide.getUserInfo().getToken());
//                info.setUserId(UserInfoProvide.getUserInfo().getUserId());
//                info.setUserName(name);
//                info.setFiles(idFileList.get(0).getVisitPath());
//                return JsonUtil.getInstance().toJson(info);
//            }
//        }, new ResponseCallBack<SuccessInfo>(SuccessInfo.class) {
//            @Override
//            public void onError(Call call, Exception e, int id) {
//                super.onError(call, e, id);
//                infoActivity.closeMyDialog();
//            }
//
//            @Override
//            public void onResponse(Response<SuccessInfo> successResponse, int i) {
//                if (TextUtils.equals(successResponse.getSuccess(),"1")){
//                    SuccessInfo success = successResponse.getData();
//                    if (success != null){
//                        if (success.isIsValid()){
//                            ToastUtil.showToast(infoActivity,success.getValidaionMessage());
//                            UserInfo userInfo = new UserInfo();
//                            userInfo.setEmail(mail);
//                            userInfo.setAvatar(path.get(0));
//                            userInfo.setDepartName(UserInfoProvide.getUserInfo().getDepartName());
//                            userInfo.setUserId(UserInfoProvide.getUserInfo().getUserId());
//                            userInfo.setUserName(name);
//                            userInfo.setPhone(num);
//                            userInfo.setPost(UserInfoProvide.getUserInfo().getPost());
//                            userInfo.setToken(UserInfoProvide.getUserInfo().getToken());
//                            userInfo.setXzName(UserInfoProvide.getUserInfo().getXzName());
//                            UserInfoProvide.setUserInfo(userInfo);//保存登陆信息到内存中
//                            infoActivity.close();
//                        }else {
//                            ToastUtil.showToast(infoActivity,success.getValidaionMessage());
//                        }
//                    }
//                }
//                infoActivity.closeMyDialog();
//            }
//        }).setPhotos(path).build().uploadFiles();
    }

    // 保存个人信息（不带头像）
    public void submit_noPhoto(final String num, final String mail, final String name){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", mail);
            jsonObject.put("phone", num);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        infoActivity.showMyDialog();
        OkHttpUtils.postString()
                .url("HttpConfig.updateMyInfo")
                .content(jsonObject.toString())
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new ResponseCallBack<SuccessInfo>(SuccessInfo.class) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                        infoActivity.closeMyDialog();
                    }

                    @Override
                    public void onResponse(Response<SuccessInfo> successResponse, int i) {
                        if (TextUtils.equals(successResponse.getSuccess(),"1")){
                            SuccessInfo success = successResponse.getData();
                            if (success != null){
                                if (success.isValid()){
                                    ToastUtil.showToast(infoActivity,success.getValidaionMessage());
                                    UserInfo userInfo = new UserInfo();
                                    userInfo.setEmail(mail);
                                    userInfo.setAvatar(UserInfoProvide.getUserInfo().getAvatar());
                                    userInfo.setDepartName(UserInfoProvide.getUserInfo().getDepartName());
                                    userInfo.setUserId(UserInfoProvide.getUserInfo().getUserId());
                                    userInfo.setUserName(name);
                                    userInfo.setPhone(num);
                                    userInfo.setPost(UserInfoProvide.getUserInfo().getPost());
                                    userInfo.setToken(UserInfoProvide.getUserInfo().getToken());
                                    userInfo.setXzName(UserInfoProvide.getUserInfo().getXzName());
                                    UserInfoProvide.setUserInfo(userInfo);//保存登陆信息到内存中
                                    infoActivity.close();
                                }else {
                                    ToastUtil.showToast(infoActivity,success.getValidaionMessage());
                                }
                            }
                        }
                        infoActivity.closeMyDialog();
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

