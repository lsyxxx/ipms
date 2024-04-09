package com.abt.wf.repository;

import com.abt.wf.entity.Loan;
import java.util.List;

/**
 * 借款申请task查询
 * criteria 申请人 申请日期（起止日期） 流程状态 审批编号 支付方式,项目，借款部门
 */
public interface LoanTaskRepository {
    List<Loan> findDoneList(int page, int limit, String userid, String username, String startDate, String endDate, String state, String idLike, String payType, String deptId, String project);
    List<Loan> findTodoList(int page, int limit, String userid, String username, String startDate, String endDate, String state, String idLike, String payType, String deptId, String project);
    List<Loan> findUserApplyList(int page, int limit, String userid, String username, String startDate, String endDate, String state, String idLike, String payType, String deptId, String project);

}
