package com.abt.common.service;

import com.abt.common.entity.Category;

import java.util.List;

public interface CategoryService {


    /**
     * 根据typeId查询字典
     * @param typeId 分类Id
     * @return List<Category>
     */
    List<Category> findByTypeId(String typeId);
}
