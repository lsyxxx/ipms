package com.abt.wf.serivce;

import com.abt.wf.entity.Reimburse;
import com.abt.wf.model.ReimburseApplyForm;
import com.abt.wf.model.TaskDTO;

import java.util.List;

/**
 * 流程执行相关
 */
public interface WorkFlowExecutionService {
    List<TaskDTO> previewFlow(ReimburseApplyForm form);

    Reimburse apply(ReimburseApplyForm form);

    void approve(ReimburseApplyForm form);
}
