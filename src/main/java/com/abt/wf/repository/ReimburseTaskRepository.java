package com.abt.wf.repository;


import com.abt.wf.model.ReimburseForm;

import java.util.List;

public interface ReimburseTaskRepository {

    /**
     * 查询指定用户的报销申请以及流程当前节点
     * criteria: 分页, 审批编号, 状态，创建人，创建时间
     */
    List<ReimburseForm> findReimburseWithCurrenTaskPageable(int page, int size, String entityId, String state, String createUserid, String startDate, String endDate);

    int countReimburseWithCurrenTaskPageable(String entityId, String state, String createUserid, String startDate, String endDate);

    /**
     * 查询指定用户作为参与人的流程task
     * criteria: 分页, 审批编号, 状态，流程创建时间，参与人id, 待办/已办
     * @param todo 待办: 0, 已办:1
     */
    List<ReimburseForm> findTaskPageable(int page, int size, String entityId, String state, String invokedUserid,
                                         String startDate, String endDate, int todo);

    int TODO = 0;
    int DONE = 1;

    int countTask(String entityId, String state, String invokedUserid,
                  String startDate, String endDate, int todo);
}
