package com.abt.wf.serivce.impl;

import com.abt.wf.entity.Reimburse;
import com.abt.wf.model.ReimburseApplyForm;
import com.abt.wf.model.ReimburseDTO;
import com.abt.wf.repository.ReimburseRepository;
import com.abt.wf.serivce.ReimburseService;
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

    public ReimburseServiceImpl(ReimburseRepository reimburseRepository) {
        this.reimburseRepository = reimburseRepository;
    }

    @Override
    public Reimburse saveEntity(ReimburseApplyForm applyForm) {
        Reimburse entity = Reimburse.create(applyForm);
        final Reimburse saved = reimburseRepository.save(entity);
        return saved;
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

}