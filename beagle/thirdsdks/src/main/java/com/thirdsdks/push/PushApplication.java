package com.thirdsdks.push;

import android.app.Application;

import com.beagle.component.app.IApplication;
import com.beagle.component.logger.AppDev;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by zhaotao on 2020/12/20 01:23.
 */

public class PushApplication implements IApplication {

    @Override
    public void attachBaseContext(Application application) {

    }

    @Override
    public void onCreate(Application application) {
        JPushInterface.setDebugMode(AppDev.isDebug());    // 设置开启日志,发布时请关闭日志
        JPushInterface.init(application);                 // 初始化 JPush
    }
}
