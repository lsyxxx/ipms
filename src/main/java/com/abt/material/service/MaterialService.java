package com.abt.material.service;

import com.abt.material.entity.MaterialDetail;
import com.abt.material.model.MaterialRequestForm;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MaterialService {


    /**
     * 查询所有物品
     */
    List<MaterialDetail> findAll();

    Page<MaterialDetail> findByDeptPageable(MaterialRequestForm requestForm);

    /**
     * 物资模糊查询
     * query 关联
     */
    Page<MaterialDetail> findByQueryPageable(MaterialRequestForm requestForm);
}
