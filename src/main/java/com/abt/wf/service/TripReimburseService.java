package com.abt.wf.service;

import com.abt.wf.entity.TripReimburse;
import com.abt.wf.model.TripReimburseForm;
import com.abt.wf.model.TripRequestForm;

import java.util.List;

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
//    List<TripReimburse> findTripsMyTodoByCriteria(TripRequestForm form);
//    List<TripReimburse> findTripsMyApplyByCriteriaPageable(TripRequestForm form);
//    List<TripReimburse> findTripsMyDoneByCriteriaPageable(TripRequestForm form);
//    List<TripReimburse> findTripsAllByCriteriaPageable(TripRequestForm form);
}
