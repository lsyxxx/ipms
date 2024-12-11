package com.abt.wf.service;

import com.abt.wf.entity.PurchaseApplyMain;
import com.abt.wf.model.PurchaseApplyRequestForm;

import java.io.File;

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

    File createPdf(String id, String pdfPath) throws Exception;

    void setBusinessId(String procId, String entityId);

    void setMangerUser(String userid, PurchaseApplyMain form);

    void setLeaderUser(String userid, PurchaseApplyMain form);
}
