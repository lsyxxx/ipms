package com.abt.testing.controller;

import com.abt.chkmodule.entity.CheckItem;
import com.abt.chkmodule.entity.CheckModule;
import com.abt.chkmodule.entity.CheckUnit;
import com.abt.chkmodule.model.ChannelEnum;
import com.abt.chkmodule.model.CheckItemSaveDTO;
import com.abt.chkmodule.service.CheckItemService;
import com.abt.chkmodule.service.CheckModuleService;
import com.abt.common.config.ValidateGroup;
import com.abt.common.model.R;
import com.abt.sys.exception.BusinessException;
import com.abt.testing.model.CheckModuleRequestForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    private final CheckItemService checkItemService;

    public CheckSettingController(CheckModuleService checkModuleService, CheckItemService checkItemService) {
        this.checkModuleService = checkModuleService;
        this.checkItemService = checkItemService;
    }


    /**
     * 一般查询检测分类
     *
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
     *
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

    /**
     * 检测项目-条件分页查询
     *
     * @param form 动态查询表单
     */
    @PostMapping("/page")
    public R<Page<CheckModule>> findModulePage(@RequestBody CheckModuleRequestForm form) {
        form.forcePaged();
        Pageable pageable = form.createDefaultPageable();
        Page<CheckModule> page = checkModuleService.findCheckModulesPage(
                form.getQuery(),
                form.getCheckUnitId(),
                form.getUseChannel(),
                form.getEnabled(),
                form.getStatus(),
                form.getCertificates(),
                pageable
        );
        return R.success(page);
    }

    /**
     * 检测项目基础详情
     *
     * @param id 检测项目ID
     */
    @GetMapping("/module/find")
    public R<CheckModule> findCheckModuleDetail(String id) {
        return R.success(checkModuleService.findCheckModuleDetail(id));
    }

    /**
     * 查询指定检测项目的子参数列
     *
     * @param checkModuleId 检测项目的主键 ID
     */
    @GetMapping("/item/find")
    public R<List<CheckItem>> findCheckItems(String checkModuleId) {
        List<CheckItem> items = checkItemService.findCheckItemsByModuleId(checkModuleId);
        return R.success(items);
    }

    /**
     * 子参数-禁用与启用
     *
     * @param id      子参数ID
     * @param enabled 启用状态
     */
    @GetMapping("/item/enabled")
    public R<Object> toggleItemEnabled(String id, boolean enabled) {
        checkItemService.updateItemEnabled(id, enabled);
        return R.success("状态更新成功");
    }

    /**
     * 子参数-保存/编辑
     * * @param dto 子参数保存传输对象
     */
    @PostMapping("/item/save")
    public R<Object> saveItem(@RequestBody CheckItemSaveDTO dto) {
        checkItemService.saveItem(dto);
        return R.success("操作成功");
    }
}
