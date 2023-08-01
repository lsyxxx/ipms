package com.ipms.sys.service;

import java.util.List;

public interface BatchProcessService<T> {

    void insertBatch(List<T> list);
}
