package com.abt.wf.service;

import com.abt.wf.entity.Reimburse;
import com.abt.wf.model.ReimburseForm;
import com.abt.wf.model.ValidationResult;


/**
 *
 */
public interface ReimburseService extends WorkFlowService<ReimburseForm>{
//    List<ReimburseForm> queryMyRbs(String starter, LocalDate processStartDay, LocalDate processEndDay, int page, int size);

    Reimburse findById(String entityId);

    void saveEntity(Reimburse rbs);

    /**
     * 读取业务流程
     *
     * @param entityId 业务实体id
     */
    Reimburse load(String entityId);

}
