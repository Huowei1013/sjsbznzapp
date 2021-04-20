package com.thirdsdks.recorder;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.beagle.component.activity.BaseCompatFragment;

import com.thirdsdks.filedeal.FileDownUtil;
import com.thirdsdks.filedeal.ToastUtil;
import com.thirdsdks.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhaotao on 2021/01/04 20:52.
 */

public class RecordeFragment extends BaseCompatFragment implements AudioRecoderUtils.OnAudioStatusUpdateListener {

    private AudioRecoderDialog recoderDialog;
    private AudioRecoderUtils recoderUtils;
    private Context context;
    private long startTime;
    private TextView recorderTime;
    private SeekBar seekBar;
    private ArrayList<String> voices = new ArrayList<>();
    private boolean isDown = false;
    private Button playBtn;
    private Button recorderBtn;
    private Button deleteBtn;
    private boolean isOnlyPlay;
    private boolean flag = false;
    private String sec = "";

    private Map<String, String> localNetPathMap = new HashMap<>();

    public static RecordeFragment newRecorderFragment() {
        return new RecordeFragment();
    }

    public static RecordeFragment newRecorderFragment(boolean isOnlyPlay) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isOnlyPlay", isOnlyPlay);
        RecordeFragment recordeFragment = new RecordeFragment();
        recordeFragment.setArguments(bundle);
        return recordeFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recorder, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (recoderDialog == null) {
            recoderDialog = new AudioRecoderDialog(context);
            recoderDialog.setShowAlpha(0.98f);
        }
        if (recoderUtils == null) {
            recoderUtils = new AudioRecoderUtils(context.getFilesDir());
            recoderUtils.setOnAudioStatusUpdateListener(this);
        }
        recorderTime = (TextView) view.findViewById(R.id.recorderTime);
        recorderTime.setText("0\"");
        seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        seekBar.setEnabled(false);
        playBtn = (Button) view.findViewById(R.id.playBtn);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoderUtils.stopRecord();
                if (recoderUtils.isAudioPlaying() || ((Button) v).getText().equals("停止")) {
                    ((Button) v).setText("播放");
                    recoderUtils.stopPlayAudio();
                    handler.removeCallbacks(runnable);
                    seekBar.setProgress(0);
                } else {
                    // 插入网络地址播放逻辑 start
                    String path = recoderUtils.getFilePath();
                    if (!TextUtils.isEmpty(path)) {
                        if (path.startsWith("http") || path.startsWith("https")) {
                            if (!TextUtils.isEmpty(localNetPathMap.get(path))) {
                                // 替换为本地路径
                                recoderUtils.setPath(localNetPathMap.get(path));
                            }
                        }
                    }
                    // 插入网络地址播放逻辑 end
                    if (recoderUtils.startPlayAudio()) {
                        ((Button) v).setText("停止");
                        if (!TextUtils.isEmpty(sec)) {
                            sec = sec.substring(0, sec.length() - 1);
                            seekBar.setMax(Integer.parseInt(sec));
                        } else {
                            seekBar.setMax(recoderUtils.getDuration() / 1000);
                        }
                        if (isOnlyPlay) {
                            seekBar.setMax(recoderUtils.getDuration() / 1000);
                        }
                        handler.removeCallbacks(runnable);
                        seekBar.setProgress(0);
                        handler.postDelayed(runnable, 1000);
                    } else {
                        ToastUtil.showToast(context, "文件不存在,或正在下载");
                        seekBar.setProgress(0);
                    }
                }
            }
        });
        recorderBtn = (Button) view.findViewById(R.id.recorderBtn);
        recorderBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!isDown) {
                    recoderUtils.stopPlayAudio();
                    startTime = System.currentTimeMillis();
                    recoderUtils.startRecord();
                    recoderDialog.showAtLocation(v, Gravity.CENTER, 0, 0);
                    isDown = true;
                    playBtn.setText("播放");
                }
                return false;
            }
        });

        recorderBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!flag) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            reset();
                            ToastUtil.showToast(context, "长按录音");
                            recorderBtn.getParent().requestDisallowInterceptTouchEvent(true);
                            return false;
                        case MotionEvent.ACTION_UP:
                            if (isDown) {
                                recoderDialog.dismiss();
                                recoderUtils.stopRecord();
                                voices.clear();
                                voices.add(recoderUtils.getFilePath());
                                isDown = false;
                            }
                            break;
                        default:
                    }
                }
                return false;
            }
        });
        deleteBtn = (Button) view.findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voices.clear();
                recorderTime.setText("0\"");
                handler.removeCallbacks(runnable);
                seekBar.setProgress(0);
                playBtn.setText("播放");
                if (recoderUtils.deleteRecorderFile()) {
                    ToastUtil.showToast(context, "删除成功");
                } else {
                    ToastUtil.showToast(context, "删除失败,文件不存在");
                }
            }
        });
        Bundle bundle = getArguments();
        if (bundle != null) {
            isOnlyPlay = bundle.getBoolean("isOnlyPlay", false);
            if (isOnlyPlay) {
                recorderBtn.setVisibility(View.GONE);
                deleteBtn.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onUpdate(double db) {
        if (null != recoderDialog && (System.currentTimeMillis() - startTime <= AudioRecoderUtils.MAX_LENGTH)) {
            int level = (int) db;
            recoderDialog.setLevel(level);
            recoderDialog.setTime(System.currentTimeMillis() - startTime);
            recorderTime.setText((System.currentTimeMillis() - startTime) / 1000 + "\"");
            seekBar.setMax((int) ((System.currentTimeMillis() - startTime) / 1000));
        } else if (recoderDialog != null && recoderDialog.isShowing()) {
            ToastUtil.showToast(context, "超过录音时长2分钟");
            recoderDialog.dismiss();
            recoderUtils.stopRecord();
            voices.clear();
            voices.add(recoderUtils.getFilePath());
            isDown = false;
            recorderTime.setText((System.currentTimeMillis() - startTime) / 1000 + "\"");
            seekBar.setMax((int) ((System.currentTimeMillis() - startTime) / 1000));
        }
    }

    private Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (seekBar.getProgress() != seekBar.getMax()) {
                seekBar.setProgress(seekBar.getProgress() + 1);
                handler.postDelayed(runnable, 1000);
                if (isOnlyPlay) {
                    recorderTime.setText(seekBar.getProgress() + "\"");
                }
            } else {
                seekBar.setProgress(0);
                playBtn.setText("播放");
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    public ArrayList<String> getVoices() {
        return voices;
    }

    public String getVoicesSec() {
        return recorderTime.getText().toString();
    }

    public void setPlay(String pathVoice) {
        recoderUtils = new AudioRecoderUtils(pathVoice);
        // 插入网络地址播放逻辑 start
        voices = new ArrayList<>();
        voices.add(pathVoice);
        if (pathVoice.startsWith("http") || pathVoice.startsWith("https")) {
            download(pathVoice);
        }
    }

    public void download(final String url) {
        FileDownUtil fileDownUtil = new FileDownUtil(context);
        fileDownUtil.url(url)
                .fileName(System.currentTimeMillis() + ".mp3")
                .download(new FileDownUtil.SimpleResponse() {
                    @Override
                    public void onResponse(File resource) {
                        localNetPathMap.put(url, resource.getPath());
                    }
                });
    }

    public void reset() {
        handler.removeCallbacks(runnable);
        seekBar.setProgress(0);
        recorderTime.setText("0\"");
        recoderDialog.setTime(0);
        voices.clear();
        if (recoderUtils != null) {
            recoderUtils.stopRecord();
            recoderUtils = null;
        }
        recoderUtils = new AudioRecoderUtils(context.getFilesDir());
        recoderUtils.setOnAudioStatusUpdateListener(this);
    }

    public void set(String path) {
        voices.clear();
        voices.add(path);
    }

    public void setSec(String sec) {
        this.sec = sec;
        recorderTime.setText(sec);
    }

    public void setText(String sec) {
        recorderTime.setText(sec);
    }

    public void setEnable(boolean flag) {
        this.flag = flag;
        if (flag) {
            recorderBtn.setClickable(false);
            deleteBtn.setClickable(false);
            playBtn.setClickable(false);
        } else {
            recorderBtn.setClickable(true);
            deleteBtn.setClickable(true);
            playBtn.setClickable(true);
        }
    }

    public void setDelete() {
        voices.clear();
        recorderTime.setText("0\"");
        handler.removeCallbacks(runnable);
        seekBar.setProgress(0);
        playBtn.setText("播放");
        if (recoderUtils.deleteRecorderFile()) {
            ToastUtil.showToast(context, "删除成功");
        } else {
            ToastUtil.showToast(context, "删除失败,文件不存在");
        }
    }
}
