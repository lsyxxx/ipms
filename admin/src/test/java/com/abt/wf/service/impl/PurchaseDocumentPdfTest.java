package com.abt.wf.service.impl;

import com.abt.common.ExcelUtil;
import com.abt.wf.entity.PurchaseApplyMain;
import com.abt.wf.entity.PurchaseApplyDetail;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** 验证实际 PDF 合并页数、页面方向、顺序以及未验收分支。 */
class PurchaseDocumentPdfTest {
    @TempDir Path temporary;
    private PurchaseServiceImpl renderer;

    @BeforeEach
    void initialize() throws Exception {
        renderer = spy(new PurchaseServiceImpl(null, null, null, null, null, null, null, null, null, null, null));
        Path template = Files.createFile(temporary.resolve("template.xlsx"));
        ReflectionTestUtils.setField(renderer, "excelTemplate", template.toString());
        ReflectionTestUtils.setField(renderer, "acceptExcelTemplate", template.toString());
        doAnswer(invocation -> Path.of(invocation.getArgument(1, String.class)).toFile()).when(renderer).doCreateExcel(any(), anyString());
        doAnswer(invocation -> Path.of(invocation.getArgument(1, String.class)).toFile()).when(renderer).createAcceptExcel(any(), anyString());
    }

    @Test
    void mergesAllPagesAndRetainsOrderAndOrientation() throws Exception {
        var main = new PurchaseApplyMain(); main.setAccepted(true); main.setFinished(true);
        try (MockedStatic<ExcelUtil> excel = convertedPdf()) {
            Path combined = renderer.createDocumentPdf(main, temporary.resolve("work").toString()).toPath();
            try (PDDocument document = PDDocument.load(combined.toFile())) {
                assertEquals(6, document.getNumberOfPages());
                assertEquals(PDRectangle.A4.getWidth(), document.getPage(0).getMediaBox().getWidth());
                assertEquals(PDRectangle.A4.getHeight(), document.getPage(3).getMediaBox().getWidth());
                assertEquals(PDRectangle.A4.getWidth(), document.getPage(3).getMediaBox().getHeight());
            }
        }
        verify(renderer).createAcceptExcel(eq(main), anyString());
    }

    @Test
    void unacceptedOrUnfinishedDocumentsHaveOnlyPurchasePages() throws Exception {
        for (boolean accepted : new boolean[]{false, true}) {
            var main = new PurchaseApplyMain(); main.setAccepted(accepted); main.setFinished(false);
            try (MockedStatic<ExcelUtil> excel = convertedPdf()) {
                Path combined = renderer.createDocumentPdf(main, temporary.resolve("work" + accepted).toString()).toPath();
                try (PDDocument document = PDDocument.load(combined.toFile())) {
                    assertEquals(3, document.getNumberOfPages());
                }
            }
        }
        verify(renderer, never()).createAcceptExcel(any(), anyString());
    }

    private MockedStatic<ExcelUtil> convertedPdf() throws Exception {
        MockedStatic<ExcelUtil> excel = mockStatic(ExcelUtil.class);
        excel.when(() -> ExcelUtil.excel2Pdf(anyString(), anyString())).thenAnswer(invocation -> {
            Path pdf = Path.of(invocation.getArgument(1, String.class));
            try (PDDocument document = new PDDocument()) {
                PDRectangle size = pdf.getFileName().toString().equals("accept.pdf")
                        ? new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()) : PDRectangle.A4;
                for (int i = 0; i < 3; i++) document.addPage(new PDPage(size));
                document.save(pdf.toFile());
            }
            return pdf.toFile();
        });
        return excel;
    }

    @Test
    void rendersExistingTemplatesAndReleasesIntermediateFiles() throws Exception {
        // 直接走项目的模板填充、Excel 转 PDF 和合并，不依赖数据库或签名服务。
        PurchaseServiceImpl real = new PurchaseServiceImpl(null, null, null, null, null, null, null, null, null, null, null);
        Path templates = Files.isDirectory(Path.of("template")) ? Path.of("template") : Path.of("..", "template");
        ReflectionTestUtils.setField(real, "excelTemplate", templates.resolve("purchase_apply_template.xlsx").toString());
        ReflectionTestUtils.setField(real, "acceptExcelTemplate", templates.resolve("purchase_accept_template.xlsx").toString());
        PurchaseApplyMain main = new PurchaseApplyMain();
        main.setAccepted(true); main.setFinished(true); main.setCreateDate(LocalDateTime.now());
        main.setAcceptDate(LocalDateTime.now()); main.setCost(new BigDecimal("7.50"));
        PurchaseApplyDetail detail = new PurchaseApplyDetail();
        detail.setName("测试样品瓶"); detail.setSpecification("100mL"); detail.setUnit("个");
        detail.setCurrentQuantity(3d); detail.setPrice(new BigDecimal("2.50")); detail.setCost(new BigDecimal("7.50"));
        detail.setUsage("导出测试"); detail.setAcceptItems("品名、规格、数量"); detail.setAcceptResult("合格");
        main.getDetails().add(detail);
        Path work = temporary.resolve("real");
        Path pdf = real.createDocumentPdf(main, work.toString()).toPath();
        try (PDDocument document = PDDocument.load(pdf.toFile())) {
            assertEquals(2, document.getNumberOfPages());
            String text = new PDFTextStripper().getText(document);
            assertTrue(text.contains("采购申请单"));
            assertTrue(text.contains("供应品验收记录"));
            assertTrue(text.contains("测试样品瓶"));
        }
        for (String file : new String[]{"purchase.xlsx", "purchase.pdf", "accept.xlsx", "accept.pdf"}) {
            Files.delete(work.resolve(file));
        }
        assertTrue(Files.isRegularFile(pdf));
    }
}
