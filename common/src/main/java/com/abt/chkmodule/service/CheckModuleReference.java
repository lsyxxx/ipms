package com.abt.chkmodule.service;

/**
 * 关联了checkmoduleId
 */
public interface CheckModuleReference {

    /**
     * 根据检测项目id删除
     * @param checkModuleId 检测项目id
     */
    void deleteByCheckModuleId(String checkModuleId);

    /**
     * 是否存在指定检测项目id的数据
     * @param checkModuleId 检测项目id
     */
    boolean existsByCheckModuleId(String checkModuleId);

    /**
     * 服务名称
     */
    String getServiceChineseName();


    /**
     * 关联的表名
     */
    String getTableName();
}
