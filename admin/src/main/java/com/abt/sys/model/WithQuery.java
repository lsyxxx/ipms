package com.abt.sys.model;


/**
 * 处理查询后的结果
 * @param <T>
 */
public interface WithQuery<T> {
    /**
     * 查询后处理
     */
    T afterQuery();


    /**
     * 简化结果，配合JsonIgnore
     * 传入一个空对象，仅添加需要的值
     * 让返回给前端的数据更干净
     */
    default void simple() {
    };

}
