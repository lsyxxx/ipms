package com.abt.wf.service;

import com.abt.wf.entity.SubcontractTestingSettlementDetail;
import com.abt.wf.entity.SubcontractTestingSettlementMain;
import com.abt.wf.model.SbctSummaryData;
import com.abt.wf.model.SubcontractTestingSettlementRequestForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

/**
 * 外送结算流程服务
 */
public interface SubcontractTestingSettlementService extends WorkFlowService<SubcontractTestingSettlementMain>, BusinessService<SubcontractTestingSettlementRequestForm, SubcontractTestingSettlementMain> {
    /**
     * 仅读取主体main
     * @param id 审批编号
     */
    SubcontractTestingSettlementMain loadEntityOnly(String id);

    /**
     * 读取业务，包含详情及当前任务
     * @param id 业务id
     */
    SubcontractTestingSettlementMain loadEntireEntity(String id);

    /**
     * 获取汇总数据
     */
    List<SbctSummaryData> getSummaryData(String mid);

    Page<SubcontractTestingSettlementDetail> getSamplesPage(String id, PageRequest pageRequest);

    void findDuplicatedSamplesAndMarked(SubcontractTestingSettlementMain main);

    boolean duplicatedSamplesExists(String id);
}
