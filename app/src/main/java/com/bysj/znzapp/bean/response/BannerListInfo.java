package com.bysj.znzapp.bean.response;

import com.thirdsdks.xbanner.entity.BaseBannerInfo;

import lombok.Data;

/**
 * @Author: huowei
 * @CreateDate: 2021/3/22 19:14
 * @Describe: 轮播图
 * @Version: 1.0.0
 */
@Data
public class BannerListInfo implements BaseBannerInfo {
    private String name;
    private String url;

    @Override
    public Object getXBannerUrl() {
        return url;
    }

    @Override
    public String getXBannerTitle() {
        return name;
    }
}
