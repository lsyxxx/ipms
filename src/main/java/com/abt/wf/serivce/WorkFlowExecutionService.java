package com.abt.wf.serivce;

import com.abt.wf.entity.Reimburse;
import com.abt.wf.model.ApprovalTask;
import com.abt.wf.model.ReimburseApplyForm;

import java.util.List;

/**
 * 流程执行相关
 */
public interface WorkFlowExecutionService {
    List<ApprovalTask> previewFlow(ReimburseApplyForm form);

    /**
     * 申请
     * @param form 申请表单
     * @return 业务实体
     */
    Reimburse apply(ReimburseApplyForm form);

    void approve(ReimburseApplyForm form);
}
