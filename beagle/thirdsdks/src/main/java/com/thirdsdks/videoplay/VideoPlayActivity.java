package com.thirdsdks.videoplay;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;

import com.beagle.component.activity.BaseCompatActivity;
import com.beagle.component.app.AppManager;

import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;

import com.thirdsdks.R;

import org.json.JSONObject;

/**
 * Created by zhaotao on 2021/01/05 11:30.
 */

public class VideoPlayActivity extends BaseCompatActivity {

    NestedScrollView postDetailNestedScroll;

    // 推荐使用StandardGSYVideoPlayer，功能一致
    // CustomGSYVideoPlayer部分功能处于试验阶段
    LandLayoutVideo detailPlayer;
    RelativeLayout activityDetailPlayer;
    private boolean isPlay;
    private boolean isPause;
    private OrientationUtils orientationUtils;
    private TextView tv_name;
    private TextView tv_name1;
    private TextView tv_code;
    private TextView tv_platform;
    private TextView tv_grid;
    private TextView tv_depart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Activity activity = AppManager.currentActivity();
        if (activity instanceof VideoPlayActivity) {
            AppManager.delActivity(activity);
            activity.finish();
            overridePendingTransition(0, 0);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        initHead();
        initView();

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        String data = intent.getStringExtra("data");

        try {
            //{"OBJECTID":"2546","BUS_ID":"2546","GRIDID":"1143904946618368","NAME":"01495城东米行弄-老广电门口北向南",
            // "CODE":"330522020002727760","BELONGS":"大华","ZGBMMC":"城东派出所","GRIDNAME":"小西门社区第一网格","SHAPE":"Point"}
            JSONObject jsonObject = new JSONObject(data);
            tv_name.setText(StringUtil.notNull(jsonObject.optString("NAME")));
            tv_name1.setText(StringUtil.notNull(jsonObject.optString("NAME")));
            tv_code.setText(StringUtil.notNull(jsonObject.optString("CODE")));
            tv_platform.setText(StringUtil.notNull(jsonObject.optString("BELONGS")));
            tv_grid.setText(StringUtil.notNull(jsonObject.optString("GRIDNAME")));
            tv_depart.setText(StringUtil.notNull(jsonObject.optString("ZGBMMC")));

        } catch (Exception e) {
            e.printStackTrace();
        }


        //增加封面
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        resolveNormalVideoUI();

        //外部辅助的旋转，帮助全屏
        orientationUtils = new OrientationUtils(this, detailPlayer);
        //初始化不打开外部的旋转
        orientationUtils.setEnable(false);

        GSYVideoOptionBuilder gsyVideoOption = new GSYVideoOptionBuilder();
        gsyVideoOption.setThumbImageView(imageView)
                .setIsTouchWiget(true)
                .setRotateViewAuto(false)
                .setLockLand(false)
                .setShowFullAnimation(false)
                .setNeedLockFull(true)
                .setSeekRatio(1)
                .setUrl(url)
                .setCacheWithPlay(true)
                .setVideoTitle(tv_name.getText().toString())
                .setStandardVideoAllCallBack(new SampleListener() {
                    @Override
                    public void onPrepared(String url, Object... objects) {
                        Debuger.printfError("***** onPrepared **** " + objects[0]);
                        Debuger.printfError("***** onPrepared **** " + objects[1]);
                        super.onPrepared(url, objects);
                        //开始播放了才能旋转和全屏
                        orientationUtils.setEnable(true);
                        isPlay = true;
                    }

                    @Override
                    public void onEnterFullscreen(String url, Object... objects) {
                        super.onEnterFullscreen(url, objects);
                        Debuger.printfError("***** onEnterFullscreen **** " + objects[0]);//title
                        Debuger.printfError("***** onEnterFullscreen **** " + objects[1]);//当前全屏player
                    }

                    @Override
                    public void onAutoComplete(String url, Object... objects) {
                        super.onAutoComplete(url, objects);
                    }

                    @Override
                    public void onClickStartError(String url, Object... objects) {
                        super.onClickStartError(url, objects);
                    }

                    @Override
                    public void onQuitFullscreen(String url, Object... objects) {
                        super.onQuitFullscreen(url, objects);
                        Debuger.printfError("***** onQuitFullscreen **** " + objects[0]);//title
                        Debuger.printfError("***** onQuitFullscreen **** " + objects[1]);//当前非全屏player
                        if (orientationUtils != null) {
                            orientationUtils.backToProtVideo();
                        }
                    }
                })
                .setLockClickListener(new LockClickListener() {
                    @Override
                    public void onClick(View view, boolean lock) {
                        if (orientationUtils != null) {
                            //配合下方的onConfigurationChanged
                            orientationUtils.setEnable(!lock);
                        }
                    }
                }).build(detailPlayer);

        detailPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //直接横屏
                orientationUtils.resolveByClick();

                //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
                detailPlayer.startWindowFullscreen(VideoPlayActivity.this, true, true);
            }
        });
    }


    @Override
    protected void initHead() {
        getCenterTextView().setText("视频监控");
    }

    @Override
    protected void initView() {
        postDetailNestedScroll = (NestedScrollView) findViewById(R.id.post_detail_nested_scroll);
        detailPlayer = (LandLayoutVideo) findViewById(R.id.detail_player);
        activityDetailPlayer = (RelativeLayout) findViewById(R.id.activity_detail_player);

        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_name1 = (TextView) findViewById(R.id.tv_name1);
        tv_code = (TextView) findViewById(R.id.tv_code);
        tv_platform = (TextView) findViewById(R.id.tv_platform);
        tv_grid = (TextView) findViewById(R.id.tv_grid);
        tv_depart = (TextView) findViewById(R.id.tv_depart);
    }

    @Override
    public void onBackPressed() {

        if (orientationUtils != null) {
            orientationUtils.backToProtVideo();
        }

        if (StandardGSYVideoPlayer.backFromWindowFull(this)) {
            return;
        }
        super.onBackPressed();
    }


    @Override
    protected void onPause() {
        getCurPlay().onVideoPause();
        super.onPause();
        isPause = true;
    }

    @Override
    protected void onResume() {
        getCurPlay().onVideoResume();
        super.onResume();
        isPause = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isPlay) {
            getCurPlay().release();
        }
        //GSYPreViewManager.instance().releaseMediaPlayer();
        if (orientationUtils != null)
            orientationUtils.releaseListener();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //如果旋转了就全屏
        if (isPlay && !isPause) {
            detailPlayer.onConfigurationChanged(this, newConfig, orientationUtils);
        }
    }


    private void resolveNormalVideoUI() {
        //增加title
        detailPlayer.getTitleTextView().setVisibility(View.GONE);
        detailPlayer.getBackButton().setVisibility(View.GONE);
    }

    private GSYVideoPlayer getCurPlay() {
        if (detailPlayer.getFullWindowPlayer() != null) {
            return detailPlayer.getFullWindowPlayer();
        }
        return detailPlayer;
    }

}
