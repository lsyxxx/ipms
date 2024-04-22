package com.abt.wf.model;

import com.abt.wf.config.Constants;
import com.abt.wf.entity.FlowOperationLog;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import com.abt.common.model.User;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty;

import static com.abt.wf.config.Constants.SELECT_USER_TYPE_MIX;

/**
 * 描述流程节点
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserTaskDTO extends FlowOperationLog {

    /**
     * 审批方式
     * 0: 依次审批
     */
    private int approvalType = Constants.APPROVAL_TYPE_SEQ;

    /**
     * 自定义的task(node)类型
     * apply: 申请节点,Contants.TASK_TYPE_APPLY
     * approval: 审批节点,Contants.TASK_TYPE_APPROVAL
     *
     */
    private String taskType;
    /**
     * 选择用户方式
     * 0: 用户自选
     * 1: 指定用户
     */
    private int selectUserType = Constants.SELECT_USER_TYPE_MANUAL;
    /**
     * 当前task是否正在运行
     * 是否存在taskResult
     */
    private boolean isActive = false;
    /**
     * 是否是预览节点
     */
    private boolean isPreview = true;

    /**
     * 如果是多实例节点，那么有子节点
     */
    private List<UserTaskDTO> taskList = new ArrayList<>();

    public UserTaskDTO setProperties(Collection<CamundaProperty> collection) {
        collection.forEach(i -> {
            final String name = i.getCamundaName();
            if ("approvalType".equals(name)) {
                this.approvalType = Integer.parseInt(i.getCamundaValue());
            } else if ("selectUserType".equals(name)) {
                this.selectUserType = Integer.parseInt(i.getCamundaValue());
            } else if ("taskType".equals(name)) {
                this.taskType = i.getCamundaValue();
            }
        });
        return this;
    }

    /**
     * 添加子task
     * @param dto 子task
     */
    public void addUserTaskDTO(UserTaskDTO dto) {
        taskList.add(dto);
    }

    public boolean isApplyNode() {
        return Constants.TASK_TYPE_APPLY.equals(this.taskType);
    }

    public boolean isSpecific() {
        return Constants.SELECT_USER_TYPE_SPECIFIC == this.selectUserType;
    }

    public boolean isActive() {
        this.isActive = !StringUtils.isBlank(this.getTaskResult());
        return isActive;
    }

    public static UserTaskDTO createCopyTask() {
        UserTaskDTO dto = new UserTaskDTO();
        //properties
        dto.setTaskType(Constants.TASK_TYPE_COPY);
        dto.setTaskName(Constants.TASK_NAME_COPY);
        dto.setApprovalType(Constants.APPROVAL_TYPE_SEQ);
        dto.setSelectUserType(SELECT_USER_TYPE_MIX);


        return dto;
    }

    /**
     * 创建一个抄送节点的子节点
     */
    public static UserTaskDTO createCopyUserTask(User user) {
        UserTaskDTO dto = new UserTaskDTO();
        dto.setOperatorId(user.getId());
        dto.setOperatorName(user.getUsername());

        return dto;
    }

    public static UserTaskDTO of(FlowOperationLog log) {
        UserTaskDTO dto = new UserTaskDTO();
        if (log == null) {
            return dto;
        }
        dto.setId(log.getId());
        dto.setAction(log.getAction());
        dto.setComment(log.getComment());
        dto.setEntityId(log.getEntityId());
        dto.setOperatorId(log.getOperatorId());
        dto.setOperatorName(log.getOperatorName());
        dto.setProcessDefinitionId(log.getProcessDefinitionId());
        dto.setProcessDefinitionKey(log.getProcessDefinitionKey());
        dto.setProcessInstanceId(log.getProcessInstanceId());
        dto.setServiceName(log.getServiceName());
        dto.setSort(log.getSort());
        dto.setTaskDefinitionKey(log.getTaskDefinitionKey());
        dto.setTaskEndTime(log.getTaskEndTime());
        dto.setTaskInstanceId(log.getTaskInstanceId());
        dto.setTaskName(log.getTaskName());
        dto.setTaskResult(log.getTaskResult());
        dto.setTaskStartTime(log.getTaskStartTime());
        return dto;
    }

}
