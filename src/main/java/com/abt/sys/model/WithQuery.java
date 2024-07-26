package com.abt.sys.model;

public interface WithQuery<T> {
    /**
     * 查询后处理
     */
    T afterQuery();
}
