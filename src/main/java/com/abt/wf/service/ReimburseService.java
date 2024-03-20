package com.abt.wf.service;

import com.abt.wf.entity.Reimburse;
import com.abt.wf.model.ReimburseForm;
import com.abt.wf.model.ReimburseRequestForm;
import com.abt.wf.model.ValidationResult;

import java.util.List;


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

    List<ReimburseForm> findAllByCriteria(ReimburseRequestForm requestForm);

    List<ReimburseForm> findMyApplyByCriteria(ReimburseRequestForm requestForm);

    /**
     * 查询我的已办列表
     * criteria: 分页, 审批编号, 状态，流程创建时间，参与人id, 待办/已办
     * @param requestForm
     * @return
     */
    List<ReimburseForm> findMyDoneByCriteria(ReimburseRequestForm requestForm);

    /**
     * 查询我的待办列表
     * criteria: 分页, 审批编号, 状态，流程创建时间，参与人id, 待办/已办
     * @param requestForm
     * @return
     */
    List<ReimburseForm> findMyTodoByCriteria(ReimburseRequestForm requestForm);
}
