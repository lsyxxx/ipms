package com.abt.chkmodule.service;

import com.abt.chkmodule.entity.CheckStandard;

import java.util.List;

public interface CheckStandardService {
    /**
     * 查询指定子参数的标准，如果checkItemIds传入空，那么返回空
     * @param checkItemIds 检测子参数
     */
    List<CheckStandard> findByCheckItemIdIn(List<String> checkItemIds);
}
