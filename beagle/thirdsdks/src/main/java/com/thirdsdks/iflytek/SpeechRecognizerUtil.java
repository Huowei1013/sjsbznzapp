package com.thirdsdks.iflytek;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.beagle.component.logger.LogCat;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class SpeechRecognizerUtil {

    private static String TAG = SpeechRecognizerUtil.class.getSimpleName();
    // 用HashMap存储听写结果
    private static HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    private static StringBuffer buffer = new StringBuffer();
    private static ResultListener mResultListener;

    private static final Handler han = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x001) {
                executeStream();
            }
        }
    };

    private static int ret = 0; // 函数调用返回值

    public static void recognize(ResultListener resultListener) {
        buffer.setLength(0);
        mIatResults.clear();
        mResultListener = resultListener;

        ret = SpeechApplication.mIat.startListening(mRecognizerListener);
        if (ret != ErrorCode.SUCCESS) {
            SpeechApplication.showTip("听写失败,错误码：" + ret);
        }
    }

    public static void stop() {
        buffer.setLength(0);
        mIatResults.clear();

        if (SpeechApplication.mIat != null) {
            SpeechApplication.mIat.stopListening();
        }
    }

    public static void destroy() {
        if (null != SpeechApplication.mIat) {
            // 退出时释放连接
            SpeechApplication.mIat.cancel();
            SpeechApplication.mIat.destroy();
        }
    }

    /**
     * 初始化监听器。
     */
    public static InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            LogCat.d("SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                SpeechApplication.showTip("初始化失败，错误码：" + code + ",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
            }
        }
    };

    /**
     * 听写UI监听器
     */
    private static RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            printResult(results);
        }

        /**
         * 识别回调错误.
         */
        public void onError(SpeechError error) {
            SpeechApplication.showTip(error.getPlainDescription(true));
        }
    };

    private static RecognizerListener mRecognizerListener = new RecognizerListener() {
        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            // SpeechApplication.showTip("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            SpeechApplication.showTip(error.getPlainDescription(true));
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            // showTip("结束说话");
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.d(TAG, results.getResultString());
            if (SpeechApplication.resultType.equals("json")) {
                printResult(results);

            } else if (SpeechApplication.resultType.equals("plain")) {
                buffer.append(results.getResultString());
            }
            if (isLast & SpeechApplication.cyclic) {
                Message message = Message.obtain();
                message.what = 0x001;
                han.sendMessageDelayed(message, 100);
            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            // SpeechApplication.showTip("当前正在说话，音量大小：" + volume);
            Log.d(TAG, "返回音频数据：" + data.length);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };

    private static void printResult(RecognizerResult results) {
        LogCat.e("------>" + results.getResultString());
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        boolean ls = false;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
            ls = resultJson.optBoolean("ls");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        if (ls) {
            StringBuffer resultBuffer = new StringBuffer();
            for (String key : mIatResults.keySet()) {
                resultBuffer.append(mIatResults.get(key));
            }

            String result = resultBuffer.toString();
            if (mResultListener != null) mResultListener.onResult(result);
        }

    }

    // 执行音频流识别操作
    private static void executeStream() {
        buffer.setLength(0);
        mIatResults.clear();
        // 设置音频来源为外部文件
        SpeechApplication.mIat.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
        // 也可以像以下这样直接设置音频文件路径识别（要求设置文件在sdcard上的全路径）：
        // mIat.setParameter(SpeechConstant.AUDIO_SOURCE, "-2");
        //mIat.setParameter(SpeechConstant.ASR_SOURCE_PATH, "sdcard/XXX/XXX.pcm");
        ret = SpeechApplication.mIat.startListening(mRecognizerListener);
        if (ret != ErrorCode.SUCCESS) {
            SpeechApplication.showTip("识别失败,错误码：" + ret + ",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
        } else {
            byte[] audioData = FucUtil.readAudioFile(SpeechApplication.mApplication, "iattest.wav");
            if (null != audioData) {
                // SpeechApplication.showTip(getString(R.string.text_begin_recognizer));
                // 一次（也可以分多次）写入音频文件数据，数据格式必须是采样率为8KHz或16KHz（本地识别只支持16K采样率，云端都支持），
                // 位长16bit，单声道的wav或者pcm
                // 写入8KHz采样的音频时，必须先调用setParameter(SpeechConstant.SAMPLE_RATE, "8000")设置正确的采样率
                // 注：当音频过长，静音部分时长超过VAD_EOS将导致静音后面部分不能识别。
                ArrayList<byte[]> bytes = FucUtil.splitBuffer(audioData, audioData.length, audioData.length / 3);
                for (int i = 0; i < bytes.size(); i++) {
                    SpeechApplication.mIat.writeAudio(bytes.get(i), 0, bytes.get(i).length);

                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {

                    }
                }
                SpeechApplication.mIat.stopListening();
            } else {
                SpeechApplication.mIat.cancel();
                SpeechApplication.showTip("读取音频流失败");
            }
        }
    }

    public static interface ResultListener {
        void onResult(String result);
    }
}
