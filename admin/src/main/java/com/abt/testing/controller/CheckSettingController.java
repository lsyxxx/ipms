package com.abt.testing.controller;

import com.abt.chkmodule.entity.CheckModule;
import com.abt.chkmodule.entity.CheckUnit;
import com.abt.chkmodule.model.ChannelEnum;
import com.abt.chkmodule.service.CheckModuleService;
import com.abt.common.config.ValidateGroup;
import com.abt.common.model.R;
import com.abt.sys.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 检测项目相关，包含检测分类，检测项目等
 */
@RestController
@Slf4j
@RequestMapping("/chk/setting")
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
     * 暂存一个检测检测项目
     * 只用输入名称
     */
    @PostMapping("/module/draft")
    public R<Object> checkModuleDraft(@Validated(ValidateGroup.Temp.class) @RequestBody CheckModule checkModule) {
        checkModule.setStatus(CheckModule.STATUS_TEMP);
        checkModuleService.saveCheckModule(checkModule);
        return R.success("暂存成功");
    }

    /**
     * 正式保存一个检测项目
     */
    @PostMapping("/module/save")
    public void checkModuleSave(@Validated(ValidateGroup.Save.class) @RequestBody CheckModule checkModule) {
        //校验
        if (CollectionUtils.isEmpty(checkModule.getCheckItems())) {
            throw new BusinessException("提交失败！未关联检测子参数。");
        }
        checkModule.setStatus(CheckModule.STATUS_PUBLISHED);
        checkModuleService.saveCheckModule(checkModule);
    }


    /**
     * 删除检测项目中一个检测子参数
     *
     * @param id 子参数id
     */
    @GetMapping("/item/delete")
    public R<Object> deleteCheckItem(String id) {
        checkModuleService.deleteCheckItem(id);
        return R.success("删除检测子参数成功");
    }
}
