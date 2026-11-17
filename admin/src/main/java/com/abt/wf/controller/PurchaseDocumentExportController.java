package com.abt.wf.controller;

import com.abt.common.model.R;
import com.abt.wf.model.PurchaseDocumentExportRequest;
import com.abt.wf.model.PurchaseDocumentExportVO;
import com.abt.wf.service.PurchaseDocumentExportService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

/** 采购单据导出接口，文件通过资源流传输，不整体读取进内存。 */
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/purchase/document-export")
public class PurchaseDocumentExportController {
    private final PurchaseDocumentExportService purchaseDocumentExportService;

    @PostMapping("/save")
    public R<PurchaseDocumentExportVO> save(@Valid @RequestBody PurchaseDocumentExportRequest request) {
        return R.success(purchaseDocumentExportService.save(request));
    }

    @GetMapping("/findList")
    public R<List<PurchaseDocumentExportVO>> findList() {
        return R.success(purchaseDocumentExportService.findList());
    }

    @GetMapping("/findById")
    public R<PurchaseDocumentExportVO> findById(@RequestParam @NotBlank(message = "导出任务编号不能为空") String id) {
        return R.success(purchaseDocumentExportService.findById(id));
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> download(@RequestParam @NotBlank(message = "导出任务编号不能为空") String id) throws IOException {
        Path zip = purchaseDocumentExportService.findZip(id);
        PurchaseDocumentExportVO task = purchaseDocumentExportService.findById(id);
        FileSystemResource resource = new FileSystemResource(zip);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/zip"))
                .contentLength(resource.contentLength())
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(task.getFileName(), StandardCharsets.UTF_8).build().toString())
                .body(resource);
    }
}
