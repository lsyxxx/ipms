package com.abt.wf.service;

import com.abt.wf.entity.InvoiceApply;
import com.abt.wf.model.InvoiceApplyRequestForm;
import org.camunda.bpm.engine.task.Task;
import org.springframework.data.domain.Page;

public interface InvoiceApplyService extends WorkFlowService<InvoiceApply>, BusinessService<InvoiceApplyRequestForm, InvoiceApply> {
    Page<InvoiceApply> findMyApplyByQueryPaged(InvoiceApplyRequestForm requestForm);

    Page<InvoiceApply> findAllByQueryPaged(InvoiceApplyRequestForm requestForm);

    Page<InvoiceApply> findMyTodoByQueryPaged(InvoiceApplyRequestForm requestForm);

    Page<InvoiceApply> findMyDoneByQueryPaged(InvoiceApplyRequestForm requestForm);

    InvoiceApply getEntityWithCurrentTask(String entityId);
}
