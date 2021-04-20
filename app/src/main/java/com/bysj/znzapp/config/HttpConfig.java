package com.bysj.znzapp.config;


import com.bysj.znzapp.BuildConfig;

/**
 * @Author: huowei
 * @CreateDate: 2021/3/22 16:27
 * @Describe:
 * @Version: 1.0.0
 */
public class HttpConfig {
    // 公共路径
    public static final String host = BuildConfig.host;
    private static final String commonPath = "xssjrd/app/rest/";

    // 访问地址（因为有些接口是直接访问host的，没有公用路径）
    public static final String url = host + commonPath;

    // Token名称
    public static final String bgToken = "bgToken";
    //*************************************系统**************************************
    // 检查版本号
//    public static final String checkVersion = url + "system/checkVersion";//"index/versionCheck";
    //最新检测版本地址
    public static final String checkVersion = url + "system/checkVersionV2";//"index/versionCheck";
    // 配置
    public static final String getConfig = url + "system/getConfig";
    // 文件上传（通过getConfig获得）
    public static String fileUploadServer = "";// bgfile/upload
    // 文件下载（通过getConfig获得）
    public static String fileDownloadServer = "";// bgfile/docs
    // 轮播图
    public static final String getBannersList = url + "system/getBannerList";

    //*************************************登录页**************************************
    // 用户登陆
    public static final String login = host + "userInfo/login";
    // 用户注销
    public static final String logOut = url + "user/logout";
    // 用户信息
    public static final String getUserInfo = host + "userInfo/getUserInfo";

    //*************************************消息功能*************************************
    // 消息分组列表
    public static final String getMessageByGroup = url + "message/getMessageByGroup";
    // 消息未读数量
    public static final String getUnReadMessageCount = url + "message/getUnReadMessageCount";
    // 全部标记已读
    public static final String markAllMessagekAsRead = url + "message/markAllMessagekAsRead";
    // 消息标记已读
    public static final String markOneMessageAsRead = url + "message/markOneMessageAsRead";
    // 象山抗旱监测
    public static final String xiangshanWaterServiceUrl = "http://115.231.59.150:10036/";

    public static String userInfoSave = host+"userInfo/doSave";
    public static String editPassWord = host+"userInfo/editPassWord";
    public static String saveEvent = host+"eventInfo/doSave";
    public static String getEventList =host+ "eventInfo/selectList";
    public static String getEventDetail=host+ "eventInfo/details";
}
