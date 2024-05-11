package com.abt.wf.service;

import com.abt.wf.entity.InvoiceOffset;
import com.abt.wf.model.InvoiceOffsetRequestForm;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

public interface InvoiceOffsetService extends WorkFlowService<InvoiceOffset>, BusinessService<InvoiceOffsetRequestForm, InvoiceOffset>{

    Page<InvoiceOffset> findAllByCriteria(InvoiceOffsetRequestForm requestForm);

    InvoiceOffset getEntityWithCurrentTask(String entityId);

}
