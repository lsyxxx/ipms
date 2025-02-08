package com.abt.wf.service;

import com.abt.wf.entity.PurchaseApplyMain;
import com.abt.wf.model.PurchaseApplyRequestForm;

import java.io.File;
import java.io.IOException;

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

    File createExcel(String id, String path) throws Exception;

    void setBusinessId(String procId, String entityId);

    void setMangerUser(String userid, PurchaseApplyMain form);

    void setLeaderUser(String userid, PurchaseApplyMain form);

    void setCeoUser(String userid, PurchaseApplyMain form);

    void delete(String id);

    /**
     * 生成供应品验收单
     * @param form 采购申请表单
     * @param path 保存地址
     */
    File createAcceptExcel(PurchaseApplyMain form, String path) throws IOException;
}
