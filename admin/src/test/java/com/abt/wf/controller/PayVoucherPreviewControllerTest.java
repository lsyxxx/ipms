package com.abt.wf.controller;

import com.abt.common.util.TokenUtil;
import com.abt.finance.service.InvoiceService;
import com.abt.sys.exception.GlobalExceptionHandler;
import com.abt.sys.model.dto.UserView;
import com.abt.wf.entity.PayVoucher;
import com.abt.wf.service.CostDetailService;
import com.abt.wf.service.PayVoucherService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** 验证支付预览在计算流程条件之前拒绝缺失业务归属的请求。 */
class PayVoucherPreviewControllerTest {
    private final PayVoucherService service = mock(PayVoucherService.class);
    private LocalValidatorFactoryBean validator;
    private MockMvc mvc;

    @BeforeEach
    void initialize() {
        validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mvc = MockMvcBuilders.standaloneSetup(new PayVoucherController(service,
                        mock(CostDetailService.class), mock(InvoiceService.class)))
                .setValidator(validator).setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @AfterEach
    void close() {
        validator.close();
    }

    @Test
    void missingNullAndBlankCompanyReturnChineseValidationError() throws Exception {
        for (String company : List.of("", ",\"company\":null", ",\"company\":\"\"", ",\"company\":\" \"")) {
            mvc.perform(post("/wf/pay/preview").contentType(MediaType.APPLICATION_JSON)
                            .content("{\"payAmount\":740" + company + "}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.msg").value("请选择业务归属"));
        }
        verifyNoInteractions(service);
    }

    @Test
    void selectedCompanyAndAmountReachPreviewWithAuthenticatedUser() throws Exception {
        when(service.preview(any())).thenReturn(List.of());
        UserView user = mock(UserView.class);
        when(user.getId()).thenReturn("user1");
        when(user.getName()).thenReturn("张三");
        try (var token = mockStatic(TokenUtil.class)) {
            token.when(TokenUtil::getUserFromAuthToken).thenReturn(user);
            for (String company : List.of("ABT", "QT")) {
                mvc.perform(post("/wf/pay/preview").contentType(MediaType.APPLICATION_JSON)
                                .content("{\"payAmount\":740.25,\"company\":\"" + company + "\"}"))
                        .andExpect(status().isOk()).andExpect(jsonPath("$.data").isArray());
            }
        }
        ArgumentCaptor<PayVoucher> forms = ArgumentCaptor.forClass(PayVoucher.class);
        verify(service, times(2)).preview(forms.capture());
        assertEquals(List.of("ABT", "QT"), forms.getAllValues().stream().map(PayVoucher::getCompany).toList());
        for (PayVoucher form : forms.getAllValues()) {
            assertEquals("user1", form.getSubmitUserid());
            assertEquals("740.25", form.getPayAmount().toPlainString());
        }
    }
}
