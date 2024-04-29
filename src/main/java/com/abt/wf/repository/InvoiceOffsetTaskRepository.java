package com.abt.wf.repository;

import com.abt.wf.entity.InvoiceOffset;

import java.util.List;

public interface InvoiceOffsetTaskRepository {
    List<InvoiceOffset> findDoneList(int page, int limit, String userid, String username, String startDate, String endDate, String state, String idLike,  String contractName);

    int countDoneList(String userid, String username, String startDate, String endDate, String state, String idLike,  String contractName);

    List<InvoiceOffset> findTodoList(int page, int limit, String userid, String username, String startDate, String endDate, String state, String idLike,  String contractName);

    int countTodoList(String assigneeId, String assigneeName, String startDate, String endDate, String state,
                      String idLike,  String contractName);

    List<InvoiceOffset> findUserApplyList(int page, int limit, String userid, String username, String startDate, String endDate, String state, String idLike,  String contractName);

}
