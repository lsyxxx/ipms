package com.abt.wf.service;

import com.abt.wfbak.model.ReimburseForm;

import java.time.LocalDate;
import java.util.List;

/**
 *
 */
public interface ReimburseService extends WorkFlowService<ReimburseForm>{
    List<ReimburseForm> queryMyRbs(String starter, LocalDate processStartDay, LocalDate processEndDay, int page, int size);
}
