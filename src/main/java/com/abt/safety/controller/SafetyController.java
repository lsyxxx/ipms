package com.abt.safety.controller;

import com.abt.common.model.R;
import com.abt.common.util.TokenUtil;
import com.abt.safety.entity.SafetyForm;
import com.abt.safety.entity.SafetyItem;
import com.abt.safety.entity.SafetyRecord;
import com.abt.safety.model.RectifyRequest;
import com.abt.safety.model.SafeItemRequestForm;
import com.abt.safety.model.SafetyFormRequestForm;
import com.abt.safety.model.SafetyRecordRequestForm;
import com.abt.safety.service.SafetyConfigService;

import com.abt.safety.service.SafetyRecordService;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.WithQuery;
import com.abt.sys.model.dto.UserView;
import com.abt.sys.util.WithQueryUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 安全检查
 */
@RestController
@Slf4j
@RequestMapping("/safety")
@RequiredArgsConstructor
public class SafetyController {
    

    private final SafetyConfigService safetyConfigService;
    private final SafetyRecordService safetyRecordService;



    /**
     * 获取安全检查项目配置列表
     *
     * @return 分页结果
     */
    @GetMapping("/item/config/page")
    public R<Page<SafetyItem>> getSafetyItemConfigPage(@ModelAttribute SafeItemRequestForm requestForm) {
        Page<SafetyItem> page = safetyConfigService.getSafetyItemConfigPage(requestForm);
        return R.success(page, "获取安全检查项目配置列表成功");
    }

    /**
     * 添加或更新安全检查项目配置
     *
     * @param safetyItem 安全检查项目配置
     * @return 操作结果
     */
    @PostMapping("/item/config/add")
    public R<SafetyItem> addSafetyItemConfig(@RequestBody SafetyItem safetyItem) {
        safetyConfigService.validateBeforAdd(safetyItem);
        SafetyItem savedItem = safetyConfigService.saveSafetyItemConfig(safetyItem);
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
        
        safetyConfigService.updateSafetyItemEnabled(id, enabled);
        
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
        safetyConfigService.logicDeleteSafetyItemConfig(id);
        return R.success("删除成功");
    }

    @GetMapping("/item/config/get")
    public R<SafetyItem> getSafetyItemConfig(@RequestParam String id) {
        SafetyItem safetyItem = safetyConfigService.getSafetyItem(id);
        return R.success(safetyItem, "获取安全检查项目配置成功");
    }

    @GetMapping("/item/config/check/name")
    public R<Boolean> checkDuplicateName(@RequestParam String name) {
        boolean isDuplicate = safetyConfigService.validateDuplicateSafetyItemName(name);
        return R.success(isDuplicate, "检查项目名称是否已存在");
    }

    @GetMapping("/item/sortNo")
    public R<Integer> getNextSortNo() {
        final Integer nextSortNo = safetyConfigService.getNextSortNo();
        return R.success(nextSortNo);
    }

    @PostMapping("/form/save")
    public R<Object> saveSafetyForm(@RequestBody SafetyForm safetyForm) {
        final boolean exists = safetyConfigService.checkSafetyFormLocationExists(safetyForm.getLocation(), safetyForm.getId());
        if (exists) {
            throw new BusinessException("检查地点：" + safetyForm.getLocation() + "已存在，请重新设置");
        }
        safetyConfigService.saveForm(safetyForm);
        return R.success("保存表单成功!");
    }

    @GetMapping("/form/page")
    public R<Page<SafetyForm>> findFormPage(@ModelAttribute SafetyFormRequestForm requestForm) {
        final Page<SafetyForm> page = safetyConfigService.findByQueryPageable(requestForm);
        return R.success(page, "查询成功");
    }

    @GetMapping("/form/check/location")
    public R<Boolean> checkSafetyFormLocation(String location, Long id) {
        final boolean exists = safetyConfigService.checkSafetyFormLocationExists(location, id);
        return R.success(exists);
    }

    @GetMapping("/form/update/enabled")
    public R<Object> updateSafetyFormEnabled(String id, boolean enabled) {
        safetyConfigService.updateSafetyFormEnabled(id, enabled);
        return R.success("更新成功");
    }

    @GetMapping("/form/delete")
    public R<Object> logicDeleteSafetyForm(@RequestParam String id) {
        safetyConfigService.logicDeleteSafetyForm(id);
        return R.success("已删除安全表单");
    }

    @GetMapping("/form/load")
    public R<SafetyForm> loadSafetyFormWithItems(@RequestParam String id, boolean withItems) {
        if (withItems) {
            return R.success(safetyConfigService.loadSafetyFormWithItems(id), "读取成功");
        } else {
            return R.success(safetyConfigService.loadSafetyForm(id), "读取成功");
        }
    }


    @GetMapping("/record/validate")
    public R<Boolean> validateSafetyFormApply(Long formId) {
        LocalDate now = LocalDate.now();
        final boolean exist = safetyRecordService.recordExist(now, formId);
        if (exist) {
            throw new BusinessException(String.format("%s 已存在安全检查记录，请勿重复提交。", now));
        }
        return R.success("验证通过");
    }

    /**
     * 新增安全检查表单
     */
    @PostMapping("/record/check")
    public R<Object> saveFormRecord(@Validated @RequestBody SafetyForm safetyForm) {
        LocalDate now = LocalDate.now();
        final boolean exist = safetyRecordService.recordExist(now, safetyForm.getId());
        if (safetyForm.getId() == null) {
            throw new BusinessException("请选择检查地点并填写表单后提交");
        }
        if (exist) {
            throw new BusinessException(String.format("%s 已存在安全检查记录，请勿重复提交。", now));
        }
        safetyRecordService.saveCheck(safetyForm);
        return R.success("安全检查记录保存成功!");
    }

    @GetMapping("/record/load")
    public R<SafetyRecord> loadFormRecord(String id) {
        final SafetyRecord record = safetyRecordService.loadRecord(id);
        return R.success(record, "读取成功");
    }

    @GetMapping("/record/page")
    public R<Page<SafetyRecord>> findFormRecordPage(@ModelAttribute SafetyRecordRequestForm requestForm) {
        final Page<SafetyRecord> page = safetyRecordService.findSafetyRecordPageable(requestForm);
        WithQueryUtil.build(page);
        return R.success(page, "查询成功");
    }

    /**
     * 调度人分配
     *
     * @param id              record id
     * @param rectifierJno  负责人工号
     * @param rectifierName 负责人姓名
     */
    @Secured("SAFETY_RECORD_DISPATCH")
    @GetMapping("/record/dispatch")
    public R<Object> recordDispatch(String id, String rectifierJno, String rectifierName) {
        final UserView dispatcher = TokenUtil.getUserFromAuthToken();
        safetyRecordService.dispatch(id, dispatcher.getEmpnum(), dispatcher.getName(), rectifierJno, rectifierName);
        return R.success("已分配负责人:" + rectifierName);
    }

    @PostMapping("/record/rectify")
    public R<Object> recordRectify(@RequestBody RectifyRequest rectifyRequest) {
        safetyRecordService.rectified(rectifyRequest.getId(), rectifyRequest.getRectifyRemark(), rectifyRequest.getFiles());
        return R.success("已整改");
    }






}