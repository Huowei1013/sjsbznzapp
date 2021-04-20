package com.bysj.znzapp.bean.response;

import lombok.Data;

/**
 * @Author: huowei
 * @CreateDate: 2021/4/12 14:28
 * @Describe:
 * @Version: 1.0.0
 */
@Data
public class TodoDetailInfo {

    /**
     * 主键自增
     */
    private Integer id;

    /**
     * 事件编号
     */
    private String eventId;

    /**
     * 事件地址
     */
    private String eventAddress;

    /**
     * 事件类型
     */
    private String eventType;

    /**
     * 事件时间
     */
    private String eventTime;

    /**
     * 事件人数
     */
    private Integer eventNumber;

    /**
     * 事件场所
     */
    private String eventOrg;

    /**
     * 事件人员
     */
    private String eventPerson;

    /**
     * 事件详情
     */
    private String eventContent;

    /**
     * 事件详情
     */
    private String eventCreateTime;

}
