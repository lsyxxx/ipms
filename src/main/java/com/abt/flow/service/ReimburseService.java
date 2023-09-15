package com.abt.flow.service;


import com.abt.flow.model.ReimburseApplyForm;
import com.abt.flow.model.entity.Reimburse;
import com.abt.sys.model.dto.UserView;

/**
 * 报销
 */
public interface ReimburseService{


    /**
     * 申请报销
     * @param user 申请用户
     * @param applyForm 申请表单
     */
    void apply(UserView user, ReimburseApplyForm applyForm);

    /**
     * 启动部门审查
     */
    void departmentAudit(UserView user, ReimburseApplyForm applyForm);

    /**
     * 技术负责人审批
     */
    void techLeadAudit(UserView user, ReimburseApplyForm applyForm);

    /**
     * 总经理审批
     */
    Reimburse ceoAudit(UserView user, ReimburseApplyForm applyForm);

    /**
     * 会计审批
     */
    Reimburse accountantAudit(UserView user, ReimburseApplyForm applyForm);

    /**
     * 财务主管审批
     *
     * @param user      审批用户（财务主管）
     * @param applyForm 提交表单
     */
    Reimburse financeManagerAudit(UserView user, ReimburseApplyForm applyForm);

    /**
     * 税务会计审批
     *
     * @param user      审批用户（税务会计）
     * @param applyForm 提交表单
     */
    Reimburse taxOfficerAudit(UserView user, ReimburseApplyForm applyForm);


}
