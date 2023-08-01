package com.ipms.sys.mapper;

import java.util.List;

/**
 * 批处理
 *
 */
public interface BatchMapper<T> {

    void insertBatch(List<T> list);
}
