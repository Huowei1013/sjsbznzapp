package com.thirdsdks.tinker;

import android.app.Application;

import com.beagle.component.app.IApplication;
import com.beagle.component.logger.LogCat;
import com.beagle.component.utils.ProcessUtil;

import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.crashreport.CrashReport;

import com.thirdsdks.BuildConfig;

/**
 * Created by zhaotao on 2020/12/20 20:52.
 */

public class TinkerApplication implements IApplication {

    private static final String APPID = "902bc8e01c";

    @Override
    public void attachBaseContext(Application application) {
        // 安装tinker
        if (!BuildConfig.DEBUG) {
            try {
                if (null != Class.forName("com.tencent.bugly.beta.tinker.TinkerApplicationLike")) {
                    Beta.installTinker();
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreate(Application application) {
        if (!BuildConfig.DEBUG) {
            hotfix(application);
            crashReport(application);
        }
    }

    private void hotfix(Application application) {
        // 这里实现SDK初始化，appId替换成你的在Bugly平台申请的appId
        // 调试时，将第三个参数改为true
        Bugly.init(application, APPID, !LogCat.isClosedLog);
    }

    private void crashReport(Application application) {
        // 获取当前包名
        String packageName = application.getPackageName();
        // 获取当前进程名
        String processName = ProcessUtil.getCurrentProcessName();
        LogCat.e(String.format("%s-----%s", packageName, processName));
        Bugly.setAppChannel(application, "");
        // 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(application);
        strategy.setUploadProcess(processName == null || processName.equals(packageName));
        // 初始化Bugly
        CrashReport.initCrashReport(application, APPID, true, strategy);
    }
}
