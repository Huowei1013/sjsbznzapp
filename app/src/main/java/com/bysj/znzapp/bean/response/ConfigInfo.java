package com.bysj.znzapp.bean.response;

import lombok.Data;

/**
 * @Author: huowei
 * @CreateDate: 2021/3/22 18:39
 * @Describe: 获取配置文件
 * @Version: 1.0.0
 */
@Data
public class ConfigInfo {

    private String theme;
    private String fileUploadServer;
    private String fileDownloadServer;
}
