package com.abt.wf.controller;

import com.abt.wf.service.PurchasePdfService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/** 新版采购 PDF 下载；旧版接口保持原样。 */
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/purchase/pdf")
public class PurchasePdfController {
    private final PurchasePdfService purchasePdfService;

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(@RequestParam @NotBlank(message = "采购申请编号不能为空") String id)
            throws IOException {
        return response(purchasePdfService.createPdf(id), "采购申请单.pdf");
    }

    @GetMapping("/export-accept")
    public ResponseEntity<byte[]> exportAccept(@RequestParam @NotBlank(message = "采购申请编号不能为空") String id)
            throws IOException {
        return response(purchasePdfService.createAcceptPdf(id), "采购验收单.pdf");
    }

    private ResponseEntity<byte[]> response(byte[] pdf, String name) {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(name, StandardCharsets.UTF_8).build().toString())
                .body(pdf);
    }
}
