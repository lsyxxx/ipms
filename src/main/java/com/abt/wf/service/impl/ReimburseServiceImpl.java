package com.abt.wf.service.impl;

import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.entity.FlowSetting;
import com.abt.sys.service.UserService;
import com.abt.wf.entity.Reimburse;
import com.abt.wf.model.ReimburseForm;
import com.abt.wf.repository.ReimburseRepository;
import com.abt.wf.service.ReimburseService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 */
@Service
public class ReimburseServiceImpl implements ReimburseService {

    private final ReimburseRepository reimburseRepository;

    private final List<FlowSetting> queryReimburseType;

    private final UserService userService;

    public ReimburseServiceImpl(ReimburseRepository reimburseRepository, List<FlowSetting> queryReimburseType, @Qualifier("sqlServerUserService") UserService userService) {
        this.reimburseRepository = reimburseRepository;
        this.queryReimburseType = queryReimburseType;
        this.userService = userService;
    }

    @Override
    public Reimburse saveEntity(ReimburseForm applyForm) {
        return reimburseRepository.save(applyForm);
    }

    @Override
    public Reimburse saveEntity(Reimburse entity) {
        return reimburseRepository.save(entity);
    }

    @Override
    public Optional<Reimburse> queryBy(String id) {
        return reimburseRepository.findById(id);
    }


    @Override
    public List<ReimburseForm> queryByStater(String starterId, int page, int size) {
        Pageable pageRequest = PageRequest.of(page, size);
        Page<Reimburse> pageData = reimburseRepository.findByStarterIdOrderByCreateDateDesc(starterId, pageRequest);
        List<ReimburseForm> vos = new ArrayList<>();
        for (Reimburse reimburse : pageData.getContent()) {
            ReimburseForm vo = ReimburseForm.from(reimburse);
            vos.add(vo);
        }
        return vos;
    }

    @Override
    public List<FlowSetting> queryRbsTypes() {
        return this.queryReimburseType;
    }





    @Override
    public ReimburseForm loadReimburse(String entityId) {
        final Optional<Reimburse> optionalReimburse = this.queryBy(entityId);
        if (optionalReimburse.isEmpty()) {
            throw new BusinessException("未查询到报销流程 - 审批编号: " + entityId);
        }
        Reimburse rbs = optionalReimburse.get();

        return ReimburseForm.from(rbs);
    }

}
