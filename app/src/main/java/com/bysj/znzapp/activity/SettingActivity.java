package com.bysj.znzapp.activity;

import androidx.appcompat.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beagle.component.activity.BaseCompatActivity;
import com.beagle.component.app.MvpManager;
import com.beagle.component.utils.ThreadPoolUtil;
import com.bysj.znzapp.R;
import com.bysj.znzapp.base.BaseApp;
import com.bysj.znzapp.persenter.activity.SettingActivityPresenter;
import com.bysj.znzapp.utils.FileUtil;
import com.bysj.znzapp.utils.SharedPreferencesUtil;
import com.thirdsdks.filedeal.ToastUtil;

public class SettingActivity extends BaseCompatActivity implements View.OnClickListener {

    private AlertDialog isExit;
    private String size = "";
    private String fileDirSize = "";
    private TextView tv_about;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initHead();
        initView();
    }

    @Override
    protected void initHead() {
        getCenterTextView().setText("设置");
        getCenterTextView().setTextColor(Color.WHITE);
    }

    @Override
    protected void initView() {
        RelativeLayout layout_setChangePsw = (RelativeLayout) findViewById(R.id.layout_setChangePsw);
        layout_setChangePsw.setOnClickListener(this);
        RelativeLayout layout_setChangeSenPsw = (RelativeLayout) findViewById(R.id.layout_setChangeSenPsw);
        layout_setChangeSenPsw.setOnClickListener(this);
        RelativeLayout layout_setLoginDevice = (RelativeLayout) findViewById(R.id.layout_setLoginDevice);
        layout_setLoginDevice.setOnClickListener(this);
        RelativeLayout layout_setRemind = (RelativeLayout) findViewById(R.id.layout_setRemind);
        layout_setRemind.setOnClickListener(this);
        RelativeLayout layout_setClearCache = (RelativeLayout) findViewById(R.id.layout_setClearCache);
        layout_setClearCache.setOnClickListener(this);
        RelativeLayout layout_setVersionUpdate = (RelativeLayout) findViewById(R.id.layout_setVersionUpdate);
        layout_setVersionUpdate.setOnClickListener(this);
        RelativeLayout layout_setAboutApp = (RelativeLayout) findViewById(R.id.layout_setAboutApp);
        layout_setAboutApp.setOnClickListener(this);
        TextView txt_setLogout = (TextView) findViewById(R.id.txt_setLogout);
        txt_setLogout.setOnClickListener(this);
        tv_about = (TextView) findViewById(R.id.tv_about);
        tv_about.setText("关于" + getResources().getText(R.string.AppName));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_setChangePsw:// 修改密码
                startActivity(new Intent(context, ChangePswActivity.class));
                break;
            case R.id.layout_setChangeSenPsw:// 修改敏感数据密码（暂时隐藏）
                break;
            case R.id.layout_setLoginDevice:// 登录设备管理（暂时隐藏）
                break;
            case R.id.layout_setRemind:// 提醒设置（暂时隐藏）
                break;
            case R.id.layout_setClearCache:// 清理缓存
                getCacheSize();
                break;
            case R.id.layout_setVersionUpdate:// 版本更新记录
                startActivity(new Intent(context, VersionUpdateActivity.class).putExtra("isUpdate",true));
                break;
            case R.id.layout_setAboutApp:// 关于
                startActivity(new Intent(context, VersionUpdateActivity.class).putExtra("isUpdate",false));
                break;
            case R.id.txt_setLogout:
                isExit = new AlertDialog.Builder(SettingActivity.this).create();
                isExit.setTitle("提示");
                isExit.setMessage("确定退出登录？");
                isExit.setButton(DialogInterface.BUTTON_POSITIVE, "确定", logoutListener);
                isExit.setButton(DialogInterface.BUTTON_NEUTRAL, "取消", logoutListener);
                isExit.show();
                break;
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MvpManager.bindPresenter(SettingActivity.this, "com.bysj.znzapp.presenter.activity.SettingActivityPresenter");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isExit != null) isExit.dismiss();
    }

    // 登出
    private void logout() {
//        SettingActivityPresenter settingPresenter = (SettingActivityPresenter) MvpManager.findPresenter(SettingActivity.this);
//        if (settingPresenter != null) {
//            settingPresenter.logout();
//        }
        SharedPreferencesUtil.putString(context, "username", "");
        SharedPreferencesUtil.putString(context, "password", "");
        exitToAcitivty();
    }

    // 登出提示
    DialogInterface.OnClickListener logoutListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:
                    logout();
                    break;
                case AlertDialog.BUTTON_NEUTRAL:
                    break;
                default:
                    break;
            }
        }
    };

    // 登出跳转
    public void exitToAcitivty() {
        BaseApp.getApp().exitToAcitivty(LoginActivity.class);
        finish();
    }

    // 获取缓存尺寸
    private void getCacheSize() {
        // 异步，递归是耗时操作
        showMyDialog();
        ThreadPoolUtil.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    size = FileUtil.cacheSize(context);
                    fileDirSize = FileUtil.fileDirSize(context);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeMyDialog();
                        isExit = new AlertDialog.Builder(context).create();
                        isExit.setMessage("缓存" + size + ",下载文件大小" + fileDirSize + "，删除缓存");
                        isExit.setButton(DialogInterface.BUTTON_POSITIVE, "确定", cachelistener);
                        isExit.setButton(DialogInterface.BUTTON_NEUTRAL, "取消", cachelistener);
                        isExit.show();
                    }
                });
            }
        });
    }

    // 清除缓存提示
    DialogInterface.OnClickListener cachelistener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:
                    clearAllCache();
                    break;
                case AlertDialog.BUTTON_NEUTRAL:
                    break;
                default:
                    break;
            }
        }
    };

    // 清除缓存
    private void clearAllCache() {
        showMyDialog();
        ThreadPoolUtil.execute(new Runnable() {
            @Override
            public void run() {
                FileUtil.clearAllCache(context);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeMyDialog();
                        ToastUtil.showToast(context, "缓存清空成功");
                    }
                });
            }
        });
    }
}

