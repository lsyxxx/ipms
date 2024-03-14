package com.abt.wfbak.service;

import com.abt.wfbak.entity.Reimburse;
import com.abt.wfbak.model.ApprovalTask;
import com.abt.wfbak.model.ReimburseForm;

import java.util.List;

/**
 * 流程执行相关
 */
public interface WorkFlowExecutionService {
    List<ApprovalTask> previewFlow(ReimburseForm form);

    /**
     * 申请
     * @param form 申请表单
     * @return 业务实体
     */
    Reimburse apply(ReimburseForm form);

    void approve(ReimburseForm form);

}
