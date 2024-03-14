package com.abt.wfbak.service;

import com.abt.sys.model.entity.FlowSetting;
import com.abt.wfbak.entity.Reimburse;
import com.abt.wfbak.model.ReimburseForm;

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

    void finish(String entityId);
}
