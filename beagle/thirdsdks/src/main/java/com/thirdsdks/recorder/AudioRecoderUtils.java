package com.thirdsdks.recorder;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.SystemClock;

import java.io.File;

/**
 * Created by zhaotao on 2021/01/04 20:52.
 */

public class AudioRecoderUtils {

    private File folderDir;
    private String filePath = "";
    private MediaRecorder mMediaRecorder;
    public static final int MAX_LENGTH = 1000 * 60 * 2;// 最大录音时长1000*60*10;
    private MediaPlayer mMediaPlayer;

    private OnAudioStatusUpdateListener audioStatusUpdateListener;

    public AudioRecoderUtils() {
        this.filePath = "/dev/null";
    }

    public AudioRecoderUtils(File folderDir) {
        this.folderDir = folderDir;
    }

    public String getFilePath() {
        return filePath;
    }

    public AudioRecoderUtils(String filePath) {
        this.filePath = filePath;
    }

    public void setPath(String filePath){
        this.filePath = filePath;
    }

    private long startTime;
    private long endTime;

    /**
     * 开始录音 使用amr格式
     * 录音文件
     *
     */
    public void startRecord() {
        // 开始录音
        /* ①Initial：实例化MediaRecorder对象 */
        if (mMediaRecorder == null)
            mMediaRecorder = new MediaRecorder();
        try {
            /* ②setAudioSource/setVedioSource */
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置麦克风
            /* ②设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default 声音的（波形）的采样 */
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            /*
             * ②设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default THREE_GPP(3gp格式
             * ，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
             */
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            /* ③准备 */
            filePath = folderDir.getAbsolutePath() + System.currentTimeMillis() + ".amr";
            mMediaRecorder.setOutputFile(filePath);
            mMediaRecorder.setMaxDuration(MAX_LENGTH);
            mMediaRecorder.prepare();
            /* ④开始 */
            mMediaRecorder.start();
            // AudioRecord audioRecord.
            /* 获取开始时间* */
            startTime = System.currentTimeMillis();
            updateMicStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止录音
     */
    public long stopRecord() {
        if (mMediaRecorder == null)
            return 0L;
        endTime = System.currentTimeMillis();
        long left = endTime - startTime;
        if (left < 1000) {
            SystemClock.sleep(1000 - left);
        }
        try {
            if (mMediaRecorder != null) {
                mMediaRecorder.stop();
                mMediaRecorder.reset();
                mMediaRecorder.release();
                mMediaRecorder = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mMediaRecorder != null) {
                mMediaRecorder.reset();
                mMediaRecorder.release();
                mMediaRecorder = null;
            }
        }
        return endTime - startTime;
    }

    /**
     * 删除录音文件
     */
    public boolean deleteRecorderFile() {
        File recorderFile = new File(filePath);
        return recorderFile.delete();
    }

    private final Handler mHandler = new Handler();

    private Runnable mUpdateMicStatusTimer = new Runnable() {
        public void run() {
            updateMicStatus();
        }
    };

    /**
     * 更新话筒状态
     */
    private int BASE = 1;
    private int SPACE = 100;// 间隔取样时间

    public void setOnAudioStatusUpdateListener(OnAudioStatusUpdateListener audioStatusUpdateListener) {
        this.audioStatusUpdateListener = audioStatusUpdateListener;
    }

    private void updateMicStatus() {
        if (mMediaRecorder != null) {
            double ratio = (double) mMediaRecorder.getMaxAmplitude() / BASE;
            double db = 0;// 分贝
            if (ratio > 1) {
                db = 20 * Math.log10(ratio);
                if (null != audioStatusUpdateListener) {
                    audioStatusUpdateListener.onUpdate(db);
                }
            }
            mHandler.postDelayed(mUpdateMicStatusTimer, SPACE);
        }
    }

    public interface OnAudioStatusUpdateListener {
        void onUpdate(double db);
    }

    public boolean isAudioPlaying(){
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlayAudio();
                }
            });
        }
        return mMediaPlayer.isPlaying();
    }

    public boolean startPlayAudio() {
        try {
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        stopPlayAudio();
                    }
                });
            }
            if (this.filePath != null && !this.filePath.equals("") && new File(this.filePath).exists()) {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.reset();
                }
                mMediaPlayer.setDataSource(this.filePath);
                mMediaPlayer.prepare();
                duration = mMediaPlayer.getDuration();
                mMediaPlayer.start();
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void stopPlayAudio() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private int duration;

    public int getDuration() {
        return duration;
    }
}
