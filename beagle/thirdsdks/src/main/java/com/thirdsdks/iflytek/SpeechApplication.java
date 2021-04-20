package com.thirdsdks.iflytek;

import android.annotation.SuppressLint;
import android.app.Application;
import android.widget.Toast;

import com.iflytek.cloud.Setting;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import com.beagle.component.app.IApplication;

/**
 * Created by zhaotao on 2021/01/05 11:30.
 */

public class SpeechApplication implements IApplication {

    private static final String APPID = "60011ccc";
    public static Toast mToast;
    public static Application mApplication;

    // 语音听写对象
    public static com.iflytek.cloud.SpeechRecognizer mIat;

    // 引擎类型
    private static String mEngineType = SpeechConstant.TYPE_CLOUD;

    public static String language = "zh_cn";
    private static int selectedNum = 0;
    public static String resultType = "json";
    public static boolean cyclic = false;// 音频流识别是否循环调用

    @Override
    public void attachBaseContext(Application application) {

    }

    @SuppressLint("ShowToast")
    @Override
    public void onCreate(Application application) {
        SpeechUtility.createUtility(application, "appid=" + APPID);
        mToast = Toast.makeText(application, "", Toast.LENGTH_SHORT);
        mApplication = application;
        init(application);
    }

    private void init(Application application) {
        mIat = com.iflytek.cloud.SpeechRecognizer.createRecognizer(application, SpeechRecognizerUtil.mInitListener);
        setParam();
        Setting.setShowLog(true);
    }

    private static void setParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);
        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, resultType);

        if (language.equals("zh_cn")) {
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            mIat.setParameter(SpeechConstant.ACCENT, "mandarin");
        } else {
            mIat.setParameter(SpeechConstant.LANGUAGE, language);
        }

        //此处用于设置dialog中不显示错误码信息
        //mIat.setParameter("view_tips_plain","false");

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, "4000");

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, "1000");

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "1");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, SpeechApplication.mApplication.getFilesDir().getAbsolutePath() + "/msc/iat.wav");
    }

    public static void showTip(final String str) {
        if (mToast != null) {
            mToast.setText(str);
            mToast.show();
        }
    }
}
