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
     */
    List<FrmLeaveReq> findByUser(String userid, LocalDate startDate, LocalDate endDate);

    int countLeaveRecordByUser(String userid, LocalDate startDate, LocalDate endDate);
}
