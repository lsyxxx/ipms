package com.abt.flow.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 */
@Slf4j
public class BaseTest {


    public static void notEmpty(List list) {
        Assert.notEmpty(list, "---------- List is null!");
    }


    public static void countList(List list) {
        notEmpty(list);
        log.info("------------ 列表有{}个元素", list.size());
    }

    public static void logListElement(List list) {
        countList(list);
        list.forEach(i -> {
            log.info("---- simpleLog: {}", i.toString());
        });
    }

    public static void logTask(Task task) {
        log.info("---------- TASK: name: {}, id: {},  procInstId: {}, assignee: {}, procDefId: {}",
                task.getName(), task.getId(), task.getProcessInstanceId(), task.getAssignee(), task.getProcessDefinitionId());
    }

    public static void logTask(HistoricTaskInstance task) {
        log.info("---------- TASK: name: {}, id: {},  procInstId: {}, assignee: {}, procDefId: {}, endTime: {}",
                task.getName(), task.getId(), task.getProcessInstanceId(), task.getAssignee(), task.getProcessDefinitionId(), task.getEndTime());
    }

    public static void logProcess(ProcessInstance process) {
        //几乎只有id
        log.info("-----------Runtime PROCESS instance: id: {}, name: {}, procDefId: {}, actId: {}, isEnded: {}",
                process.getId(), process.getName(), process.getProcessDefinitionId(), process.getActivityId(), process.isEnded());
    }


    static void logHistoryProcess(HistoricProcessInstance process) {
        log.info("-----------History PROCESS instance: id: {}, name: {}, procDefId: {}, endTime: {}, deleteReason: {}, businessState: {}",
                process.getId(), process.getName(), process.getProcessDefinitionId(), process.getEndTime(), process.getDeleteReason(), process.getBusinessStatus());
    }


}
