package com.thirdsdks.map.track.utils;

/**
 * Created by zhaotao on 2021/01/05 18:13.
 */

public final class Constants {

    public static final int REQUEST_CODE = 1;
    public static final int RESULT_CODE = 1;
    public static final int DEFAULT_RADIUS_THRESHOLD = 0;
    public static final int PAGE_SIZE = 5000;

    /**
     * 轨迹分析查询间隔时间（1分钟）
     */
    public static final int ANALYSIS_QUERY_INTERVAL = 60;

    /**
     * 停留点默认停留时间（1分钟）
     */
    public static final int STAY_TIME = 60;

    /**
     * 启动停留时间
     */
    public static final int SPLASH_TIME = 3000;

    /**
     * 默认采集周期
     */
    public static final int DEFAULT_GATHER_INTERVAL = 15;

    /**
     * 默认打包周期
     */
    public static final int DEFAULT_PACK_INTERVAL = 30;

    /**
     * 快速采集周期
     */
    public static final int FAST_GATHER_INTERVAL = 15;

    /**
     * 快速打包周期
     */
    public static final int FAST_PACK_INTERVAL = 30;

    /**
     * 实时定位间隔(单位:秒)
     */
    public static final int LOC_INTERVAL = 10;

    /**
     * 最后一次定位信息
     */
    public static final String LAST_LOCATION = "last_location";
}
