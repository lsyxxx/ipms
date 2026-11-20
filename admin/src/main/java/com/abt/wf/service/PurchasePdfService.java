package com.abt.wf.service;

import com.abt.wf.entity.PurchaseApplyMain;
import com.abt.wf.model.PurchasePdfSignatures;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/** 使用开源 PDF 库生成采购单据，独立于旧版 Aspose 导出。 */
public interface PurchasePdfService {
    byte[] createPdf(String id) throws IOException;

    byte[] createAcceptPdf(String id) throws IOException;

    /** 同一文档内先写申请单，再从新页写验收记录，仅保存一次 PDF。 */
    Path createDocumentPdf(PurchaseApplyMain main, Path target) throws IOException;

    /** 收集当前批次实际需要的签名用户并去重，批量查询签名文件信息。 */
    PurchasePdfSignatures findSignatures(List<PurchaseApplyMain> mains);

    /** 使用已查询的批次签名生成单据，生成期间不再逐人查库。 */
    Path createDocumentPdf(PurchaseApplyMain main, Path target, PurchasePdfSignatures signatures) throws IOException;
}
