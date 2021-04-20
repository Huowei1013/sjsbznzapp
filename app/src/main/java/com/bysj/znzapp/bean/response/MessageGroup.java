package com.bysj.znzapp.bean.response;

import lombok.Data;

/**
 * @Author: huowei
 * @CreateDate: 2021/3/23 10:44
 * @Describe:
 * @Version: 1.0.0
 */
@Data
public class MessageGroup implements Comparable<MessageGroup> {
    /**
     * bId : ae4c4753-b1dd-4dda-8fc4-4fe5e74dfd69
     * bType : event-todomessage
     * content : null
     * createDate : 2017-11-09 12:02:41
     * createUser : 系统
     * readTime : null
     * title : 您收到一条新的待办事项
     * unReadCount : 539
     */

    private String bId;
    private String type;
    private String content;
    private String createDate;
    private String createUser;
    private String readTime;
    private String title;
    private int unReadCount;

    @Override
    public int compareTo(MessageGroup messageGroup) {
        return Integer.valueOf(messageGroup.getUnReadCount()).compareTo(this.getUnReadCount());
    }
}
