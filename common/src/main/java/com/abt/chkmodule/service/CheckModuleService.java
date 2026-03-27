package com.abt.chkmodule.service;

import com.abt.chkmodule.entity.CheckModule;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;

public interface CheckModuleService {
    /**
     * 暂存检测项目配置
     * @param checkModule 检测项目对象
     */
    void draft(CheckModule checkModule);

    /**
     * 检测项目名称是否重复
     * @param name 检测项目名臣
     */
    boolean isDuplicatedName(@NotNull String name);

    Optional<CheckModule> findById(String id);
}
