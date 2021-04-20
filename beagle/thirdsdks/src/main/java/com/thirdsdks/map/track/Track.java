package com.thirdsdks.map.track;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.util.Log;

import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.LBSTraceService;
import com.baidu.trace.api.entity.LocRequest;
import com.baidu.trace.api.entity.OnEntityListener;
import com.baidu.trace.api.track.LatestPointRequest;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.model.BaseRequest;
import com.baidu.trace.model.LocationMode;
import com.baidu.trace.model.OnCustomAttributeListener;
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.model.ProcessOption;
import com.baidu.trace.model.PushMessage;
import com.baidu.trace.model.StatusCodes;

import com.beagle.component.logger.LogCat;

import com.thirdsdks.map.track.utils.AlarmUtils;
import com.thirdsdks.map.track.utils.BitmapUtil;
import com.thirdsdks.map.track.utils.CommonUtil;
import com.thirdsdks.map.track.utils.Constants;
import com.thirdsdks.map.track.utils.NetUtil;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhaotao on 2021/01/05 18:13.
 */

public class Track {

    private AtomicInteger mSequenceGenerator = new AtomicInteger();
    private LocRequest locRequest = null;
    public Context mContext = null;
    public SharedPreferences trackConf = null;
    /**
     * 轨迹客户端
     */
    public LBSTraceClient mClient = null;
    /**
     * 轨迹服务
     */
    public com.baidu.trace.Trace mTrace = null;
    /**
     * 轨迹服务ID，测试环境
     */
    public long serviceId = 0;//149274
    /**
     * Entity标识
     */
    public String entityName = "myTrace";

    public boolean isRegisterReceiver = false;
    /**
     * 服务是否开启标识
     */
    public boolean isTraceStarted = false;
    /**
     * 采集是否开启标识
     */
    public boolean isGatherStarted = false;
    private OnTraceListener traceListener;
    private PowerManager powerManager = null;
    private PowerManager.WakeLock wakeLock = null;
    private TrackReceiver trackReceiver = null;
    public static int screenWidth = 0;
    public static int screenHeight = 0;

