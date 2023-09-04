package com.abt.flow.service;

import com.abt.flow.model.entity.FlowOperationLog;

import java.util.List;

public interface FlowOperationLogService {


    /**
     * 写入一条记录
     * @param log
     */
    void insertOne(FlowOperationLog log);


    List<FlowOperationLog> getByProcessInstanceId(String processInstanceId);


}
