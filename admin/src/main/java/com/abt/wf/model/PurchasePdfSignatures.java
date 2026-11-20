package com.abt.wf.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 当前批次的签名快照，采购申请单与验收记录共享，不在渲染期间查询数据库。 */
public final class PurchasePdfSignatures {
    private final Map<String, PurchasePdfSignatureFile> files = new HashMap<>();

    public PurchasePdfSignatures(List<PurchasePdfSignatureFile> signatures) {
        for (PurchasePdfSignatureFile signature : signatures) files.put(signature.userId(), signature);
    }

    public PurchasePdfSignatureFile findByUserid(String userid) {
        return files.get(userid);
    }
}
