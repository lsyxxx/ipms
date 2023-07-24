package com.ipms.sys.mapper;

import java.util.List;

/**
 *
 * @param <R> 返回数据
 * @param <V> 查询条件
 */
public interface BaseMapper<R, V> {

    R findById(Integer id);

    R findBy(V v);

    R save(V v);

    List<R> findAll();

    long count();

    void delete();

    /**
     * 插入一条数据，插入成功后返回id
     * @param v
     * @return
     */
    long insert(V v);



}
