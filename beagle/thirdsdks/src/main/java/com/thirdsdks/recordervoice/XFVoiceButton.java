package com.thirdsdks.recordervoice;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import com.thirdsdks.iflytek.SpeechRecognizerUtil;
import com.thirdsdks.R;

/**
 * Created by zhaotao on 2021/01/05 12:37.
 * 录音控件button
 */
@SuppressLint("AppCompatCustomView")
public class XFVoiceButton extends Button implements View.OnClickListener {

    private Dialog recordIndicator;
    private ImageView mVolumeIv, mIvPauseContinue, mIvComplete;
    private VoiceLineView voicLine;
    private TextView mRecordHintTv;
    private Context mContext;
    private SpeechRecognizerUtil.ResultListener resultListener;

    private Disposable disposable;

    public XFVoiceButton(Context context) {
        super(context);
        init();
    }

    public XFVoiceButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        init();
    }

    public XFVoiceButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    private void init() {
        setOnClickListener(this);
    }

    /**
     * 设置监听
     *
     * @param resultListener
     */
    public void setResultListener(SpeechRecognizerUtil.ResultListener resultListener) {
        this.resultListener = resultListener;
    }

    /**
     * 启动录音dialog
     */
    private void startRecordDialog() {
        recordIndicator = new Dialog(getContext(), R.style.record_voice_dialog);
        recordIndicator.setContentView(R.layout.dialog_xf_voice);
        recordIndicator.setCanceledOnTouchOutside(false);
        recordIndicator.setCancelable(false);
        mVolumeIv = (ImageView) recordIndicator.findViewById(R.id.iv_voice);
        voicLine = (VoiceLineView) recordIndicator.findViewById(R.id.voicLine);
        mRecordHintTv = (TextView) recordIndicator.findViewById(R.id.tv_length);
        mRecordHintTv.setText("00:00:00");
        mIvPauseContinue = (ImageView) recordIndicator.findViewById(R.id.iv_continue_or_pause);
        mIvComplete = (ImageView) recordIndicator.findViewById(R.id.iv_complete);

        disposable = Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long num) throws Exception {
                        if (mRecordHintTv != null) {
                            mRecordHintTv.setText(formatCountDown((num + 1) * 1000));
                        }
                    }
                });

        recordIndicator.show();
        recordIndicator.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (disposable != null) disposable.dispose();
                SpeechRecognizerUtil.stop();
            }
        });

        SpeechRecognizerUtil.recognize(new SpeechRecognizerUtil.ResultListener() {
            @Override
            public void onResult(String result) {
                if (resultListener != null) resultListener.onResult(result);
            }
        });

        // 暂停或继续
        mIvPauseContinue.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                SpeechRecognizerUtil.stop();
            }
        });
        // 完成
        mIvComplete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                SpeechRecognizerUtil.stop();
                recordIndicator.dismiss();
            }
        });
    }

    @Override
    public void onClick(View view) {
        startRecordDialog();
    }

    public static String formatCountDown(long elapsed) {
        int hour, minute, second, milli;

        milli = (int) (elapsed % 1000);
        elapsed = elapsed / 1000;

        second = (int) (elapsed % 60);
        elapsed = elapsed / 60;

        minute = (int) (elapsed % 60);
        elapsed = elapsed / 60;

        hour = (int) (elapsed % 60);

        return String.format("%02d:%02d:%02d", hour, minute, second);
    }
}
