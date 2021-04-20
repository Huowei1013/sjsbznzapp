package com.thirdsdks.recordervoice;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.thirdsdks.R;

/**
 * Created by zhaotao on 2021/01/05 12:37.
 * 录音控件button
 */
public class PlayVoiceFragment extends DialogFragment {

    private Dialog recordIndicator;
    private ImageView mVolumeIv, mIvPauseContinue, mIvComplete;
    private VoiceLineView voicLine;
    private TextView mRecordHintTv;
    private Context mContext;
    private EnPlayVoiceListener enPlayVoiceListener;
    private VoiceManager voiceManager;
    private String path = "";

    public PlayVoiceFragment() {

    }

    @SuppressLint("ValidFragment")
    public PlayVoiceFragment(String path) {
        this();
        this.path = path;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        init();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        recordIndicator = new Dialog(getContext(), R.style.record_voice_dialog);
        recordIndicator.setContentView(R.layout.dialog_record_voice);
        recordIndicator.setCanceledOnTouchOutside(false);
        recordIndicator.setCancelable(false);

        mVolumeIv = (ImageView) recordIndicator.findViewById(R.id.iv_voice);
        voicLine = (VoiceLineView) recordIndicator.findViewById(R.id.voicLine);
        mRecordHintTv = (TextView) recordIndicator.findViewById(R.id.tv_length);
        mRecordHintTv.setText("00:00:00");
        mIvPauseContinue = (ImageView) recordIndicator.findViewById(R.id.iv_continue_or_pause);
        mIvComplete = (ImageView) recordIndicator.findViewById(R.id.iv_complete);
        playVoice();
        return recordIndicator;
    }

    private void init() {
        voiceManager = VoiceManager.getInstance(mContext);
    }

    /**
     * 设置监听
     *
     * @param enPlayVoiceListener
     */
    public void setEnrecordVoiceListener(EnPlayVoiceListener enPlayVoiceListener) {
        this.enPlayVoiceListener = enPlayVoiceListener;
    }

    /**
     * 启动录音dialog
     */
    private void startPlayDialog() {
        //暂停或继续
        mIvPauseContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (voiceManager != null) {
                    if (voiceManager.isPlaying()){
                        mIvPauseContinue.setImageResource(R.drawable.icon_continue);
                        voicLine.setPause();
                    }else {
                        mIvPauseContinue.setImageResource(R.drawable.icon_pause);
                        voicLine.setContinue();
                    }
                    if (!voiceManager.isFinish()){
                        voiceManager.continueOrPausePlay();
                    }
                    if (voiceManager.isFinish()){
                        mIvPauseContinue.setImageResource(R.drawable.icon_pause);
                        voicLine.setContinue();
                        voiceManager.startPlay(path);
                    }
                }
            }
        });
        //完成
        mIvComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (voiceManager != null) {
                    voiceManager.stopPlay();
                }
                recordIndicator.dismiss();
            }
        });
    }

    //开始录音
    public void playVoice() {
        if (TextUtils.isEmpty(path)) return;
        startPlayDialog();
        voiceManager.setVoicePlayListener(new VoiceManager.VoicePlayCallBack() {
            @Override
            public void voiceTotalLength(long time, String strTime) {

            }

            @Override
            public void playDoing(long time, String strTime) {
                mRecordHintTv.setText(strTime);
            }

            @Override
            public void playPause() {

            }

            @Override
            public void playStart() {
                mIvPauseContinue.setImageResource(R.drawable.icon_pause);
                voicLine.setContinue();
            }

            @Override
            public void playFinish() {
                mIvPauseContinue.setImageResource(R.drawable.icon_continue);
                voicLine.setPause();
                if (enPlayVoiceListener != null) {
                    enPlayVoiceListener.onFinishPlay();
                }
            }
        });
        voiceManager.startPlay(path);
    }

    /**
     * 结束回调监听
     */
    public interface EnPlayVoiceListener {
        void onFinishPlay();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (voiceManager != null && !voiceManager.isFinish()) {
            voiceManager.stopPlay();
        }
    }
}
