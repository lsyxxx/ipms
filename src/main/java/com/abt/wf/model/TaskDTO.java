package com.abt.wf.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description 查询流程act_相关表返回结果
 */
@Data
public class TaskDTO {
    //-- processInstance
    private String processInstanceId;
    private String businessKey;
    private LocalDateTime processStartTime;
    private LocalDateTime processEndTime;
    private String startUserid;
    private String startUsername;
    private String state;
    private String processDeleteReason;

    //--- task
    private String taskInstanceId;
    private String taskDefName;
    private String taskDefKey;
    private String assigneeId;
    private String assigneeName;
    private LocalDateTime taskStartTime;
    private LocalDateTime taskEndTime;
    private String taskDeleteReason;
    private String taskDescription;


    //--- processDefinition
    private String processDefinitionId;
    private String processDefinitionKey;

}