    public Track(Context context) {
        LogCat.d("track init");
        mContext = context;
        trackConf = mContext.getSharedPreferences("track_conf", Context.MODE_WORLD_READABLE);
        entityName = trackConf.getString("lastUserId", CommonUtil.getImei(mContext));
        LogCat.d("entityName is " + entityName);
        BitmapUtil.init();
        // 若为创建独立进程，则不初始化成员变量
        if ("com.space.grid:track".equals(CommonUtil.getCurProcessName(mContext)) || "com.space.grid.debug:track".equals(CommonUtil.getCurProcessName(mContext))) {
            return;
        }
        initView();
        ComponentName cn = new ComponentName(context, LBSTraceService.class);
        ServiceInfo info = null;
        try {
            info = context.getPackageManager()
                    .getServiceInfo(cn, PackageManager.GET_META_DATA);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (info != null) {
            serviceId = info.metaData.getInt("com.baidu.trace.eye");
            LogCat.e("--------------------  " + serviceId);
        }
        mClient = new LBSTraceClient(mContext);
        mTrace = new com.baidu.trace.Trace(serviceId, entityName);
        locRequest = new LocRequest(serviceId);
        mClient.setLocationMode(LocationMode.High_Accuracy);
        changeInterval(trackConf.getBoolean("workTrack", false));
        mClient.setInterval(Constants.DEFAULT_GATHER_INTERVAL, Constants.DEFAULT_PACK_INTERVAL);
        mTrace.setNeedObjectStorage(false);
        powerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        clearTraceStatus();
        LogCat.d("track 实例化");
        mClient.setOnCustomAttributeListener(new OnCustomAttributeListener() {
            @Override
            public Map<String, String> onTrackAttributeCallback() {
                Log.d("track", "onTrackAttributeCallback ");
                return null;
            }

            @Override
            public Map<String, String> onTrackAttributeCallback(long l) {
                Log.d("track", "onTrackAttributeCallback long " + l + " entityName is" + entityName);
                return null;
            }
        });

        mClient.setOnTraceListener(new OnTraceListener() {
            @Override
            public void onBindServiceCallback(int i, String s) {

            }

            @Override
            public void onStartTraceCallback(int i, String s) {
                Log.d("track", "onStartTraceCallback " + " i " + i + " s" + s);
            }

            @Override
            public void onStopTraceCallback(int i, String s) {
                Log.d("track", "onStopTraceCallback " + " i " + i + " s" + s);
            }

            @Override
            public void onStartGatherCallback(int i, String s) {
                Log.d("track", "onStartGatherCallback " + " i " + i + " s" + s);
            }

            @Override
            public void onStopGatherCallback(int i, String s) {
                Log.d("track", "onStopGatherCallback " + " i " + i + " s" + s);
            }

            @Override
            public void onPushCallback(byte b, PushMessage pushMessage) {
                Log.d("track", "onPushCallback " + "pushMessage " + pushMessage.getMessage());
            }

            @Override
            public void onInitBOSCallback(int i, String s) {

            }
        });
    }

    /**
     * 获取当前位置
     */
    public void getCurrentLocation(OnEntityListener entityListener, OnTrackListener trackListener) {
        // 网络连接正常，开启服务及采集，则查询纠偏后实时位置；否则进行实时定位
        if (NetUtil.isNetworkAvailable(mContext)
                && trackConf.contains("is_trace_started")
                && trackConf.contains("is_gather_started")
                && trackConf.getBoolean("is_trace_started", false)
                && trackConf.getBoolean("is_gather_started", false)) {
            LatestPointRequest request = new LatestPointRequest(getTag(), serviceId, entityName);
            ProcessOption processOption = new ProcessOption();
            processOption.setNeedDenoise(true);
            processOption.setRadiusThreshold(100);
            request.setProcessOption(processOption);
            mClient.queryLatestPoint(request, trackListener);
            LogCat.d("queryLatestPoint ");
        } else {
            mClient.queryRealTimeLoc(locRequest, entityListener);
            LogCat.d("queryRealTimeLoc ");
        }
    }

    private void initView() {
        initListener();
        getScreenSize();
    }

    /**
     * 获取屏幕尺寸
     */
    private void getScreenSize() {
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        screenHeight = dm.heightPixels;
        screenWidth = dm.widthPixels;
    }

    /**
     * 清除Trace状态：初始化app时，判断上次是正常停止服务还是强制杀死进程，根据trackConf中是否有is_trace_started字段进行判断。
     * <p>
     * 停止服务成功后，会将该字段清除；若未清除，表明为非正常停止服务。
     */
    private void clearTraceStatus() {
        if (trackConf.contains("is_trace_started") || trackConf.contains("is_gather_started")) {
            SharedPreferences.Editor editor = trackConf.edit();
            editor.remove("is_trace_started");
            editor.remove("is_gather_started");
            editor.apply();
        }
    }

    /**
     * 初始化请求公共参数
     *
     * @param request
     */
    public void initRequest(BaseRequest request) {
        request.setTag(getTag());
        request.setServiceId(serviceId);
    }

    /**
     * 获取请求标识
     *
     * @return
     */
    public int getTag() {
        return mSequenceGenerator.incrementAndGet();
    }

    public void updateTraceInfo(String userId) {
        entityName = userId;
        mTrace = new com.baidu.trace.Trace(serviceId, userId);
        SharedPreferences.Editor editor = trackConf.edit();
        editor.putString("lastUserId", userId);
        editor.apply();
        LogCat.d("entityName is " + entityName);
    }

    public void startOrStopTrackService(boolean start) {
        LogCat.d("startOrStopTrackService  " + start);
        LogCat.d("isTraceStarted  " + isTraceStarted);
        LogCat.d("startOrStopTrackService entityName is " + entityName);
        if (!start) {
            mClient.stopTrace(mTrace, traceListener);
            mContext.stopService(new Intent(mContext, LBSTraceService.class));
            LogCat.d("stopTrace  ");
        } else {
            mClient.startTrace(mTrace, traceListener);
            LogCat.d("startTrace  ");
        }
    }

    public void startOrStopGather(boolean start) {
        LogCat.d("startOrStopGather  " + start);
        LogCat.d("isGatherStarted  " + isGatherStarted);
        if (!start) {
            mClient.stopGather(traceListener);
        } else {
            mClient.startGather(traceListener);
        }
    }

    public void tryStartService(final String userId) {
        // 由于启动和关闭service是异步的同时耗时3秒左右，防止用户退出登录后又快速点击登陆，造成service状态混乱
        long time = System.currentTimeMillis();
        updateTraceInfo(userId);
        LogCat.d("time is " + AlarmUtils.getTime(time));
        LogCat.d("beginTime is " + AlarmUtils.getTime(AlarmUtils.getBeginTimeInDay()));
        LogCat.d("endTime is " + AlarmUtils.getTime(AlarmUtils.getEndTimeInDay()));
        startOrStopTrackService(true);
    }

    public void tryStartService() {
        if (mTrace != null) {
            mTrace.setEntityName(trackConf.getString("lastUserId", ""));
        }
        startOrStopTrackService(true);
    }

    public void tryStopService() {
        startOrStopTrackService(false);
    }

    /**
     * 注册广播（电源锁、GPS状态）
     */
    private void registerReceiver() {
        if (isRegisterReceiver) {
            return;
        }

        if (null == wakeLock) {
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "track upload");
        }
        if (null == trackReceiver) {
            trackReceiver = new TrackReceiver(wakeLock);
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(StatusCodes.GPS_STATUS_ACTION);
        mContext.registerReceiver(trackReceiver, filter);
        isRegisterReceiver = true;

    }

    private void unregisterPowerReceiver() {
        if (!isRegisterReceiver) {
            return;
        }
        if (null != trackReceiver) {
            mContext.unregisterReceiver(trackReceiver);
        }
        isRegisterReceiver = false;
    }

    public void changeInterval(boolean fast) {
        mClient.setInterval(fast ? Constants.FAST_GATHER_INTERVAL : Constants.FAST_GATHER_INTERVAL, fast ? Constants.FAST_PACK_INTERVAL : Constants.DEFAULT_PACK_INTERVAL);
    }

    private void initListener() {

        traceListener = new OnTraceListener() {

            /**
             * 绑定服务回调接口
             * @param errorNo  状态码
             * @param message 消息
             *                <p>
             *                <pre>0：成功 </pre>
             *                <pre>1：失败</pre>
             */
            @Override
            public void onBindServiceCallback(int errorNo, String message) {
                LogCat.d(" track onBindServiceCallback " + message);
            }

            /**
             * 开启服务回调接口
             * @param errorNo 状态码
             * @param message 消息
             *                <p>
             *                <pre>0：成功 </pre>
             *                <pre>10000：请求发送失败</pre>
             *                <pre>10001：服务开启失败</pre>
             *                <pre>10002：参数错误</pre>
             *                <pre>10003：网络连接失败</pre>
             *                <pre>10004：网络未开启</pre>
             *                <pre>10005：服务正在开启</pre>
             *                <pre>10006：服务已开启</pre>
             */
            @Override
            public void onStartTraceCallback(int errorNo, String message) {
                LogCat.d("onStartTraceCallback " + message);
                if (StatusCodes.SUCCESS == errorNo || StatusCodes.START_TRACE_NETWORK_CONNECT_FAILED <= errorNo) {
                    isTraceStarted = true;
                    LogCat.d("onStartTraceCallback  entityName is " + mTrace.getEntityName());
                    SharedPreferences.Editor editor = trackConf.edit();
                    editor.putBoolean("is_trace_started", true);
                    editor.apply();
                    startOrStopGather(true);
                    registerReceiver();
                }
            }

            /**
             * 停止服务回调接口
             * @param errorNo 状态码
             * @param message 消息
             *                <p>
             *                <pre>0：成功</pre>
             *                <pre>11000：请求发送失败</pre>
             *                <pre>11001：服务停止失败</pre>
             *                <pre>11002：服务未开启</pre>
             *                <pre>11003：服务正在停止</pre>
             */
            @Override
            public void onStopTraceCallback(int errorNo, String message) {
                LogCat.d("onStopTraceCallback " + message);
                if (StatusCodes.SUCCESS == errorNo || StatusCodes.CACHE_TRACK_NOT_UPLOAD == errorNo) {
                    isTraceStarted = false;
                    isGatherStarted = false;
                    // 停止成功后，直接移除is_trace_started记录（便于区分用户没有停止服务，直接杀死进程的情况）
                    SharedPreferences.Editor editor = trackConf.edit();
                    editor.remove("is_trace_started");
                    editor.remove("is_gather_started");
                    editor.apply();
//                    startOrStopGather(false);
                    unregisterPowerReceiver();
                }
            }

            /**
             * 开启采集回调接口
             * @param errorNo 状态码
             * @param message 消息
             *                <p>
             *                <pre>0：成功</pre>
             *                <pre>12000：请求发送失败</pre>
             *                <pre>12001：采集开启失败</pre>
             *                <pre>12002：服务未开启</pre>
             */
            @Override
            public void onStartGatherCallback(int errorNo, String message) {
                LogCat.d("onStartGatherCallback " + message + " errorNo " + errorNo);
                if (StatusCodes.SUCCESS == errorNo || StatusCodes.GATHER_STARTED == errorNo) {
                    isGatherStarted = true;
                    LogCat.d("onStartGatherCallback isGatherStarted " + isGatherStarted);
                    SharedPreferences.Editor editor = trackConf.edit();
                    editor.putBoolean("is_gather_started", true);
                    editor.apply();
                }
            }

            /**
             * 停止采集回调接口
             * @param errorNo 状态码
             * @param message 消息
             *                <p>
             *                <pre>0：成功</pre>
             *                <pre>13000：请求发送失败</pre>
             *                <pre>13001：采集停止失败</pre>
             *                <pre>13002：服务未开启</pre>
             */
            @Override
            public void onStopGatherCallback(int errorNo, String message) {
                LogCat.d("onStopGatherCallback " + message);
                if (StatusCodes.SUCCESS == errorNo || StatusCodes.GATHER_STOPPED == errorNo) {
                    isGatherStarted = false;
                    SharedPreferences.Editor editor = trackConf.edit();
                    editor.remove("is_gather_started");
                    editor.apply();
                }
            }

            /**
             * 推送消息回调接口
             *
             * @param messageType 状态码
             * @param pushMessage 消息
             *                  <p>
             *                  <pre>0x01：配置下发</pre>
             *                  <pre>0x02：语音消息</pre>
             *                  <pre>0x03：服务端围栏报警消息</pre>
             *                  <pre>0x04：本地围栏报警消息</pre>
             *                  <pre>0x05~0x40：系统预留</pre>
             *                  <pre>0x41~0xFF：开发者自定义</pre>
             */
            @Override
            public void onPushCallback(byte messageType, PushMessage pushMessage) {
                Log.d("track", "onPushCallback " + "pushMessage " + pushMessage.getMessage());
            }

            @Override
            public void onInitBOSCallback(int i, String s) {

            }
        };
    }
}
