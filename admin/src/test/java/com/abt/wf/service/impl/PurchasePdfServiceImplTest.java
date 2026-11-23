package com.abt.wf.service.impl;

import com.abt.common.util.TokenUtil;
import com.abt.sys.exception.BusinessException;
import com.abt.wf.entity.PurchaseApplyDetail;
import com.abt.wf.entity.PurchaseApplyMain;
import com.abt.wf.repository.PurchaseApplyMainRepository;
import com.abt.wf.repository.PurchasePdfSignatureRepository;
import com.abt.wf.model.PurchasePdfSignatureFile;
import com.abt.wf.service.SignatureService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** 使用真实模板与嵌入字体验证新版 PDF，不依赖 Aspose、数据库或运行中的服务器。 */
class PurchasePdfServiceImplTest {
    @TempDir Path temporary;
    private final PurchaseApplyMainRepository repository = mock(PurchaseApplyMainRepository.class);
    private final PurchasePdfSignatureRepository signatureRepository = mock(PurchasePdfSignatureRepository.class);
    private final SignatureService signatures = mock(SignatureService.class);

    private PurchasePdfServiceImpl renderer() {
        Path templates = Files.isDirectory(Path.of("..", "template")) ? Path.of("..", "template") : Path.of("template");
        return new PurchasePdfServiceImpl(repository, signatures, signatureRepository,
                new ClassPathResource("fonts/purchase/NotoSerifSC-Regular.ttf"),
                templates.resolve("purchase_apply_template.xlsx").toString(),
                templates.resolve("purchase_accept_template.xlsx").toString());
    }

    @Test
    void generatesPreviewUsingActualTemplates() throws Exception {
        Path preview = Path.of("target", "purchase-pdf-preview", "purchase-document.pdf");
        renderer().createDocumentPdf(sample(), preview);
        try (PDDocument document = PDDocument.load(preview.toFile())) {
            assertEquals(2, document.getNumberOfPages());
        }
    }

