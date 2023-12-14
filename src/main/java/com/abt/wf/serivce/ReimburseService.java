package com.abt.wf.serivce;

import com.abt.wf.entity.Reimburse;
import com.abt.wf.model.ReimburseApplyForm;

/**
 * 报销业务
 */
public interface ReimburseService {
    Reimburse saveEntity(ReimburseApplyForm applyForm);
}
