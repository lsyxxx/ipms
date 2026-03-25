package com.abt.wxapp.checkmodule.controller;

import com.abt.common.config.ValidateGroup;
import com.abt.wxapp.checkmodule.entity.DynamicForm;
import com.abt.wxapp.checkmodule.service.DynamicFormService;
import com.abt.wxapp.common.model.PageRequestForm;
import com.abt.wxapp.common.model.R;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.jpa.repository.Query;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 检测项目动态表单
 */
@RestController
@Slf4j
@RequestMapping("/chk/dyform")
public class DynamicFormController {

    private final DynamicFormService dynamicFormService;

    public DynamicFormController(DynamicFormService dynamicFormService) {
        this.dynamicFormService = dynamicFormService;
    }


    /**
     * 新增表单，只能新增，不能修改（修改即再插入一条新记录）
     * @param form 表单内容
     */
    @PostMapping("/save")
    public R<Object> save(@Validated(ValidateGroup.Save.class) @RequestBody DynamicForm form) {
        dynamicFormService.save(form);
        return R.success("保存成功");
    }

    /**
     * 查询该检测项目下 id 最大的表单（最新插入的一版）
     * @param checkModuleId 检测项目id
     * @param checkModuleName 检测项目名称
     * @return 表单内容
     */
    @GetMapping("/find/cm/newest")
    public R<DynamicForm> findNewestByCheckModule(String checkModuleId, String checkModuleName) {
        DynamicForm form = dynamicFormService.findNewestByCheckModuleId(checkModuleId, checkModuleName);
        return R.success(form);
    }

    /**
     * 查询指定检测项目的历史版本记录
     * @param checkModuleId 检测项目Id
     * @param checkModuleName 检测项目名称，用于报错
     */
    @GetMapping("/find/cm/history")
    public void findHistory(String checkModuleId, String checkModuleName) {

    }

    /**
     * 查询指定表单
     * @param id 表单id
     */
    @GetMapping("/find/cm/one")
    public void findOne(Long id) {

    }

    /**
     * 条件查询
     * @param form 条件查询表单
     */
    @GetMapping("/find/page/query")
    public void findByQuery(@ModelAttribute PageRequestForm form) {

    }





}
