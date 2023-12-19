package com.abt.wf.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @description 查询流程act_相关表返回结果
 */
@Data
public class TaskDTO {
    //-- processInstance
    private String processInstanceId;
    private String businessKey;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime processStartTime;
//    private String processStartTimeStr;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime processEndTime;
//    private String processEndTimeStr;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime taskStartTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime taskEndTime;
    private String taskDeleteReason;
    private String taskDescription;


    //--- processDefinition
    private String processDefinitionId;
    private String processDefinitionKey;

}
