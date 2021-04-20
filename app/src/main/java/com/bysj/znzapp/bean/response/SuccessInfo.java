package com.bysj.znzapp.bean.response;

import lombok.Data;

/**
 * @Author: huowei
 * @CreateDate: 2021/4/12 13:46
 * @Describe:
 * @Version: 1.0.0
 */
@Data
public class SuccessInfo {
    /**
     * isValid : true
     * validaionMessage :
     * id : 92c170ef-417e-4238-a45d-54412428f2aa
     */

    private boolean isValid;
    private String validaionMessage;
    private String id;
    private String errorCode;
    private boolean addFlag;
    private String deptName;
}
