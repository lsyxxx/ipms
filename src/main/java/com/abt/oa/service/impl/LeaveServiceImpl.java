package com.abt.oa.service.impl;

import com.abt.oa.entity.FrmLeaveReq;
import com.abt.oa.reposity.FrmLeaveReqRepository;
import com.abt.oa.service.LeaveService;
import com.abt.sys.util.WithQueryUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 *
 */
@Service
@Slf4j
public class LeaveServiceImpl implements LeaveService {
    private final FrmLeaveReqRepository frmLeaveReqRepository;


    public LeaveServiceImpl(FrmLeaveReqRepository frmLeaveReqRepository) {
        this.frmLeaveReqRepository = frmLeaveReqRepository;
    }

    @Override
    public List<FrmLeaveReq> findByUser(String userid, LocalDate startDate, LocalDate endDate) {
        return WithQueryUtil.build(frmLeaveReqRepository.findByApplyUserIDAndDateBetween(userid, startDate, endDate));
    }

    @Override
    public int countLeaveRecordByUser(String userid, LocalDate startDate, LocalDate endDate) {
        return frmLeaveReqRepository.countByApplyUserIDAndDateBetween(userid, startDate, endDate);
    }
}
