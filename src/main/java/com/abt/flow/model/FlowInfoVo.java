package com.abt.flow.model;

import lombok.Data;

/**
 * 流程信息VO
 * 用于前端查看流程列表
 */
@Data
public class FlowInfoVo {


    /**
     * 流程实例id
     */
    private String procInstId;
    /**
     * 名称
     */
    private String name;
    /**
     * 流程类型代码
     */
    private String flowCode;
    private String activityName;
    private String description;
    private String state;


}
