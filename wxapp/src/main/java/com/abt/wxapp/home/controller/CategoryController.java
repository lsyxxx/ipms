package com.abt.wxapp.home.controller;

import com.abt.wxapp.common.model.R;
import com.abt.wxapp.home.model.CategoryOptionVo;
import com.abt.wxapp.home.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 检测分类（小程序 tabs）
 */
@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/find-list")
    public R<List<CategoryOptionVo>> findList() {
        return R.success(categoryService.findList());
    }
}
