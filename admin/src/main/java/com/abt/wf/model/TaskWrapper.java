package com.abt.wf.model;

import lombok.*;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.task.Task;

/**
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskWrapper {
    private Task task;
    private HistoricTaskInstance historicTaskInstance;
    private String entityId;
    private String serviceName;
    private String description;
    private String starter;
}
