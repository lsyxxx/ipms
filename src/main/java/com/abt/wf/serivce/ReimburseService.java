package com.abt.wf.serivce;

import com.abt.wf.entity.Reimburse;
import com.abt.wf.model.ReimburseApplyForm;
import com.abt.wf.model.ReimburseDTO;

import java.util.List;
import java.util.Optional;

/**
 * 报销业务
 */
public interface ReimburseService {

    Reimburse saveEntity(ReimburseApplyForm applyForm);

    Optional<Reimburse> queryBy(String id);

    List<ReimburseDTO> queryByStater(String starterId, int page, int size);
}