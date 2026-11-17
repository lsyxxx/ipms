package com.abt.testing.controller;

import com.abt.sys.exception.GlobalExceptionHandler;
import com.abt.testing.model.ReportCoverInfoVO;
import com.abt.testing.service.ReportCoverInfoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** 验证维护接口的校验分组、字段长度和物理删除路由。 */
class ReportCoverInfoControllerTest {
    private final ReportCoverInfoService service = mock(ReportCoverInfoService.class);
    private LocalValidatorFactoryBean validator;
    private MockMvc mvc;

    @BeforeEach
    void setup() {
        validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mvc = MockMvcBuilders.standaloneSetup(new ReportCoverInfoController(service))
                .setValidator(validator).setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @AfterEach
    void cleanup() {
        validator.close();
    }

    @Test
    void saveRejectsBlankNameClientIdAndBlankListItems() throws Exception {
        for (String body : new String[]{"{\"coverName\":\" \"}",
                "{\"id\":\"client-id\",\"coverName\":\"封面\"}",
                "{\"coverName\":\"封面\",\"checkModuleIds\":[\"\"]}",
                "{\"coverName\":\"封面\",\"instruments\":[\" \"]}",
                "{\"coverName\":\"封面\",\"status\":2}"}) {
            mvc.perform(post("/report-cover-info/save").contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest());
        }
        verifyNoInteractions(service);
    }

    @Test
    void updateRequiresIdAndEnforcesFieldLengths() throws Exception {
        mvc.perform(post("/report-cover-info/update").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"coverName\":\"封面\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/report-cover-info/update").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"cover\",\"coverName\":\"封面\",\"itemCode\":\"" + "A".repeat(65) + "\"}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test
    void saveReturnsVoInExistingResponseWrapper() throws Exception {
        ReportCoverInfoVO view = new ReportCoverInfoVO();
        view.setId("cover");
        view.setCoverName("封面");
        view.setItemCode("ABC");
        when(service.save(any())).thenReturn(view);
        mvc.perform(post("/report-cover-info/save").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"coverName\":\"封面\",\"itemCode\":\"ABC\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.itemCode").value("ABC"));
    }

    @Test
    void exposesPhysicalDeleteAsDeleteRequest() throws Exception {
        mvc.perform(delete("/report-cover-info/delete").param("id", "cover"))
                .andExpect(status().isOk());
        verify(service).delete("cover");
    }
}
