package com.abt.material.service.impl;

import com.abt.material.entity.MaterialDetail;
import com.abt.material.model.MaterialRequestForm;
import com.abt.material.repository.MaterialDetailRepository;
import com.abt.material.service.MaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 物品耗材
 */
@Service
@Slf4j
public class MaterialServiceImpl implements MaterialService {

    private final MaterialDetailRepository materialDetailRepository;

    public MaterialServiceImpl(MaterialDetailRepository materialDetailRepository) {
        this.materialDetailRepository = materialDetailRepository;
    }


    @Override
    public List<MaterialDetail> findAll() {
        return materialDetailRepository.findAll(
                Sort.by(
                        Sort.Order.asc("deptName"),
                        Sort.Order.asc("index"),
                        Sort.Order.asc("name"),
                        Sort.Order.asc("specification")
                ));
    }

    @Override
    public Page<MaterialDetail> findByDeptPageable(MaterialRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(),
                Sort.by(Sort.Order.asc("deptId"), Sort.Order.asc("name"), Sort.Order.asc("index")));
        return materialDetailRepository.findByQueryPageable(requestForm.getDeptId(), pageable);
    }
}
