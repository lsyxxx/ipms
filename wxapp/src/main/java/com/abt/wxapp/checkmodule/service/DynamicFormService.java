package com.abt.wxapp.checkmodule.service;

import com.abt.wxapp.checkmodule.entity.DynamicForm;

import java.util.List;

public interface DynamicFormService {
    /**
     * 保存表单
     * @param form 提交的动态表单
     */
    void save(DynamicForm form);

    /**
     * 查询该检测项目下 id 最大的表单（最新插入的一版）
     * @param checkModuleId 检测项目id
     * @param checkModuleName 检测项目名称
     * @return DynamicForm表单内容
     */
    DynamicForm findNewestByCheckModuleId(String checkModuleId, String checkModuleName);

    /**
     * 所有检测项目各自最新一条表单（同一 cm_id 下 id 最大）
     */
    List<DynamicForm> findAllCheckModuleDynamicForm();

    void findOneBy(Long id);
}
