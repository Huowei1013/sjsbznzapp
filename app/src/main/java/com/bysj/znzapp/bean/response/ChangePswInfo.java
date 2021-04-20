package com.bysj.znzapp.bean.response;

import lombok.Data;

/**
 * @Author: huowei
 * @CreateDate: 2021/4/11 12:12
 * @Describe:
 * @Version: 1.0.0
 */
@Data
public class ChangePswInfo {
    private boolean isValid;
    private String validaionMessage;
}
