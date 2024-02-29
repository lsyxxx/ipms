package com.abt.wf.serivce.impl;

import com.abt.sys.model.dto.UserView;
import com.abt.sys.model.entity.FlowSetting;
import com.abt.wf.entity.Reimburse;
import com.abt.wf.model.ReimburseApplyForm;
import com.abt.wf.model.ReimburseDTO;
import com.abt.wf.repository.ReimburseRepository;
import com.abt.wf.serivce.ReimburseService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    public ReimburseServiceImpl(ReimburseRepository reimburseRepository, List<FlowSetting> queryReimburseType) {
        this.reimburseRepository = reimburseRepository;
        this.queryReimburseType = queryReimburseType;
    }

    @Override
    public Reimburse saveEntity(ReimburseApplyForm applyForm) {
        Reimburse entity = Reimburse.create(applyForm);
        return reimburseRepository.save(entity);
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
    public List<ReimburseDTO> queryByStater(String starterId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Reimburse> pageData = reimburseRepository.findByStarterIdOrderByCreateDateDesc(starterId, pageRequest);
        List<ReimburseDTO> vos = new ArrayList<>();
        for (Reimburse reimburse : pageData.getContent()) {
            ReimburseDTO vo = ReimburseDTO.from(reimburse);
            vos.add(vo);
        }
        return vos;
    }

    @Override
    public List<ReimburseDTO> queryByStater(String starterId) {
        List<Reimburse> list = reimburseRepository.findAllByStarterIdOrderByCreateDateDesc(starterId);
        List<ReimburseDTO> vos = new ArrayList<>();
        list.forEach(ReimburseDTO::from);
        return vos;
    }


    @Override
    public List<FlowSetting> queryRbsTypes() {
        return this.queryReimburseType;
    }




    @Override
    public String tempSave(ReimburseApplyForm form) {
        Reimburse rbs = Reimburse.createTemp(form);
        return rbs.getId();
    }


    public void load(String rbsId, UserView userView) {
        final Optional<Reimburse> entity = reimburseRepository.findById(rbsId);
    }

}
