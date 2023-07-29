package com.ipms.sys.service;

import java.util.List;

/**
 * 基本CRUD
 * T 数据库对象
 * K 主键对象
 */
public interface CrudService<T, K> {

    List<T> findAll();

    void delete(T t);

    T findById(K id);

    void update(T t);

    /**
     * 返回主键
     * @param t
     * @return
     */
    Long insert(T t);

}
