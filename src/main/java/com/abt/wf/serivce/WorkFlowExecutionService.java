package com.abt.wf.serivce;

import com.abt.wf.entity.Reimburse;
import com.abt.wf.model.ReimburseApplyForm;

/**
 * 流程执行相关
 */
public interface WorkFlowExecutionService {
    String previewFlow(ReimburseApplyForm form);

    Reimburse apply(ReimburseApplyForm form);

    void approve(ReimburseApplyForm form);
}
