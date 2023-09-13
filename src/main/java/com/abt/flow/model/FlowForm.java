package com.abt.flow.model;

import com.abt.common.model.AutoForm;
import com.abt.common.model.User;
import com.abt.flow.model.entity.BizFlowRelation;
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

    /**
     * 流程-业务关系
     * T_biz_flow_rel
     */
    private BizFlowRelation relation;

    /**
     * 流程申请人
     */
    private User<String> applicant;

}
