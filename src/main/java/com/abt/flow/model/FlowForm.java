package com.abt.flow.model;

import com.abt.common.model.AutoForm;
import com.abt.common.model.User;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 流程业务表单
 */
@Data
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
    private String nextAssignee;

    /**
     * 部门审批人
     */
    private String deptManager;

    /**
     * 技术负责人
     */
    private String techManager;

    private String ceo;
    /**
     * 财务主管
     */
    private String fiManager;

    /**
     * 税务审批人
     */
    private String taxOfficer;

    /**
     * 会计审批人
     */
    private String accountancy;

    /**
     * 流程申请人
     */
    private User<String> applicant;

    /**
     * 流程状态
     */
    private ProcessState state;

}
