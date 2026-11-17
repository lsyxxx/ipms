package com.abt.wf.service;

import com.abt.wf.model.PurchaseDocumentExportRequest;
import com.abt.wf.model.PurchaseDocumentExportVO;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/** 采购单据后台导出与任务查询。 */
public interface PurchaseDocumentExportService {
    PurchaseDocumentExportVO save(PurchaseDocumentExportRequest request);

    List<PurchaseDocumentExportVO> findList();

    PurchaseDocumentExportVO findById(String id);

    Path findZip(String id) throws IOException;
}
