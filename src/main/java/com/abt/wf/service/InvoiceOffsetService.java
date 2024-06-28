package com.abt.wf.service;

import com.abt.wf.entity.InvoiceOffset;
import com.abt.wf.model.InvoiceOffsetRequestForm;
import com.abt.wf.model.InvoiceOffsetRequestForm;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

public interface InvoiceOffsetService extends WorkFlowService<InvoiceOffset>, BusinessService<InvoiceOffsetRequestForm, InvoiceOffset>{

    Page<InvoiceOffset> findAllByCriteria(InvoiceOffsetRequestForm requestForm);

    Page<InvoiceOffset> findAllByQueryPaged(InvoiceOffsetRequestForm requestForm);

    Page<InvoiceOffset> findMyTodoByQueryPaged(InvoiceOffsetRequestForm requestForm);

    Page<InvoiceOffset> findMyDoneByQueryPaged(InvoiceOffsetRequestForm requestForm);

    Page<InvoiceOffset> findMyApplyByQueryPaged(InvoiceOffsetRequestForm requestForm);

    InvoiceOffset getEntityWithCurrentTask(String entityId);

}
