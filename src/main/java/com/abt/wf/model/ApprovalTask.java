package com.abt.wf.model;

import lombok.Data;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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

    public ApprovalTask addTask(TaskDTO dto) {
        this.taskList.add(dto);
        return this;
    }
}
