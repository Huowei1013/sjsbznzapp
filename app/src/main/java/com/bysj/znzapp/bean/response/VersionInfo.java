package com.bysj.znzapp.bean.response;

import lombok.Data;

/**
 * @Author: huowei
 * @CreateDate: 2021/3/22 18:31
 * @Describe: 版本更新
 * @Version: 1.0.0
 */
@Data
public class VersionInfo {
    private boolean isLatest;
    private boolean needUpdate;
    private String latestVersion;
    private String nextVersion;
    private String address;
    private String description;
}
