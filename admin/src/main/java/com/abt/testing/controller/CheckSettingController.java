package com.abt.testing.controller;

import com.abt.chkmodule.entity.CheckModule;
import com.abt.chkmodule.entity.CheckUnit;
import com.abt.chkmodule.model.ChannelEnum;
import com.abt.chkmodule.service.CheckModuleService;
import com.abt.common.config.ValidateGroup;
import com.abt.common.model.R;
import com.abt.sys.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 检测项目相关，包含检测分类，检测项目等
 */
@RestController
@Slf4j
@RequestMapping("/pub/chk/setting")
public class CheckSettingController {

    private final CheckModuleService checkModuleService;

    public CheckSettingController(CheckModuleService checkModuleService) {
        this.checkModuleService = checkModuleService;
    }


    /**
     * 一般查询检测分类
     * @param channel 渠道。不传默认全部
     * @param enabled 是否启用。不传默认全部
     */
    @GetMapping("/unit/list")
    public R<List<CheckUnit>> findChekUnitList(@RequestParam(required = false) ChannelEnum channel, Boolean enabled) {
        final List<CheckUnit> list = checkModuleService.findCheckUnitList(channel, enabled);
        return R.success(list);
    }


    /**
     * 校验名称是否重复
     * @param name 检测项目名称
     */
    @GetMapping("/module/validate/name")
    public R<Boolean> validateDuplicatedName(String name) {
        boolean isValid = checkModuleService.isDuplicatedName(name);
        return R.success(isValid);
    }


    /**
     * 检测项目-暂存
     */
    @PostMapping("/module/draft")
    public R<Object> checkModuleDraft(@Validated(ValidateGroup.Temp.class) @RequestBody CheckModule checkModule) {
        checkModule.setStatusTemp();
        checkModuleService.saveCheckModule(checkModule);
        return R.success("暂存成功");
    }

    /**
     * 检测项目-发布新增
     */
    @PostMapping("/module/save")
    public R<Object> checkModuleSave(@Validated(ValidateGroup.Save.class) @RequestBody CheckModule checkModule) {
        if (CollectionUtils.isEmpty(checkModule.getCheckItems())) {
            throw new BusinessException("提交失败！未关联检测子参数。");
        }
        checkModule.setPublished();
        checkModuleService.saveCheckModule(checkModule);

        return R.success("发布成功");
    }

    /**
     * 检测项目-启用
     */
    @GetMapping("/module/enable")
    public R<Object> enableModule(String id) {
        checkModuleService.enabledCheckModule(id);
        return R.success("启用成功");
    }

    /**
     * 检测项目-禁用
     */
    @GetMapping("/module/disable")
    public R<Object> disableModule(String id) {
        checkModuleService.disabledCheckModule(id);
        return R.success("禁用成功");
    }

    /**
     * 检测项目-删除暂存
     * * @param id 检测项目主键ID
     */
    @GetMapping("/module/draft/delete")
    public R<Object> deleteCheckModuleDraft(String id) {
        checkModuleService.deleteCheckModuleDraft(id);
        return R.success("删除暂存成功");
    }
}
