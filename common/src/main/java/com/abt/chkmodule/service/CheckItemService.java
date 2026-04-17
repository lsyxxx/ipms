package com.abt.chkmodule.service;

import com.abt.chkmodule.entity.CheckItem;

import java.util.List;

public interface CheckItemService extends CheckModuleReference {

    /**
     * 查询指定检测项目下的所有子参数（包含关联的标准）
     */
    List<CheckItem> findCheckItemsByModuleId(String checkModuleId);

    /**
     * 更新子参数启用状态
     */
    void updateItemEnabled(String id, boolean enabled);

    /**
     * 保存或编辑子参数
     */
    void saveItem(CheckItem checkItem);
}
