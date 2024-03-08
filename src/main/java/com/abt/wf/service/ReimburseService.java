package com.abt.wf.service;

import com.abt.sys.model.entity.FlowSetting;
import com.abt.wf.entity.Reimburse;
import com.abt.wf.model.ReimburseForm;
import com.abt.wf.model.ReimburseForm;

import java.util.List;
import java.util.Optional;

/**
 * 报销业务
 */
public interface ReimburseService {

    Reimburse saveEntity(ReimburseForm applyForm);

    Reimburse saveEntity(Reimburse entity);

    Optional<Reimburse> queryBy(String id);

    List<ReimburseForm> queryByStater(String starterId, int page, int size);

    List<FlowSetting> queryRbsTypes();

    ReimburseForm loadReimburse(String entityId);

    /**
     * 更新实体，流程完成
     */
    void finish(String entityId, int state, String deleteReason);

    void finish(String entityId);
}
