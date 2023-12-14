package com.abt.wf.serivce;

import com.abt.wf.model.ReimburseApplyForm;
import org.camunda.bpm.engine.history.HistoricTaskInstance;

import java.util.List;

/**
 * 流程执行相关
 */
public interface WorkFlowExecutionService {
    List<HistoricTaskInstance> previewFlow(ReimburseApplyForm form);

    void apply(ReimburseApplyForm form);
}
