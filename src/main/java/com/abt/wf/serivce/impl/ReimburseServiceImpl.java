package com.abt.wf.serivce.impl;

import com.abt.wf.entity.Reimburse;
import com.abt.wf.model.ReimburseApplyForm;
import com.abt.wf.repository.ReimburseRepository;
import com.abt.wf.serivce.ReimburseService;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class ReimburseServiceImpl implements ReimburseService {

    private final ReimburseRepository reimburseRepository;

    public ReimburseServiceImpl(ReimburseRepository reimburseRepository) {
        this.reimburseRepository = reimburseRepository;
    }

    @Override
    public Reimburse saveEntity(ReimburseApplyForm applyForm) {
        Reimburse entity = Reimburse.create(applyForm);
        final Reimburse saved = reimburseRepository.save(entity);
        return saved;
    }
}
