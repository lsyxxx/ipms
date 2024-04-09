package com.abt.wf.service;

import com.abt.wf.entity.InvoiceApply;
import com.abt.wf.model.InvoiceApplyRequestForm;

public interface InvoiceApplyService extends WorkFlowService<InvoiceApply>, BusinessService<InvoiceApplyRequestForm, InvoiceApply> {
}
