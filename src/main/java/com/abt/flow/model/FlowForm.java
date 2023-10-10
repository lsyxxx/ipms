package com.abt.flow.model;

import com.abt.common.model.AutoForm;
import com.abt.common.model.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 流程业务表单
 */

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Accessors(chain = true)
public class FlowForm extends AutoForm {

    /**
     * 流程业务
     */
    private FlowType flowType;

    /**
     * 流程实例id
     */
    private String processInstanceId;

    /**
     * 下一个审批人
     */
    private User nextAssignee;

    /**
     * 部门审批人
     */
    private User deptManager;

    /**
     * 技术负责人
     */
    private User techManager;

    private User ceo;
    /**
     * 财务主管
     */
    private User fiManager;

    /**
     * 税务审批人
     */
    private User taxOfficer;

    /**
     * 会计审批人
     */
    private User accountancy;

    /**
     * 流程申请人/创建人
     */
    private User applicant;

    /**
     * 流程状态
     */
    private ProcessState state;

    /**
     * 当前节点
     */
    private String activityName;
    private String taskId;

    /**
     * 自定义名称
     */
    private String customName;
    private String processDefId;


}
