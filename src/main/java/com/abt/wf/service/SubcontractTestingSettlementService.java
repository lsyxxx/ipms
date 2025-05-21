package com.abt.wf.service;

import com.abt.wf.entity.SubcontractTestingSettlementMain;
import com.abt.wf.model.SubcontractTestingSettlementRequestForm;

import java.util.List;

/**
 * 外送结算流程服务
 */
public interface SubcontractTestingSettlementService extends WorkFlowService<SubcontractTestingSettlementMain>, BusinessService<SubcontractTestingSettlementRequestForm, SubcontractTestingSettlementMain> {
    /**
     * 读取业务，包含详情及当前任务
     * @param id 业务id
     */
    SubcontractTestingSettlementMain loadEntireEntity(String id);
}
