package com.abt.sys.model;

public interface WithQuery<T> {
    /**
     * 查询后处理
     */
    T afterQuery();

//    /**
//     * 保存前处理
//     */
//    default T beforePersist() {
//        return null;
//    };
}
