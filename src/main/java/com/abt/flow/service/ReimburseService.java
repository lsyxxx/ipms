package com.abt.flow.service;


import com.abt.flow.model.ProcessVo;
import com.abt.flow.model.ReimburseApplyForm;
import com.abt.sys.model.dto.UserView;

/**
 * 报销
 */
public interface ReimburseService {


    /**
     * 申请报销
     * @param user
     * @param applyForm 申请表单
     * @return
     */
    ProcessVo apply(UserView user, ReimburseApplyForm applyForm);

    /**
     * 启动部门审查
     * @return
     */
    ProcessVo departmentAudit(ProcessVo process, ReimburseApplyForm applyForm);

    /**
     * 技术负责人审批
     * @param process
     * @param applyForm
     * @return
     */
    ProcessVo techLeadAudit(ProcessVo process, ReimburseApplyForm applyForm);

    /**
     * 总经理审批
     * @param processVo
     * @param applyForm
     * @return
     */
    ProcessVo ceoAudit(ProcessVo processVo, ReimburseApplyForm applyForm);

    /**
     * 财务主管审批
     * @param processVo
     * @param applyForm
     * @return
     */
    ProcessVo accountancyAudit(ProcessVo processVo, ReimburseApplyForm applyForm);

}
