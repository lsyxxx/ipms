package com.abt.safety.service;

import com.abt.safety.entity.SafetyForm;
import com.abt.safety.model.SafeItemRequestForm;
import com.abt.safety.entity.SafetyItem;

import com.abt.safety.model.SafetyFormRequestForm;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

public interface SafetyService {

    /**
     * 获取安全检查项目配置列表
     * @param requestForm
     * @return
     */
    Page<SafetyItem> getSafetyItemConfigList(SafeItemRequestForm requestForm);

    boolean validateDuplicateSafetyItemName(String name);

    /**
     * 验证添加安全检查项目配置前的数据
     * @param safetyItem
     */
    void validateBeforAdd(SafetyItem safetyItem);

    /**
     * 保存安全检查项目配置
     * @param safetyItem
     * @return
     */
    SafetyItem saveSafetyItemConfig(SafetyItem safetyItem);

    /**
     * 更新安全检查项目状态
     * @param id id
     * @param enabled 是否启用
     */
    void updateSafetyItemEnabled(String id, boolean enabled);

    void logicDeleteSafetyItemConfig(String id);

    /**
     * 获取安全检查项目配置
     * @param id id
     * @return SafetyItem
     */
    SafetyItem getSafetyItem(String id);

    /**
     * 获取下一个排序号
     *
     * @return 新的排序号，等于未删除项目数量 + 1
     */
    Integer getNextSortNo();

    /**
     * 保存表单
     * @param form 表单
     */
    @Transactional
    SafetyForm saveForm(SafetyForm form);

    /**
     * 读取检查表单，包含关联的项目
     *
     * @return
     */
    SafetyForm loadSafetyFormWithItems(String id);

    SafetyForm loadSafetyForm(String id);

    Page<SafetyForm> findByQueryPageable(SafetyFormRequestForm requestForm);

    boolean checkSafetyFormLocationExists(String location, String id);

    void updateSafetyFormEnabled(String id, boolean enabled);

    void logicDeleteSafetyForm(String id);
}