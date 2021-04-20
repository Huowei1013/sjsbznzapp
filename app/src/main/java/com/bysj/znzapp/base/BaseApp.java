package com.bysj.znzapp.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.StrictMode;
import android.text.TextUtils;

import androidx.multidex.BuildConfig;
import androidx.multidex.MultiDex;

import com.beagle.component.app.AppManager;
import com.beagle.component.app.ModuleApplicaiton;
import com.beagle.component.app.MvpManager;
import com.beagle.component.logger.LogCat;
import com.beagle.component.utils.ProcessUtil;
import com.beagle.okhttp.LogInterceptor;
import com.beagle.okhttp.OkHttpApplication;
import com.bysj.znzapp.config.HttpConfig;
import com.bysj.znzapp.data.UserInfoProvide;
import com.bysj.znzapp.utils.SharedPreferencesUtil;

/**
 * @Author: huowei
 * @CreateDate: 2021/3/22 16:16
 * @Describe:
 * @Version: 1.0.0
 */
public class BaseApp extends Application {
    public static BaseApp baseApp;

    public static BaseApp getApp() {
        return baseApp;
    }

    @Override
    protected void attachBaseContext(final Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        ModuleApplicaiton.getInstance().attachBaseContextsInit(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        baseApp = this;

        //调整华为EC初始化
//        MeetingFunc.onAppCreate();
        LogCat.isClosedLog(!BuildConfig.DEBUG);
        if (TextUtils.equals(ProcessUtil.getCurrentProcessName(), getPackageName())) {
            ModuleApplicaiton.getInstance().onCreate("com.thirdsdks.iflytek.SpeechApplication", baseApp);
            ModuleApplicaiton.getInstance().onCreate("com.thirdsdks.push.PushApplication", baseApp);
            ModuleApplicaiton.getInstance().onCreate("com.thirdsdks.rtmp.RtmpApplication", baseApp);
            ModuleApplicaiton.getInstance().onCreate("com.thirdsdks.video.VideoApplication", baseApp);
            ModuleApplicaiton.getInstance().onCreate("com.thirdsdks.map.MapApplication",baseApp);
            //初始化延时处理
            initResource();
        }
    }
    /***
     * 子线程中处理初始化操作 优化启动时间
     */
    private void initResource(){
        new Thread(new Runnable() {
            @Override
            public void run() {
//                checkService();
                registerActivityLifecycleCallbacks(new MvpManager());
                OkHttpApplication.getInterceptors().add(new LogInterceptor());
                SharedPreferencesUtil.putString(baseApp, "host", HttpConfig.host);
                //ModuleApplicaiton.getInstance().onCreate("com.thirdsdks.huawei.ECApplication", this);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());
                    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                            .detectDiskReads()
                            .detectDiskWrites()
                            .detectNetwork()
                            .penaltyLog()
                            .build());
                }
                //Aroute建议放到最后，初始化慢...
//                initAroute();
            }
        }).start();
    }
    // 退出到某个Activity，并清空数据和状态
    public void exitToAcitivty(Class<? extends Activity> activityClass) {
        Intent intent = new Intent(this, activityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("exit", "");
        startActivity(intent);
        clearData();
    }
    // 清空缓存数据
    public void clearData() {
        // 停用鹰眼服务
//        if (BaseApp.getApp().getTrack() != null) {
//            BaseApp.getApp().getTrack().tryStopService();
//        }
        UserInfoProvide.cleanUserInfo();
        clearCookie();
        MvpManager.clear();
    }
    // 清除cookie
    public void clearCookie() {
        SharedPreferences sp = getSharedPreferences("cookies", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
        SharedPreferences sp1 = getSharedPreferences("CookiePrefsFile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = sp1.edit();
        editor1.clear();
        editor1.commit();
    }
    // 退出操作
    public void exitApp() {
        clearData();
        AppManager.finishAllActivity();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
