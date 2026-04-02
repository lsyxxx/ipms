package com.abt.chkmodule.service;

import com.abt.chkmodule.entity.CheckItem;
import com.abt.chkmodule.entity.CheckModule;
import com.abt.chkmodule.entity.CheckUnit;
import com.abt.chkmodule.model.ChannelEnum;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;

public interface CheckModuleService {
    /**
     * 一般查询检测分类
     * @param channelEnum 渠道
     * @param enabled 是否启用
     */
    List<CheckUnit> findCheckUnitList(ChannelEnum channelEnum, Boolean enabled);

    /**
     * 删除一个检测子参数
     * @param id 子参数id
     */
    void deleteCheckItem(String id);

    /**
     * 检测项目名称是否重复
     * @param name 检测项目名臣
     */
    boolean isDuplicatedName(@NotNull String name);

    Optional<CheckModule> findById(String id);

    /**
     * 保存一个检测子参数，并关联检测项目
     * @param checkModuleId 检测项目id
     * @param checkItem 检测子参数对象
     */
    void saveCheckItemOne(String checkModuleId, CheckItem checkItem);

    /**
     * 保存检测项目子参数列表
     * @param checkModuleId 检测项目id
     * @param checkItemList 关联子参数列表
     */
    void saveCheckItemList(String checkModuleId, List<CheckItem> checkItemList);

    /**
     * 保存（正式发布）一个
     * @param checkModule 检测项目对象，含关联的子参数等
     */
    void saveCheckModule(CheckModule checkModule);

    /**
     * 不启用检测项目
     * @param id id
     */
    void disabledCheckModule(String id);

    void deleteCheckModuleById(String id);
}
