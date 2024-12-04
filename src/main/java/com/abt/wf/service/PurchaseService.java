package com.abt.wf.service;

import com.abt.finance.service.ICashCreditService;
import com.abt.wf.entity.PurchaseApplyMain;
import com.abt.wf.entity.Reimburse;
import com.abt.wf.model.PurchaseApplyRequestForm;
import com.abt.wf.model.ReimburseRequestForm;

/**
 * 采购流程
 */
public interface PurchaseService extends WorkFlowService<PurchaseApplyMain>, BusinessService<PurchaseApplyRequestForm, PurchaseApplyMain> {
    void skipEmptyUserTask(PurchaseApplyMain form);

    /**
     * 暂存草稿
     */
    void tempSave(PurchaseApplyMain entity);

    void setCostVariable(PurchaseApplyMain entity);

    void accept(PurchaseApplyMain form);
}
