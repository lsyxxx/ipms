package com.abt.wf.repository;

import java.util.List;

public interface TripReimburseTaskRepository {

    /**
     * 已办主数据，不包含明细
     */
    List<TripReimburse> findDoneMainList(int page, int limit, String entityId, String state, String userid, String username,
                                         String startDate, String endDate);

    int countDoneList(int page, int limit, String entityId, String state, String userid,
                      String startDate, String endDate);

    /**
     * 待办主数据，不包含明细
     */
    List<TripReimburse> findTodoMainList(int page, int limit, String entityId, String state, String userid, String username,
                                         String startDate, String endDate);

    int countTodoList(int page, int limit, String entityId, String state, String userid, String startDate, String endDate);

}
