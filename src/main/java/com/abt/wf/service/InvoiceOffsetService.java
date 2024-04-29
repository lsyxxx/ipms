package com.abt.wf.service;

import com.abt.wf.entity.InvoiceOffset;
import com.abt.wf.model.InvoiceOffsetRequestForm;
import org.springframework.stereotype.Service;

public interface InvoiceOffsetService extends WorkFlowService<InvoiceOffset>, BusinessService<InvoiceOffsetRequestForm, InvoiceOffset>{


    InvoiceOffset getEntityWithCurrentTask(String entityId);
}
