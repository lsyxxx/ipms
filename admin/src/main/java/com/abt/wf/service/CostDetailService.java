package com.abt.wf.service;

import com.abt.wf.entity.CostDetail;
import com.abt.wf.model.CostDetailRequestForm;

import java.util.List;

public interface CostDetailService {
    void save(List<CostDetail> list);

    void save(List<CostDetail> list, String refCode);

    List<CostDetail> findBy(CostDetailRequestForm requestForm);

    void check(List<CostDetail> list, double totalCost);

    void checkAndSave(List<CostDetail> list, double totalCost);
}
