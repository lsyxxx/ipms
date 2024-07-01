package com.abt.wf.service;

import com.abt.common.model.Page;
import com.abt.wf.entity.TripReimburse;
import com.abt.wf.model.TripReimburseForm;
import com.abt.wf.model.TripRequestForm;

/**
 * 差旅报销
 */
public interface TripReimburseService extends WorkFlowService<TripReimburseForm>, BusinessService<TripRequestForm, TripReimburseForm>{
    void validateApplyForm(TripReimburseForm form);

    /**
     * 读取业务数据并组装好
     *
     */
    TripReimburseForm load(String rootId);

    TripReimburse loadCommonData(String rootId);
    void saveEntity(TripReimburse entity);

    int countAllByCriteria(TripRequestForm requestForm);

    Page<TripReimburseForm> findMyApplyByCriteriaPaged(TripRequestForm requestForm);

    Page<TripReimburseForm> findAllPaged(TripRequestForm requestForm);

    int countMyApplyByCriteria(TripRequestForm requestForm);

    Page<TripReimburseForm>  findMyDoneByCriteriaPaged(TripRequestForm requestForm);

    int countMyDoneByCriteria(TripRequestForm requestForm);

    Page<TripReimburseForm> findMyTodoByCriteriaPaged(TripRequestForm requestForm);
}

