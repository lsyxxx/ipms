package com.abt.chkmodule.service;

import com.abt.chkmodule.entity.CheckItem;
import com.abt.chkmodule.entity.CheckModule;
import com.abt.chkmodule.entity.CheckUnit;
import com.abt.chkmodule.model.ChannelEnum;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

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
     * 检测项目-发布新增/暂存
     * @param checkModule 检测项目对象，含关联的子参数等
     */
    void saveCheckModule(CheckModule checkModule);

    /**
     * 检测项目-启用
     */
    void enabledCheckModule(String id);

    /**
     * 检测项目-禁用
     */
    void disabledCheckModule(String id);
    /**
     * 检测项目-删除暂存
     * @param id 检测项目id
     */
    void deleteCheckModuleDraft(String id);

    /**
     * 检测项目-条件分页查询
     */
    Page<CheckModule> findCheckModulesPage(String query,
                                           String checkUnitId,
                                           ChannelEnum useChannel,
                                           Boolean enabled,
                                           Integer status,
                                           List<String> certificates,
                                           Pageable pageable);

    /**
     * 获取检测项目基础详情（包含关联仪器、关联检测项目，不含子参数）
     * @param id 检测项目ID
     */
    CheckModule findCheckModuleDetail(String id);}
