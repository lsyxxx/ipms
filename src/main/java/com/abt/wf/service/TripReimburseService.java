package com.abt.wf.service;

import com.abt.wf.entity.TripReimburse;
import com.abt.wf.entity.WorkflowBase;
import com.abt.wf.model.TripReimburseForm;
import com.abt.wf.model.TripRequestForm;

/**
 * 差旅报销
 */
public interface TripReimburseService extends WorkFlowService<TripReimburseForm>, BusinessQueryService<TripRequestForm, TripReimburseForm> {
    void validateApplyForm(TripReimburseForm form);

    /**
     * 读取业务数据并组装好
     *
     * @param rootId
     * @return
     */
    TripReimburseForm load(String rootId);

    TripReimburse loadCommonData(String rootId);

    void saveEntity(TripReimburse entity);
}

