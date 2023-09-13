package com.abt.flow.model;

import lombok.Data;

/**
 * 流程信息VO
 * 用于传递前端查看流程列表(待办/已办列表)
 */
@Data
public class FlowInfoVo {


    /**
     * 流程实例id
     */
    private String procInstId;
    /**
     * customName
     */
    private String customName;
    /**
     * 流程类型代码
     */
    private String flowCode;
    private String flowName;
    private String activityName;
    private String description;
    private String state;


}
