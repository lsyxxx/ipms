package com.abt.oa.service;

import com.abt.oa.entity.FrmLeaveReq;

import java.time.LocalDate;
import java.util.List;

/**
 * 请假
 */
public interface LeaveService {
    /**
     * 查询用户在指定范围内的请假
     * @param userid 查询用户id
     * @param isFinishIn 状态，可以多个用逗号分隔(sql in语句)
     * @param startDate 开始时间
     * @param endDate 结束时间
     */
    List<FrmLeaveReq> findByUser(String userid, String isFinishIn, LocalDate startDate, LocalDate endDate);


    int countLeaveRecordByUser(String userid, String isFinish, LocalDate startDate, LocalDate endDate);
}
