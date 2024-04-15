package com.abt.wf.repository;

import com.abt.wf.entity.InvoiceApply;

import java.util.List;

/**
 * 开票申请task查询
 * criteria 申请人 申请日期（起止日期） 流程状态 审批编号 合同名称，合同编号, 客户id， 客户name, 项目名称，申请部门id, 申请部门name
 */
public interface InvoiceApplyTaskRepository {

    List<InvoiceApply> findDoneListPageable(int page, int limit, String userid, String username, String state, String startDate, String endDate,
                                    String idLike, String clientId, String clientName, String contractNo, String contractName,
                                            String project, String deptId, String deptName);

    int countDoneList(String userid, String username, String state, String startDate, String endDate,
                      String idLike, String clientId, String clientName, String contractNo, String contractName,
                      String project, String deptId, String deptName);

    List<InvoiceApply> findTodoListPageable(int page, int limit, String userid, String username, String state, String startDate, String endDate,
                                            String idLike, String clientId, String clientName, String contractNo, String contractName,
                                            String project, String deptId, String deptName);

    int countTodoList(String userid, String username, String state, String startDate, String endDate,
                      String idLike, String clientId, String clientName, String contractNo, String contractName,
                      String project, String deptId, String deptName);

    List<InvoiceApply> findApplyListPageable(int page, int limit, String userid, String username, String state, String startDate, String endDate,
                                             String idLike, String clientId, String clientName, String contractNo, String contractName,
                                             String project, String deptId, String deptName);

    int countApplyList(String userid, String username, String state, String startDate, String endDate,
                       String idLike, String clientId, String clientName, String contractNo, String contractName,
                       String project, String deptId, String deptName);
}
