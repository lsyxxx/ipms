package com.abt.wf.entity;

import com.abt.common.util.TimeUtil;
import com.abt.wf.config.Constants;
import com.abt.wf.model.ActionEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.camunda.bpm.engine.task.Task;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.lang.Nullable;
import static com.abt.wf.config.Constants.*;
import static com.abt.wf.model.ActionEnum.AUTOPASS;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.concurrent.Flow;

/**
 *
 */
@Getter
@Setter
@NoArgsConstructor
@Table(name = "wf_opt_log")
@Entity
@DynamicInsert
@DynamicUpdate
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FlowOperationLog {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 业务id
     */
    @Column(name = "entity_id", columnDefinition="VARCHAR(128)")
    private String entityId;

    @Column(name="cb_id", columnDefinition = "VARCHAR(128)")
    private String creditBookId;

    /**
     * 业务名称
     */
    @Column(name="srv_name", columnDefinition="VARCHAR(128)")
    private String serviceName;

    @Column(name="action_", columnDefinition="VARCHAR(64)")
    private String action;
    /**
     * 节点顺序
     * 0开始，提交
     */
    @Column(name="sort_", columnDefinition="TINYINT")
    private int sort = 0;

    /**
     * 操作人id
     * 抄送则是: system/系统
     */
    @Column(name="opt_id", columnDefinition="VARCHAR(128)")
    private String operatorId;
    @Column(name="opt_name", columnDefinition="VARCHAR(128)")
    private String operatorName;

    /**
     * 描述
     */
    @Column(name="desc_", columnDefinition="VARCHAR(1000)")
    private String description;

    //-- process
    @Column(name="proc_inst_id", columnDefinition="VARCHAR(128)")
    private String processInstanceId;
    @Column(name="proc_def_id", columnDefinition="VARCHAR(128)")
    private String processDefinitionId;
    @Column(name="proc_def_key", columnDefinition="VARCHAR(128)")
    private String processDefinitionKey;

    //-- task
    @Column(name = "task_inst_id", columnDefinition="VARCHAR(128)")
    private String taskInstanceId;
    @Column(name="task_name", columnDefinition="VARCHAR(128)")
    private String taskName;
    @Column(name="task_def_key", columnDefinition="VARCHAR(128)")
    private String taskDefinitionKey;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name="task_start_time")
    private LocalDateTime taskStartTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name="task_end_time")
    private LocalDateTime taskEndTime;
    /**
     * 节点审批结果
     */
    @Column(name="task_result", columnDefinition="VARCHAR(128)")
    private String taskResult;

    @Column(name="comment", columnDefinition="VARCHAR(1000)")
    private String comment;


    /**
     * 确认事项，json字符串保存; 可能有多个确认事项
     * {"事项确认": "本人确认对本事项。。。", "流程规范确认": "确认内容"}
     */
    @Column(name="check_items", columnDefinition = "VARCHAR(MAX)")
    private String checkItems;

    /**
     * 补充附件
     */
    @Column(name="file_json", columnDefinition = "VARCHAR(5000)")
    private String fileJson;


    @Transient
    private String signatureBase64;

    public static FlowOperationLog create(String operatorId, String operatorName, String processInstanceId,
                                          String processDefinitionId, String processDefinitionKey,
                                          String serviceName) {
        FlowOperationLog optLog = new FlowOperationLog();
        optLog.setOperatorId(operatorId);
        optLog.setOperatorName(operatorName);
        optLog.setProcessInstanceId(processInstanceId);
        optLog.setProcessDefinitionId(processDefinitionId);
        optLog.setProcessDefinitionKey(processDefinitionKey);
        optLog.setServiceName(serviceName);
        return optLog;
    }

    public static FlowOperationLog create(String operatorId, String operatorName,
                                          WorkflowBase form) {
        FlowOperationLog optLog = new FlowOperationLog();
        optLog.setOperatorId(operatorId);
        optLog.setOperatorName(operatorName);
        optLog.setProcessInstanceId(form.getProcessInstanceId());
        optLog.setProcessDefinitionId(form.getProcessDefinitionId());
        optLog.setProcessDefinitionKey(form.getProcessDefinitionKey());
        optLog.setServiceName(form.getServiceName());
        return optLog;
    }



    /**
     * 用户申请log
     */
    public static FlowOperationLog applyLog(String operatorId, String operatorName, WorkflowBase form, Task task, String entityId) {
        FlowOperationLog optLog = FlowOperationLog.create(operatorId, operatorName, form);
        optLog.setEntityId(entityId);
        optLog.setTaskInstanceId(task.getId());
        optLog.setTaskName(task.getName());
        optLog.setTaskStartTime(TimeUtil.from(task.getCreateTime()));
        optLog.setTaskEndTime(LocalDateTime.now());
        optLog.setAction(ActionEnum.APPLY.name());
        return optLog;
    }

    /**
     * 用户审批通过
     */
    public static FlowOperationLog passLog(String operatorId, String operatorName, WorkflowBase form, Task task, String entityId) {
        FlowOperationLog optLog = FlowOperationLog.create(operatorId, operatorName, form);
        optLog.setEntityId(entityId);
        optLog.setTaskInstanceId(task.getId());
        optLog.setTaskName(task.getName());
        optLog.setTaskStartTime(TimeUtil.from(task.getCreateTime()));
        optLog.setTaskEndTime(LocalDateTime.now());
        optLog.setAction(ActionEnum.PASS.name());
        return optLog;
    }

    public static FlowOperationLog rejectLog(String operatorId, String operatorName, WorkflowBase form, Task task, String entityId) {
        FlowOperationLog optLog = FlowOperationLog.create(operatorId, operatorName,
                form.getProcessInstanceId(), form.getProcessDefinitionId(), form.getProcessDefinitionKey(),
                form.getServiceName());
        optLog.setEntityId(entityId);
        optLog.setTaskInstanceId(task.getId());
        optLog.setTaskName(task.getName());
        optLog.setTaskStartTime(TimeUtil.from(task.getCreateTime()));
        optLog.setTaskEndTime(LocalDateTime.now());
        optLog.setAction(ActionEnum.REJECT.name());
        return optLog;
    }

    public static FlowOperationLog revokeLog(String operatorId, String operatorName, WorkflowBase form, String entityId) {
        FlowOperationLog optLog = FlowOperationLog.create(operatorId, operatorName,
                form.getProcessInstanceId(), form.getProcessDefinitionId(), form.getProcessDefinitionKey(),
                form.getServiceName());
        optLog.setEntityId(entityId);
        optLog.setTaskName("用户撤销");
        optLog.setTaskStartTime(LocalDateTime.now());
        optLog.setTaskEndTime(LocalDateTime.now());
        optLog.setAction(ActionEnum.REVOKE.name());
        optLog.setComment("系统：用户主动撤销");
        optLog.setTaskResult(STATE_DETAIL_REVOKE);
        return optLog;
    }

    public static FlowOperationLog autoPassLog(WorkflowBase form, Task task, String entityId) {
        FlowOperationLog optLog = FlowOperationLog.create(TERMINATE_SYS, TERMINATE_SYS, form);
        optLog.setEntityId(entityId);
        optLog.setTaskName(task.getName());
        optLog.setTaskDefinitionKey(task.getTaskDefinitionKey());
        optLog.setTaskInstanceId(task.getId());
        optLog.setTaskStartTime(LocalDateTime.now());
        optLog.setTaskEndTime(LocalDateTime.now());
        optLog.setAction(ActionEnum.AUTOPASS.name());
        optLog.setComment("系统：用户未选择审批人，自动跳过空审批人");
        optLog.setTaskResult(STATE_DETAIL_AUTOPASS);

        return optLog;
    }


    public static FlowOperationLog deleteLog(String operatorId, String operatorName, WorkflowBase form, String entityId, String reason) {
        FlowOperationLog optLog = FlowOperationLog.create(operatorId, operatorName,
                form.getProcessInstanceId(), form.getProcessDefinitionId(), form.getProcessDefinitionKey(),
                form.getServiceName());
        optLog.setEntityId(entityId);
        optLog.setTaskName("用户删除");
        optLog.setTaskStartTime(LocalDateTime.now());
        optLog.setTaskEndTime(LocalDateTime.now());
        optLog.setAction(ActionEnum.DELETE.name());
        optLog.setComment(reason);
        optLog.setTaskResult(STATE_DETAIL_DELETE);
        return optLog;
    }



    /**
     * 抄送，操作人为系统
     */
    public static FlowOperationLog carbonCopyLog(String processInstanceId, String processDefinitionId, String processDefinitionKey,
                                                 @Nullable String taskId, String serviceName) {
        FlowOperationLog log = FlowOperationLog.create(OPERATOR_SYS, OPERATOR_SYS, processInstanceId, processDefinitionId, processDefinitionKey, serviceName);
        log. setTaskInstanceId(taskId);
        log.setAction(ActionEnum.COPY.name());
        return log;
    }


}
