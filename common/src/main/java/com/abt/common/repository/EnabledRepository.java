package com.abt.common.repository;


import java.io.Serializable;

/**
 * 自定义enabledById, disabledById通用方法
 * 实体类必须有enabled字段，且属性名必须为enabled(数据库列名可以不一样)
 * 业务repository接口继承EnabledRepository即可
 */
public interface EnabledRepository<T, ID extends Serializable>{

    /**
     * 根据id启用
     * @param id id
     */
    void enableById(ID id);

    /**
     * 根据id禁用
     * @param id id
     */
    void disableById(ID id);

    /**
     * 批量禁用
     */
    void disableAllByIds(Iterable<ID> ids);

    /**
     * 批量启用
     */
    void enableAllByIds(Iterable<ID> ids);
}
