package com.abt.safety.controller;

import com.abt.common.model.R;
import com.abt.safety.entity.SafetyForm;
import com.abt.safety.entity.SafetyItem;
import com.abt.safety.model.SafeItemRequestForm;
import com.abt.safety.model.SafetyFormRequestForm;
import com.abt.safety.service.SafetyService;

import com.abt.sys.exception.BusinessException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 安全检查
 */
@RestController
@Slf4j
@RequestMapping("/safety")
public class SafetyController {
    

    private final SafetyService safetyService;
    
    
    public SafetyController(SafetyService safetyService) {
        this.safetyService = safetyService;
    }

    /**
     * 获取安全检查项目配置列表
     *
     * @return 分页结果
     */
    @GetMapping("/item/config/list")
    public R<List<SafetyItem>> getSafetyItemConfigList(@ModelAttribute SafeItemRequestForm requestForm) {
        Page<SafetyItem> page = safetyService.getSafetyItemConfigList(requestForm);
        return R.success(page.getContent(), "获取安全检查项目配置列表成功");
    }

    /**
     * 添加或更新安全检查项目配置
     *
     * @param safetyItem 安全检查项目配置
     * @return 操作结果
     */
    @PostMapping("/item/config/add")
    public R<SafetyItem> addSafetyItemConfig(@RequestBody SafetyItem safetyItem) {
        safetyService.validateBeforAdd(safetyItem);
        SafetyItem savedItem = safetyService.saveSafetyItemConfig(safetyItem);
        return R.success(savedItem, "保存成功");
    }

    /**
     * 更新安全检查项目状态
     *
     * @param id      检查项目ID
     * @param enabled 是否启用
     * @return 操作结果
     */
    @GetMapping("/item/config/update/enabled")
    @Transactional
    public R<Object> updateSafetyItemEnabled(
            @RequestParam String id,
            @RequestParam Boolean enabled) {
        
        safetyService.updateSafetyItemEnabled(id, enabled);
        
        return R.success("状态更新成功");
    }

    /**
     * 删除安全检查项目配置
     *
     * @param id 检查项目ID
     * @return 操作结果
     */
    @GetMapping("/item/config/delete")
    @Transactional
    public R<Object> deleteSafetyItemConfig(@RequestParam String id) {
        safetyService.logicDeleteSafetyItemConfig(id);
        return R.success("删除成功");
    }

    @GetMapping("/item/config/get")
    public R<SafetyItem> getSafetyItemConfig(@RequestParam String id) {
        SafetyItem safetyItem = safetyService.getSafetyItem(id);
        return R.success(safetyItem, "获取安全检查项目配置成功");
    }

    @GetMapping("/item/config/check/name")
    public R<Boolean> checkDuplicateName(@RequestParam String name) {
        boolean isDuplicate = safetyService.validateDuplicateSafetyItemName(name);
        return R.success(isDuplicate, "检查项目名称是否已存在");
    }

    @GetMapping("/item/sortNo")
    public R<Integer> getNextSortNo() {
        final Integer nextSortNo = safetyService.getNextSortNo();
        return R.success(nextSortNo);
    }

    @PostMapping("/form/save")
    public R<Object> saveSafetyForm(@RequestBody SafetyForm safetyForm) {
        final boolean exists = safetyService.checkSafetyFormLocationExists(safetyForm.getLocation(), safetyForm.getId());
        if (exists) {
            throw new BusinessException("检查地点：" + safetyForm.getLocation() + "已存在，请重新设置");
        }
        safetyService.saveForm(safetyForm);
        return R.success("保存表单成功!");
    }

    @GetMapping("/form/list")
    public R<Page<SafetyForm>> findFormList(@ModelAttribute SafetyFormRequestForm requestForm) {
        final Page<SafetyForm> page = safetyService.findByQueryPageable(requestForm);
        return R.success(page, "查询成功");
    }

    @GetMapping("/form/check/location")
    public R<Boolean> checkSafetyFormLocation(String location, String id) {
        final boolean exists = safetyService.checkSafetyFormLocationExists(location, id);
        return R.success(exists);
    }

    @GetMapping("/form/update/enabled")
    public R<Object> updateSafetyFormEnabled(String id, boolean enabled) {
        safetyService.updateSafetyFormEnabled(id, enabled);
        return R.success("更新成功");
    }

    @GetMapping("/form/delete")
    public R<Object> logicDeleteSafetyForm(@RequestParam String id) {
        safetyService.logicDeleteSafetyForm(id);
        return R.success("已删除安全表单");
    }

    @GetMapping("/form/load")
    public R<SafetyForm> loadSafetyFormWithItems(@RequestParam String id, boolean withItems) {
        if (withItems) {
            return R.success(safetyService.loadSafetyFormWithItems(id), "读取成功");
        } else {
            return R.success(safetyService.loadSafetyForm(id), "读取成功");
        }
    }
}