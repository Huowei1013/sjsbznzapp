package com.bysj.znzapp.bean.response;

import java.util.List;

import lombok.Data;

/**
 * @Author: huowei
 * @CreateDate: 2021/4/12 13:58
 * @Describe:
 * @Version: 1.0.0
 */

public class MyReportInfo {

    /**
     * total : 1
     * rows : [{"id":"123","no":"案件办理-YPT20170612006","description":"..........","startDate":"2017-06-12 09:35:00"}]
     */

    private int total;
    private List<RowsBean> eventList;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<RowsBean> getRows() {
        return eventList;
    }

    public void setRows(List<RowsBean> rows) {
        this.eventList = rows;
    }
    @Data
    public static class RowsBean {

        /**
         * id : 123
         * no : 案件办理-YPT20170612006
         * description : ..........
         * startDate : 2017-06-12 09:35:00
         */

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
         * 事件时间
         */
        private String eventCreateTime;

        /**
         * 上报人
         */
        private String eventFactPerson;

    }
}
