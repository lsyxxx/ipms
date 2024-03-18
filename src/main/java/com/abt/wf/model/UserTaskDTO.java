package com.abt.wf.model;

import com.abt.wf.config.Constants;
import com.abt.wf.entity.FlowOperationLog;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty;

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
     * apply: 申请节点
     * approval: 审批节点
     */
    private String taskType;
    /**
     * 选择用户方式
     * 0: 用户自选
     * 1: 指定用户
     */
    private int selectUserType = Constants.SELECT_USER_TYPE_MANUAL;

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

}
