package com.abt.oa.service.impl;

import com.abt.oa.entity.FrmLeaveReq;
import com.abt.oa.reposity.FrmLeaveReqRepository;
import com.abt.oa.service.LeaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
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

    public List<FrmLeaveReq> findByUser(String userid, LocalDate startDate, LocalDate endDate) {


    }
}
