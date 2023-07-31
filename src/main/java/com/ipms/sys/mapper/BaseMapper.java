package com.ipms.sys.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *
 * @param <R> 返回数据
 * @param <V> 查询条件
 */
public interface BaseMapper<R, V> {

    R findById(Long id);

    R findBy(V v);

    List<R> findAll();

    long count();

    /**
     * 插入一条数据，插入成功后返回id
     */
    long insert(R r);



}
