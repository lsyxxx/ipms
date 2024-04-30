package com.abt.wf.service;

import com.abt.wf.entity.InvoiceOffset;
import com.abt.wf.model.InvoiceOffsetRequestForm;
import org.springframework.stereotype.Service;

import java.util.List;

public interface InvoiceOffsetService extends WorkFlowService<InvoiceOffset>, BusinessService<InvoiceOffsetRequestForm, InvoiceOffset>{


    InvoiceOffset getEntityWithCurrentTask(String entityId);

    List<InvoiceOffset> findDone(InvoiceOffsetRequestForm form);

    List<InvoiceOffset> findTodo(InvoiceOffsetRequestForm form);
}
