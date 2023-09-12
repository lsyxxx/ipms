package com.abt.flow.service;


import com.abt.common.validator.IValidator;
import com.abt.common.validator.ValidatorChain;
import com.abt.flow.model.ProcessVo;
import com.abt.flow.model.ReimburseApplyForm;
import com.abt.sys.model.dto.UserView;

import java.util.List;
import java.util.Map;

/**
 * 报销
 */
public interface ReimburseService{


    /**
     * 申请报销
     * @param user 申请用户
     * @param applyForm 申请表单
     * @return ProcessVo
     */
    ProcessVo<ReimburseApplyForm> apply(UserView user, ReimburseApplyForm applyForm);

    /**
     * 启动部门审查
     */
    ProcessVo<ReimburseApplyForm> departmentAudit(UserView user, ReimburseApplyForm applyForm);

    /**
     * 技术负责人审批
     */
    ProcessVo<ReimburseApplyForm> techLeadAudit(UserView user, ReimburseApplyForm applyForm);

    /**
     * 总经理审批
     */
    ProcessVo<ReimburseApplyForm> ceoAudit(UserView user, ReimburseApplyForm applyForm);

    /**
     * 财务主管审批
     */
    ProcessVo<ReimburseApplyForm> accountancyAudit(UserView user, ReimburseApplyForm applyForm);



}
