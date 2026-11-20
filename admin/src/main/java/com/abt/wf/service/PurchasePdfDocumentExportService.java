package com.abt.wf.service;

import com.abt.wf.model.PurchaseDocumentExportRequest;
import com.abt.wf.model.PurchaseDocumentExportVO;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/** 新版采购 PDF 单据后台导出与任务查询。 */
public interface PurchasePdfDocumentExportService {
    PurchaseDocumentExportVO save(PurchaseDocumentExportRequest request);

    List<PurchaseDocumentExportVO> findList();

    PurchaseDocumentExportVO findById(String id);

    /** 仅创建者可取消任务；生成中的任务在当前文件结束后停止。 */
    PurchaseDocumentExportVO cancel(String id);

    Path findZip(String id) throws IOException;
}
