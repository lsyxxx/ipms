package com.abt.flow.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
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
     * 申请说明
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
     * 同biz_flow_rel: customName
     */
    private String businessKey;

    private String processInstanceId;
    private String taskId;
    /**
     * 业务-流程关系表id
     */
    private String relationId;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    private String createUser;


    /**
     * 更新流程processInstance, taskId
     * @param processInstanceId 空则不更新
     * @param taskId
     */
    public Form updateProcess(String processInstanceId, String taskId) {
        if (StringUtils.hasLength(processInstanceId)) {
            this.processInstanceId = processInstanceId;
        }
        this.taskId = taskId;
        return this;
    }


    /**
     * 默认生成customName
     * [createUser] bizName createTime
     * @return
     */
    public String customName() {
        return String.format("[%s] %s %s", createUser, bizName, createTime);
    }


}
