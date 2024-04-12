package com.abt.wf.service;

import com.abt.wf.entity.InvoiceApply;
import com.abt.wf.model.InvoiceApplyRequestForm;
import org.camunda.bpm.engine.task.Task;

public interface InvoiceApplyService extends WorkFlowService<InvoiceApply>, BusinessService<InvoiceApplyRequestForm, InvoiceApply> {
    InvoiceApply getEntityWithCurrentTask(String entityId);
}
