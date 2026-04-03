package com.abt.testing.service;

import jakarta.transaction.Transactional;

public interface CheckModuleSettingService {
    /**
     * 删除检测项目
     * 双重验证保证正确删除：
     * 1. 业务表查询
     * 2. 数据库外键
     * @param id 检测项目id
     */
    @Transactional
    void deleteCheckModuleById(String id);
}
