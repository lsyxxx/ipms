package com.abt.wf.projection;

public interface SubcontractTestingSettlementDetailProjection {
    String getEntrustId();
    String getProjectName();
    String getEntrustCompany();
    String getCheckModuleId();
    String getCheckModuleName();
    String getUnit();

    /**
     * 已结算数量
     */
    Long getSettledCount();

    /**
     * 未结算
     */
    Long getUnsettledCount();

    /**
     * 申请总数
     */
    Long getNum();
}