    @Test
    void fetchesOneDeduplicatedSignatureBatchForOneHundredDocuments() {
        List<PurchaseApplyMain> mains = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            PurchaseApplyMain main = sample();
            main.setCreateUserid("creator" + i);
            main.setManagerUserid("manager" + i); main.setManagerCheckDate(main.getCreateDate());
            main.setLeaderUserid("leader" + i); main.setLeaderCheckDate(main.getCreateDate());
            main.setPurchaser("purchaser" + i); main.setPurchaserCheckDate(main.getCreateDate());
            main.setCeo("ceo" + i); main.setCeoCheckDate(main.getCreateDate());
            main.setChief("chief" + i); main.setChiefCheckDate(main.getCreateDate());
            mains.add(main);
        }
        renderer().findSignatures(mains);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<String>> ids = ArgumentCaptor.forClass(List.class);
        verify(signatureRepository, times(1)).findList(ids.capture());
        assertEquals(600, ids.getValue().size());
        assertEquals(600, new HashSet<>(ids.getValue()).size());
        verifyNoInteractions(signatures);
    }

    @Test
    void skipsEmptyBatchesAndChunksLargeSignatureQueriesBelowSqlServerParameterLimit() {
        PurchaseApplyMain main = sample();
        main.setCreateUserid(" "); main.setPurchaser(""); main.setManagerUserid("not-approved");
        renderer().findSignatures(List.of(main));
        verifyNoInteractions(signatureRepository);
        List<PurchaseApplyMain> mains = new ArrayList<>();
        for (int i = 0; i < 1001; i++) {
            PurchaseApplyMain item = new PurchaseApplyMain(); item.setCreateUserid("user" + i); mains.add(item);
        }
        renderer().findSignatures(mains);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<String>> ids = ArgumentCaptor.forClass(List.class);
        verify(signatureRepository, times(2)).findList(ids.capture());
        assertEquals(List.of(1000, 1), ids.getAllValues().stream().map(List::size).toList());
    }

    @Test
    void deduplicatesSharedUsersAcrossDocumentsAndIncludesAcceptancePurchaserWithoutApprovalDate() {
        PurchaseApplyMain main = sample();
        main.setCreateUserid("shared"); main.setManagerUserid("shared");
        main.setManagerCheckDate(main.getCreateDate());
        main.setPurchaser("purchaser");
        main.setLeaderUserid("not-approved");
        renderer().findSignatures(List.of(main, main));
        verify(signatureRepository).findList(List.of("shared", "purchaser"));
        verifyNoMoreInteractions(signatureRepository);
    }

    @Test
    void alsoKeepsRightmostFieldsOfSevenColumnTemplate() throws Exception {
        Path templates = Files.isDirectory(Path.of("admin", "template"))
                ? Path.of("admin", "template") : Path.of("template");
        var sevenColumnRenderer = new PurchasePdfServiceImpl(repository, signatures, signatureRepository,
                new ClassPathResource("fonts/purchase/NotoSerifSC-Regular.ttf"),
                templates.resolve("purchase_apply_template.xlsx").toString(),
                templates.resolve("purchase_accept_template.xlsx").toString());
        Path target = temporary.resolve("seven-column.pdf");
        sevenColumnRenderer.createDocumentPdf(sample(), target);
        try (PDDocument document = PDDocument.load(target.toFile())) {
            String application = pageText(document, 1).replaceAll("\\s", "");
            assertTrue(application.contains("用途"));
            assertTrue(application.contains("实验室日常检测"));
            assertTrue(application.contains("740"));
            assertTrue(application.contains("ABT-QR-49:2021"));
        }
    }

    @Test
    void writesApplicationAndAcceptanceInOneDocumentAndLeavesNoIntermediateFiles() throws Exception {
        PurchaseApplyMain main = sample();
        main.setDetails(List.of(detail("后项", 2), detail("前项", 1)));
        Path target = temporary.resolve("document.pdf");
        renderer().createDocumentPdf(main, target);
        try (PDDocument document = PDDocument.load(target.toFile())) {
            assertEquals(2, document.getNumberOfPages());
            assertEquals(PDRectangle.A4.getWidth(), document.getPage(0).getMediaBox().getWidth());
            assertEquals(PDRectangle.A4.getHeight(), document.getPage(1).getMediaBox().getWidth());
            String application = pageText(document, 1);
            assertTrue(application.contains("采购申请单"));
            assertFalse(application.contains("供应品验收记录"));
            assertTrue(application.indexOf("前项") < application.indexOf("后项"));
            String acceptance = pageText(document, 2);
            assertTrue(acceptance.contains("供应品验收记录"));
            assertTrue(acceptance.contains("合格"));
            assertFalse(new PDFTextStripper().getText(document).contains("Evaluation"));
            assertFalse(new PDFTextStripper().getText(document).contains("{."));
            for (var page : document.getPages()) {
                for (var name : page.getResources().getFontNames()) {
                    assertTrue(page.getResources().getFont(name).isEmbedded());
                }
            }
        }
        assertEquals("后项", main.getDetails().get(0).getName(), "不得修改原实体集合的排序");
        try (var files = Files.list(temporary)) { assertEquals(List.of(target), files.toList()); }
    }

    @Test
    void onlyAppendsAcceptanceWhenBothAcceptedAndFinished() throws Exception {
        for (boolean accepted : new boolean[]{false, true}) {
            for (boolean finished : new boolean[]{false, true}) {
                PurchaseApplyMain main = sample();
                main.setAccepted(accepted); main.setFinished(finished);
                Path target = temporary.resolve(accepted + "-" + finished + ".pdf");
                renderer().createDocumentPdf(main, target);
                try (PDDocument pdf = PDDocument.load(target.toFile())) {
                    assertEquals(accepted && finished ? 2 : 1, pdf.getNumberOfPages());
                }
            }
        }
    }

    @Test
    void handlesEmptyDetailsAndSplitsVeryLongRowsWithoutLosingText() throws Exception {
        PurchaseApplyMain empty = sample(); empty.setDetails(List.of());
        renderer().createDocumentPdf(empty, temporary.resolve("empty.pdf"));
        try (PDDocument pdf = PDDocument.load(temporary.resolve("empty.pdf").toFile())) {
            assertEquals(2, pdf.getNumberOfPages());
            assertFalse(new PDFTextStripper().getText(pdf).contains("{."));
        }
        PurchaseApplyMain main = sample();
        PurchaseApplyDetail huge = detail("超长明细", 0);
        huge.setUsage("长内容".repeat(400) + "采购尾标");
        huge.setAcceptRemark("长备注".repeat(400) + "验收尾标");
        main.getDetails().add(huge);
        for (int i = 1; i <= 65; i++) main.getDetails().add(detail("物品" + i, i));
        Path target = temporary.resolve("long.pdf");
        renderer().createDocumentPdf(main, target);
        Path preview = Path.of("target", "purchase-pdf-preview", "long-document.pdf");
        Files.createDirectories(preview.getParent());
        Files.copy(target, preview, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        try (PDDocument pdf = PDDocument.load(target.toFile())) {
            assertTrue(pdf.getNumberOfPages() > 4);
            String text = new PDFTextStripper().getText(pdf).replaceAll("\\s", "");
            assertTrue(text.contains("采购尾标"));
            assertTrue(text.contains("验收尾标"));
            assertTrue(text.contains("物品65"));
            assertTrue(pageText(pdf, pdf.getNumberOfPages()).contains("物品65"), "签名尾页应保留最后一条明细");
            new PDFTextStripper() {
                @Override
                protected void processTextPosition(TextPosition text) {
                    PDRectangle size = pdf.getPage(getCurrentPageNo() - 1).getMediaBox();
                    assertTrue(text.getXDirAdj() >= 49, "文字越过左边距");
                    assertTrue(text.getXDirAdj() + text.getWidthDirAdj() < size.getWidth() - 49,
                            "文字越过右边距");
                    assertTrue(text.getYDirAdj() < size.getHeight() - 50, "文字越过底边距");
                    super.processTextPosition(text);
                }
            }.getText(pdf);
            boolean acceptanceStarted = false;
            for (int i = 0; i < pdf.getNumberOfPages(); i++) {
                String page = pageText(pdf, i + 1);
                if (page.contains("供应品验收记录")) acceptanceStarted = true;
                assertTrue(page.contains(acceptanceStarted ? "供应品验收记录" : "采购申请单"));
                assertEquals(acceptanceStarted ? PDRectangle.A4.getHeight() : PDRectangle.A4.getWidth(),
                        pdf.getPage(i).getMediaBox().getWidth());
            }
        }
    }

    @Test
    void includesSignatureImagesWithApprovalDateRulesAndMissingFileFallback() throws Exception {
        BufferedImage signature = new BufferedImage(180, 45, BufferedImage.TYPE_INT_RGB);
        var graphics = signature.createGraphics();
        graphics.setColor(Color.WHITE); graphics.fillRect(0, 0, 180, 45);
        graphics.setColor(Color.BLACK); graphics.drawLine(10, 30, 160, 10); graphics.dispose();
        ImageIO.write(signature, "png", temporary.resolve("signature.png").toFile());
        when(signatures.getSignatureDir()).thenReturn(temporary.toString());
        when(signatureRepository.findList(anyList())).thenReturn(List.of(
                new PurchasePdfSignatureFile("creator", "signature.png"),
                new PurchasePdfSignatureFile("manager", "absent.png")));
        PurchaseApplyMain main = sample();
        main.setCreateUserid("creator"); main.setManagerUserid("manager");
        main.setManagerCheckDate(LocalDateTime.of(2026, 11, 3, 12, 0));
        main.setLeaderUserid("not-approved");
        Path target = temporary.resolve("signature.pdf");
        renderer().createDocumentPdf(main, target);
        try (PDDocument pdf = PDDocument.load(target.toFile())) {
            for (var page : pdf.getPages()) {
                boolean imageFound = false;
                for (var name : page.getResources().getXObjectNames()) {
                    imageFound |= page.getResources().getXObject(name) instanceof PDImageXObject;
                }
                assertTrue(imageFound);
            }
        }
        verify(signatureRepository).findList(List.of("creator", "manager"));
        verify(signatures, never()).getSignatureByUserid(anyString());
    }

    @Test
    void singleExportsValidateStateAndUseOnlyNewRenderer() throws Exception {
        PurchaseApplyMain main = sample();
        when(repository.findByIdWithDetails("id")).thenReturn(main);
        try (var token = mockStatic(TokenUtil.class)) {
            token.when(TokenUtil::getUseridFromAuthToken).thenReturn("owner");
            try (PDDocument application = PDDocument.load(renderer().createPdf("id"));
                 PDDocument acceptance = PDDocument.load(renderer().createAcceptPdf("id"))) {
                assertEquals(1, application.getNumberOfPages());
                assertEquals(1, acceptance.getNumberOfPages());
                assertTrue(pageText(acceptance, 1).contains("供应品验收记录"));
            }
            main.setAccepted(false);
            assertThrows(BusinessException.class, () -> renderer().createAcceptPdf("id"));
            main.setAccepted(true); main.setFinished(false);
            assertThrows(BusinessException.class, () -> renderer().createAcceptPdf("id"));
            assertThrows(BusinessException.class, () -> renderer().createPdf("unknown"));
        }
    }

    @Test
    void reportsMissingFontAndTemplateClearly() {
        var missing = new PurchasePdfServiceImpl(repository, signatures, signatureRepository,
                new FileSystemResource(temporary.resolve("absent.ttf")), "absent.xlsx", "absent.xlsx");
        BusinessException error = assertThrows(BusinessException.class,
                () -> missing.createDocumentPdf(sample(), temporary.resolve("missing.pdf")));
        assertTrue(error.getMessage().contains("字体"));
        var missingTemplate = new PurchasePdfServiceImpl(repository, signatures, signatureRepository,
                new ClassPathResource("fonts/purchase/NotoSerifSC-Regular.ttf"), "absent.xlsx", "absent.xlsx");
        assertTrue(assertThrows(BusinessException.class,
                () -> missingTemplate.createDocumentPdf(sample(), temporary.resolve("missing-template.pdf")))
                .getMessage().contains("模板"));
    }

    @Test
    void savesApplicationOnlyAsUniquePaymentAndReimbursementAttachments() throws Exception {
        PurchaseApplyMain main = sample();
        when(repository.findByIdWithDetails(main.getId())).thenReturn(main);
        Path directory = temporary.resolve("attachments");
        try (var token = mockStatic(TokenUtil.class)) {
            token.when(TokenUtil::getUseridFromAuthToken).thenReturn("owner");
            Path payment = renderer().createPdf(main.getId(), directory);
            Path reimbursement = renderer().createPdf(main.getId(), directory);
            assertTrue(payment.isAbsolute());
            assertEquals(directory.toAbsolutePath(), payment.getParent());
            assertNotEquals(payment, reimbursement, "两次附件生成不得覆盖同一文件");
            for (Path file : List.of(payment, reimbursement)) {
                assertTrue(file.getFileName().toString().endsWith(".pdf"));
                try (PDDocument pdf = PDDocument.load(file.toFile())) {
                    assertEquals(1, pdf.getNumberOfPages(), "已验收单据的附件仍只含采购申请单");
                    String text = new PDFTextStripper().getText(pdf);
                    assertTrue(text.contains("采购申请单"));
                    assertFalse(text.contains("供应品验收记录"));
                    assertFalse(text.contains("Evaluation"));
                }
            }
            try (var files = Files.list(directory)) { assertEquals(2, files.count()); }
        }
    }

    @Test
    void attachmentGenerationFailureDoesNotLeaveAnEmptyFile() {
        Path directory = temporary.resolve("attachments");
        try (var token = mockStatic(TokenUtil.class)) {
            token.when(TokenUtil::getUseridFromAuthToken).thenReturn("owner");
            assertThrows(BusinessException.class, () -> renderer().createPdf("missing", directory));
            assertFalse(Files.exists(directory));
        }
    }

    private String pageText(PDDocument pdf, int page) throws Exception {
        PDFTextStripper stripper = new PDFTextStripper();
        stripper.setStartPage(page); stripper.setEndPage(page);
        return stripper.getText(pdf);
    }

    static PurchaseApplyMain sample() {
        PurchaseApplyMain main = new PurchaseApplyMain();
        main.setId("DEMO202611200001"); main.setAccepted(true); main.setFinished(true);
        main.setCreateDate(LocalDateTime.of(2026, 11, 2, 9, 0));
        main.setAcceptDate(LocalDateTime.of(2026, 11, 6, 9, 0));
        main.setCost(new BigDecimal("740.00"));
        main.getDetails().add(detail("移液器吸头", 1));
        main.getDetails().add(detail("样品瓶", 2));
        main.getDetails().add(detail("一次性手套", 3));
        main.getDetails().get(1).setSpecification("500mL，玻璃");
        main.getDetails().get(1).setCurrentQuantity(20d); main.getDetails().get(1).setUnit("个");
        main.getDetails().get(1).setPrice(new BigDecimal("12.50")); main.getDetails().get(1).setCost(new BigDecimal("250.00"));
        main.getDetails().get(2).setSpecification("丁腈，M码，100只/盒");
        main.getDetails().get(2).setCurrentQuantity(5d); main.getDetails().get(2).setUnit("盒");
        main.getDetails().get(2).setPrice(new BigDecimal("28.00")); main.getDetails().get(2).setCost(new BigDecimal("140.00"));
        return main;
    }

    private static PurchaseApplyDetail detail(String name, int order) {
        PurchaseApplyDetail detail = new PurchaseApplyDetail();
        detail.setSortNo(order); detail.setName(name); detail.setSpecification("200μL，1000支/袋");
        detail.setCurrentQuantity(10d); detail.setUnit("袋");
        detail.setPrice(new BigDecimal("35.00")); detail.setCost(new BigDecimal("350.00"));
        detail.setUsage("实验室日常检测"); detail.setAcceptItems("品名、规格、等级、生产日期、有效期、成分、包装、外观、贮存、数量、合格证明、说明书、保修卡");
        detail.setAcceptResult("合格");
        return detail;
    }
}
