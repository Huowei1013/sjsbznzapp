package com.thirdsdks.rtmp;

import android.app.Application;

import com.beagle.component.app.IApplication;
import com.beagle.component.logger.LogCat;

import com.tencent.rtmp.TXLivePusher;

/**
 * Created by zhaotao on 2021/01/05 11:30.
 */

public class RtmpApplication implements IApplication {

    @Override
    public void attachBaseContext(Application application) {

    }

    @Override
    public void onCreate(Application application) {
        int[] sdkVer = TXLivePusher.getSDKVersion(); // 这里调用TXLivePlayer.getSDKVersion()也是可以的
        if (sdkVer != null && sdkVer.length >= 4) {
            LogCat.e("rtmp sdk version is:" + sdkVer[0] + "." + sdkVer[1] + "." + sdkVer[2] + "." + sdkVer[3]);
        }
    }
}
