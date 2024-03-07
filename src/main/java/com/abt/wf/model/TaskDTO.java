package com.abt.wf.model;

import com.abt.common.util.TimeUtil;
import com.abt.wf.service.impl.WorkFlowExecutionServiceImpl;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.task.Comment;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 查询流程act_相关表返回结果
 */
@Data
public class TaskDTO {

    //-- processInstance
    private String processInstanceId;
    private String businessKey;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime processStartTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime processEndTime;
    private String startUserid;
    private String startUsername;
    private String state;
    private String stateDesc;
    private String processDeleteReason;

    //--- processDefinition
    private String processDefinitionId;
    private String processDefinitionKey;


    //--- task
    private String taskInstanceId;
    private String taskDefName;
    private String taskDefKey;
    private String assigneeId;
    private String assigneeName;
    private String taskResult;      //节点审批结果
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime taskStartTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime taskEndTime;
    private String taskDeleteReason;
    private String taskDescription;


    //-- comment
    private List<Comment> comments;

    public static TaskDTO from(HistoricTaskInstance task) {
        TaskDTO dto = new TaskDTO();
        dto.setProcessInstanceId(task.getProcessInstanceId());
        dto.setProcessDefinitionKey(task.getProcessDefinitionKey());
        dto.setProcessDefinitionId(task.getProcessDefinitionId());
        dto.setTaskInstanceId(task.getId());
        dto.setTaskDefName(task.getName());
        dto.setTaskDefKey(task.getTaskDefinitionKey());
        dto.setTaskStartTime(TimeUtil.from(task.getStartTime()));
        dto.setTaskEndTime(TimeUtil.from(task.getEndTime()));
        dto.setTaskDescription(task.getDescription());
        dto.setTaskDeleteReason(task.getDeleteReason());
        dto.setAssigneeId(task.getAssignee());
        dto.convertDbState();
        return dto;
    }

    public String convertDbState() {
        if (this.state == null) {
            return "";
        }
        switch (this.state) {
            case HistoricProcessInstance.STATE_ACTIVE -> this.stateDesc = "审批中";
            case HistoricProcessInstance.STATE_SUSPENDED -> this.stateDesc = "已挂起";
            case HistoricProcessInstance.STATE_COMPLETED ->this.stateDesc = "已通过";
            case HistoricProcessInstance.STATE_INTERNALLY_TERMINATED -> {
                if (this.processDeleteReason != null && this.processDeleteReason.contains(WorkFlowExecutionServiceImpl.DELETE_REASON_REJECT_BY)) {
                    this.stateDesc = "已拒绝";
                }
            }
            default -> this.stateDesc = state;
        }
        return this.stateDesc;
    }

}
