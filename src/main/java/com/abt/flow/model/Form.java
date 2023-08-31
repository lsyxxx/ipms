package com.abt.flow.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * 流程表单
 */
@Data
@Accessors(chain = true)
public class Form {

    /**
     * 对应表单id
     * TODO: 自动生成表单
     */
    private String id;
    /**
     * 表单名称
     * TODO: 比如报销申请表单
     */
    private String name;

    /**
     * 表单说明
     * TODO: 比如：报销<5000流程，申请表单
     */
    private String description;

    /**
     * 表单对应的业务类型代码，比如报销
     */
    private String bizCode;

    /**
     * 业务类型Id
     */
    private String bizId;

    private String bizName;

    /**
     * 流程定义ID
     */
    private String procDefId;

    /**
     * 用于流程的key，唯一
     */
    private String businessKey;
}
