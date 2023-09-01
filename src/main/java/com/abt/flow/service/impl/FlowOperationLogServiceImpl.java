package com.abt.flow.service.impl;

import com.abt.flow.model.entity.FlowOperationLog;
import com.abt.flow.repository.FlowOperationLogRepository;
import com.abt.flow.service.FlowOperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service
@Slf4j
public class FlowOperationLogServiceImpl implements FlowOperationLogService {


    private final static String ORDER_OPTDATE = "operateDate";

    private final FlowOperationLogRepository flowOperationLogRepository;

    public FlowOperationLogServiceImpl(FlowOperationLogRepository flowOperationLogRepository) {
        this.flowOperationLogRepository = flowOperationLogRepository;
    }


    @Override
    public void insertOne(FlowOperationLog entity) {
        log.info("插入一条流程操作记录 -- {}", entity.toString());
        flowOperationLogRepository.save(entity);
    }

    @Override
    public List<FlowOperationLog> getByProcessInstanceId(String processInstanceId) {
        FlowOperationLog prop = FlowOperationLog.of();
        prop.setProcInstId(processInstanceId);
        Example<FlowOperationLog> example = Example.of(prop);
        return flowOperationLogRepository.findAll(example);
    }


    public void getAllOrderByOperateDate(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.DESC, ORDER_OPTDATE);
        Page<FlowOperationLog> all = flowOperationLogRepository.findAll(pageRequest);
    }

}
