package com.abt.wfbak.model;

import lombok.Data;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 */
@Data
public class ApprovalTask {

    /**
     * 审批方式
     * 0: 依次审批
     */
    private int approvalType = 0;
    /**
     * 选择用户方式
     * 0: 用户自选
     * 1: 指定用户
     */
    private int selectUserType = 0;
    private String description;
    /**
     * 自定义的task(node)类型
     * apply: 申请节点
     * approval: 审批节点
     */
    private String taskType;
    private List<TaskDTO> taskList = new ArrayList<>();

    private String taskDefId;
    private String taskDefName;

    private String processInstanceId;
    private String processDefKey;
    private String processDefId;

    public static final String TASK_TYPE_APPLY = "apply";
    public static final String TASK_TYPE_APPROVAL = "approval";

    /**
     * 选择用户方式-用户自选
     */
    public static final int SELECT_USER_TYPE_MANUAL = 0;
    /**
     * 选择用户方式-指定用户
     */
    public static final int SELECT_USER_TYPE_SPECIFIC = 1;

    public ApprovalTask setProperties(Collection<CamundaProperty> collection) {
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

    public boolean isSpecific() {
        return SELECT_USER_TYPE_SPECIFIC == this.selectUserType;
    }

    public ApprovalTask addTask(TaskDTO dto) {
        this.taskList.add(dto);
        return this;
    }


    /**
     * 是否是申请节点
     */
    public boolean isApplyNode() {
        return TASK_TYPE_APPLY.equals(this.taskType);
    }

    /**
     * 是否审批节点
     */
    public boolean isApprovalNode() {
        return TASK_TYPE_APPROVAL.equals(this.taskType);
    }
}
