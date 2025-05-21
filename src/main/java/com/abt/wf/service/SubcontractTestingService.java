package com.abt.wf.service;

import com.abt.wf.entity.SubcontractTesting;
import com.abt.wf.model.SubcontractTestingRequestForm;
import com.abt.wf.projection.SubcontractTestingSettlementDetailProjection;

import java.util.List;

public interface SubcontractTestingService extends WorkFlowService<SubcontractTesting> ,BusinessService<SubcontractTestingRequestForm, SubcontractTesting> {
    SubcontractTesting loadWithSampleList(String entityId);

    String exportSampleList(String id);

    List<String> findAllApplySubcontractCompany();

    /**
     * 查询外送申请的明细，仅查看申请通过的
     */
    List<SubcontractTestingSettlementDetailProjection> findDetails(SubcontractTestingRequestForm requestForm);

}
