package com.abt.testing.service;

import com.abt.market.entity.SettlementItem;
import com.abt.testing.entity.Entrust;
import com.abt.testing.model.EntrustRequestForm;

import java.util.List;

public interface EntrustService {
    List<Entrust> findEntrustListByQuery(EntrustRequestForm requestForm);

    /**
     * 根据委托单号（检测编号）查询相关检测项目及样品数量
     *
     * @param entrustNo 委托单号（检测编号）
     * @return SettlementItem列表
     */
    List<SettlementItem> findSampleCheckModules(String entrustNo);
}
