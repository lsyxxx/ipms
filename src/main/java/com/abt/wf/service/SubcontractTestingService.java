package com.abt.wf.service;

import com.abt.wf.entity.SubcontractTesting;
import com.abt.wf.entity.SubcontractTestingSample;
import com.abt.wf.model.SbctSummaryData;
import com.abt.wf.model.SubcontractTestingRequestForm;
import com.abt.wf.projection.SubcontractTestingSettlementDetailProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SubcontractTestingService extends WorkFlowService<SubcontractTesting> ,BusinessService<SubcontractTestingRequestForm, SubcontractTesting> {
    /**
     * 校验实体是否正常
     * @param id 实体类id
     */
    void validateEntity(String id);

    SubcontractTesting loadEntityOnly(String entityId);

    SubcontractTesting loadWithSampleList(String entityId);

    /**
     * 读取申请单的样品列表
     * @param entityId 申请单Id
     */
    List<SubcontractTestingSample> getSamples(String entityId);

    String exportSampleList(String id);

    List<String> findAllApplySubcontractCompany();

    /**
     * 查询外送申请的明细，仅查看申请通过的
     */
    List<SubcontractTestingSettlementDetailProjection> findDetails(SubcontractTestingRequestForm requestForm);

    /**
     * 验证是否有重复外送的样品
     * 1. 提交的表单中是否存在重复
     * 2. 提交的表单中和数据库中是否存在重复
     * @param entity 申请单实体类
     * @return 返回重复的样品列表。若无重复，则返回空列表
     */
    List<SubcontractTestingSample> validateDuplicatedSample(SubcontractTesting entity);

    /**
     * 查询申请单样品，并标记重复的
     *
     * @param ids 申请单id列表
     * @return  样品列表
     */
    Page<SubcontractTestingSample> findSamplesAndMarkDuplicated(List<String> ids, Pageable pageable);

    /**
     * 仅查询重复外送的样品列表
     */
    List<SubcontractTestingSample> findDuplicatedSamples(List<String> ids);

    List<SbctSummaryData> getSummaryData(List<String> ids);
}
