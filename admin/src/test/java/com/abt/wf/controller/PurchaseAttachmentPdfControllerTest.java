package com.abt.wf.controller;

import com.abt.sys.exception.BusinessException;
import com.abt.wf.service.PurchasePdfService;
import com.abt.wf.service.PurchaseService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** 验证去支付、去报销共用接口保留原协议并调用新版生成服务。 */
class PurchaseAttachmentPdfControllerTest {
    @TempDir Path directory;
    private final PurchaseService legacy = mock(PurchaseService.class);
    private final PurchasePdfService pdfService = mock(PurchasePdfService.class);

    @Test
    void existingEndpointReturnsAttachmentPathFromNewRenderer() throws Exception {
        PurchaseController controller = new PurchaseController(legacy, pdfService);
        ReflectionTestUtils.setField(controller, "rbsPurDir", directory.toString());
        Path attachment = directory.resolve("purchase.pdf");
        when(pdfService.createPdf("PUR001", directory)).thenReturn(attachment);
        MockMvcBuilders.standaloneSetup(controller).build()
                .perform(get("/purchase/create/pdf").param("id", "PUR001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(attachment.toAbsolutePath().toString()));
        verify(pdfService).createPdf("PUR001", directory);
        verifyNoInteractions(legacy);
    }

    @Test
    void rejectsMissingOrBlankPurchaseIdBeforeGeneratingFile() {
        PurchaseController controller = new PurchaseController(legacy, pdfService);
        assertThrows(BusinessException.class, () -> controller.createPdf(null));
        assertThrows(BusinessException.class, () -> controller.createPdf(" "));
        verifyNoInteractions(legacy, pdfService);
    }
}
