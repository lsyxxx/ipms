package com.abt.common.service.impl;

import com.abt.common.entity.Category;
import com.abt.common.repository.CategoryRepository;
import com.abt.common.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 数据字典
 */
@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }


    @Override
    public List<Category> findByTypeId(String typeId) {
        return categoryRepository.findByTypeId(typeId);
    }

}
