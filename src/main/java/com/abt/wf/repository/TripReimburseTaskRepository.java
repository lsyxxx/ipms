package com.abt.wf.repository;

import com.abt.wf.entity.TripReimburse;

import java.util.List;

public interface TripReimburseTaskRepository {

    /**
     * 查询指定用户的报销申请以及流程当前节点
     * 申请人(user),申请日期(startDate-endDate),出差人包含(staff), 审批编号(id), 状态(state), 分页
     */
    List<TripReimburse> findTaskWithCurrenTaskPageable(int page, int size, String entityId, String state,
                                                       String createUserid, String startDate, String endDate, String staff);

    /**
     * 查询指定用户作为参与人的流程task
     * 申请人(user),申请日期(startDate-endDate),出差人包含(staff), 审批编号(id), 状态(state), 分页，待办/已办
     * @param todo 待办: 0, 已办:1
     */
    List<TripReimburse> findTaskPageable(int page, int size, String entityId, String state, String invokedUserid,
                                         String startDate, String endDate, String staff, int todo);
}
