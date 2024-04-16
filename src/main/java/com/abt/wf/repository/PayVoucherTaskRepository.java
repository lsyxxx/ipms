package com.abt.wf.repository;

import com.abt.wf.entity.PayVoucher;

import java.util.List;

public interface PayVoucherTaskRepository {
    List<PayVoucher> findPayVoucherDoneList(int page, int limit, String assigneeId, String assigneeName, String startDate, String endDate,
                                            String entityIdLike, String state, String project, String contactNo, String contactName);

    int countPayVoucherDoneList(String assigneeId, String assigneeName, String startDate, String endDate,
                                String entityIdLike, String state, String project, String contractNo, String contractName);

    List<PayVoucher> findPayVoucherTodoList(int page, int limit, String assigneeId, String assigneeName, String startDate, String endDate,
                                            String entityIdLike, String state, String project, String contractNo, String contractName);

    int countPayVoucherTodoList(String assigneeId, String assigneeName, String startDate, String endDate,
                                String entityIdLike, String state, String project, String contractNo, String contractName);

    List<PayVoucher> findPayVoucherUserApplyList(int page, int limit, String applyUserid, String applyUsername, String startDate, String endDate,
                                                 String entityIdLike, String state, String project, String contractNo, String contractName);

    int countPayVoucherUserApplyList(String applyUserid, String applyUsername, String startDate, String endDate,
                                     String entityIdLike, String state, String project, String contractNo, String contractName);
}
