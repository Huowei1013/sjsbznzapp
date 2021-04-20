package com.bysj.znzapp.data;

import android.text.TextUtils;

import com.beagle.component.json.JsonUtil;
import com.bysj.znzapp.base.BaseApp;
import com.bysj.znzapp.bean.UserInfo;
import com.bysj.znzapp.db.DbCenter;
import com.bysj.znzapp.utils.SharedPreferencesUtil;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * @Author: huowei
 * @CreateDate: 2021/3/22 16:21
 * @Describe:
 * @Version: 1.0.0
 */
public class UserInfoProvide {
    private static UserInfo userInfo;

    public static UserInfo getUserInfo() {
        if (userInfo == null) {// 为空则可能是被内存回收了
            // 可能切换账号，保存了很多登陆信息，找到最后一条也就是最近登陆的账号
            List<UserInfo> userInfos = DbCenter.getInstance().findAllEntity(UserInfo.class);
            if (userInfos != null && !userInfos.isEmpty()) {
                userInfo = userInfos.get(userInfos.size() - 1);
                if (userInfo != null) {
                    // 简单处理不做actionCodes关联表
                    String content = SharedPreferencesUtil.getString(BaseApp.getApp(), "actions");
                    if (!TextUtils.isEmpty(content)) {
                        List<String> actionCodes = JsonUtil.getInstance().fromJson(content, new TypeToken<List<String>>() {
                        }.getType());
                        userInfo.setActionCodes(actionCodes);
                    }
                    // 简单处理不做accountUsers关联表
                    String accountUser_content = SharedPreferencesUtil.getString(BaseApp.getApp(), "accountUsers");
                    if (!TextUtils.isEmpty(accountUser_content)) {
                        List<UserInfo.AccountUser> accountUserList = JsonUtil.getInstance().fromJson(accountUser_content, new TypeToken<List<UserInfo.AccountUser>>() {
                        }.getType());
                        userInfo.setAccountUsers(accountUserList);
                    }
                }
            }
        }
        return userInfo;
    }

    public static void setUserInfo(UserInfo userInfo) {
        UserInfoProvide.userInfo = userInfo;
        if (userInfo != null) {
            DbCenter.getInstance().saveEntity(userInfo);
            List<String> actionCodes = userInfo.getActionCodes();
            if (actionCodes != null && !actionCodes.isEmpty()) {
                String content = JsonUtil.getInstance().toJson(actionCodes);
                SharedPreferencesUtil.putString(BaseApp.getApp(), "actions", content);
            }
            List<UserInfo.AccountUser> accountUsers = userInfo.getAccountUsers();
            if (accountUsers != null && !accountUsers.isEmpty()) {
                String content = JsonUtil.getInstance().toJson(accountUsers);
                SharedPreferencesUtil.putString(BaseApp.getApp(), "accountUsers", content);
            }
        } else {
            SharedPreferencesUtil.putString(BaseApp.getApp(), "actions", "");
            SharedPreferencesUtil.putString(BaseApp.getApp(), "accountUsers", "");
        }
    }

    // 清空用户信息
    public static void cleanUserInfo() {
        if (userInfo != null) {
            DbCenter.getInstance().deleteEntity(userInfo);
        } else {
            List<UserInfo> userInfos = DbCenter.getInstance().findAllEntity(UserInfo.class);
            if (userInfos != null && !userInfos.isEmpty()) {
                UserInfo userInfo = userInfos.get(userInfos.size() - 1);
                DbCenter.getInstance().deleteEntity(userInfo);
            }
        }
        SharedPreferencesUtil.putString(BaseApp.getApp(), "actions", "");
        SharedPreferencesUtil.putString(BaseApp.getApp(), "accountUsers", "");
        userInfo = null;
    }
}
