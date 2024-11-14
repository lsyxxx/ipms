package com.abt.material.service;

import com.abt.material.entity.MaterialDetail;

import java.util.List;

public interface MaterialService {


    /**
     * 查询所有物品
     */
    List<MaterialDetail> findAll();
}
