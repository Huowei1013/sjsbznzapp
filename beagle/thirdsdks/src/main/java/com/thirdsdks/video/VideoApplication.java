package com.thirdsdks.video;

import android.app.Application;

import com.beagle.component.app.IApplication;
import com.beagle.component.logger.LogCat;

/**
 * Created by zhaotao on 2020/12/30 16:38.
 */

public class VideoApplication implements IApplication {

    public static Application APPLICATION;

    @Override
    public void attachBaseContext(Application application) {

    }

    @Override
    public void onCreate(Application application) {
        APPLICATION = application;
        // 初始化LiveSDK
        // 如果App已经集成了Crash上报，则可以不使用该功能，通过调用接口禁用掉，并可以去除相应的jar包和framework
        LogCat.d("initVideoApplication");
        // 由后台传入APPID初始化，VideoAccount
//        TIMManager.getInstance().disableCrashReport();
//        ILiveLog.setLogLevel(ILiveLog.TILVBLogLevel.DEBUG);
//        ILiveSDK.getInstance().initSdk(application, 1400027849, 11656);
////        ILiveSDK.getInstance().initSdk(application, 1254368011, 17823);
//        ILVLiveManager.getInstance().init(new ILVLiveConfig()
//                .setLiveMsgListener(MessageObservable.getInstance()));
//        ILiveSDK.getInstance().getContextEngine().setTimeOut(1);
//        ILiveSDK.getInstance().getTIMManger().disableStorage();
//        TIMManager.getInstance().disableRecentContact();
//        TIMManager.getInstance().disableRecentContactNotify();
    }
}
