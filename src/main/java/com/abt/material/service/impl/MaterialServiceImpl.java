package com.abt.material.service.impl;

import com.abt.material.entity.MaterialDetail;
import com.abt.material.repository.MaterialDetailRepository;
import com.abt.material.service.MaterialService;
import lombok.extern.slf4j.Slf4j;
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
}
