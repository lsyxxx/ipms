package com.abt.wf.service;

import com.abt.wf.entity.InvoiceApply;
import com.abt.wf.model.InvoiceApplyRequestForm;
import org.camunda.bpm.engine.task.Task;
import org.springframework.data.domain.Page;

import java.util.List;

public interface InvoiceApplyService extends WorkFlowService<InvoiceApply>, BusinessService<InvoiceApplyRequestForm, InvoiceApply> {
    InvoiceApply getEntityWithCurrentTask(String entityId);

    /**
     * 根据合同编号查询该合同的开票记录，仅通过或审批中的
     * @param contractNo 合同编号
     */
    List<InvoiceApply> findSaleAgreementInvoices(String contractNo);
}
