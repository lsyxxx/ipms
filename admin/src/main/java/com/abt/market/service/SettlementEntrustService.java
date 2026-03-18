package com.abt.market.service;

import com.abt.market.entity.SettlementSummary;

import java.util.List;

public interface SettlementEntrustService {
    /**
     * 一个委托项目的检测项目汇总
     * 检测项目	样品数量	已结算样品数量	结算金额
     * @param entrustId 委托编号
     */
    List<SettlementSummary> checkModuleSettlement(String entrustId);
}
