package com.abt.wxapp.home.service;

import com.abt.wxapp.home.model.CategoryOptionVo;

import java.util.List;

/**
 * 检测分类（小程序）
 */
public interface CategoryService {

    /**
     * 分类下拉：首项为「全部」，其余为微信渠道启用的检测分类
     */
    List<CategoryOptionVo> findList();
}
