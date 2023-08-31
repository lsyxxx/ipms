package com.abt.flow.service.impl;

import com.abt.flow.model.ProcessVo;
import com.abt.flow.model.ReimburseApplyForm;
import com.abt.flow.service.ReimburseService;
import com.abt.sys.model.dto.UserView;
import org.flowable.engine.runtime.ProcessInstance;

import java.io.InputStream;
import java.util.List;

/**
 *
 */
public class AbstractReimburseServiceImpl implements ReimburseService {

    @Override
    public ProcessVo apply(UserView user, ReimburseApplyForm applyForm) {
        return null;
    }

    @Override
    public ProcessVo departmentAudit(ProcessVo process, ReimburseApplyForm applyForm) {
        return null;
    }

    @Override
    public ProcessVo techLeadAudit(ProcessVo process, ReimburseApplyForm applyForm) {
        return null;
    }

    @Override
    public ProcessVo ceoAudit(ProcessVo processVo, ReimburseApplyForm applyForm) {
        return null;
    }

    @Override
    public ProcessVo accountancyAudit(ProcessVo processVo, ReimburseApplyForm applyForm) {
        return null;
    }
}
