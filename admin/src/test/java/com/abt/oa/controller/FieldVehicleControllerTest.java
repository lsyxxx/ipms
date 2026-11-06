package com.abt.oa.controller;

import com.abt.oa.model.*;
import com.abt.oa.service.FieldVehicleService;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** 独立 MVC 测试，不启动应用、不访问业务数据库。 */
class FieldVehicleControllerTest {
    private final FieldVehicleService service = mock(FieldVehicleService.class);
    private LocalValidatorFactoryBean validator;
    private MockMvc mvc;

    @BeforeEach
    void setup() {
        validator = new LocalValidatorFactoryBean(); validator.afterPropertiesSet();
        mvc = MockMvcBuilders.standaloneSetup(new FieldVehicleController(service)).setValidator(validator)
                .setControllerAdvice(new FieldVehicleParameterHandler()).build();
    }

    @AfterEach
    void close() { validator.close(); }

    @Test
    void vehicleQueryBindsEnabledFilterAndReturnsOccupation() throws Exception {
        when(service.findList(any())).thenReturn(java.util.List.of(
                new FieldVehicleCarOptionVO("CAR", "陕A001", null, true, "1")));
        mvc.perform(get("/field-vehicle/car/findList").param("isAvtive", "0").param("query", "陕A"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data[0].isUse").value("1"));
        verify(service).findList(argThat(form -> "0".equals(form.getIsAvtive()) && "陕A".equals(form.getQuery())));
        mvc.perform(get("/field-vehicle/car/findList")).andExpect(status().isOk());
        verify(service).findList(argThat(form -> "1".equals(form.getIsAvtive())));
        mvc.perform(get("/field-vehicle/car/findList").param("isAvtive", "2"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/field-vehicle/car/findList").param("isAvtive", "true"))
                .andExpect(status().isBadRequest());
        verify(service, times(2)).findList(any());
    }

    @Test
    void emptyDraftPassesButEmptyOfficialRequestFails() throws Exception {
        mvc.perform(post("/field-vehicle/draft/save").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isOk());
        verify(service).saveDraft(any());
        mvc.perform(post("/field-vehicle/save").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
        verify(service, never()).save(any());
    }

    @Test
    void draftDeletionUsesValidatedJsonId() throws Exception {
        mvc.perform(post("/field-vehicle/delete").contentType(MediaType.APPLICATION_JSON).content("{\"id\":\"FV\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.msg").value("删除成功"));
        verify(service).delete(argThat(form -> "FV".equals(form.getId())));
        mvc.perform(post("/field-vehicle/delete").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/field-vehicle/delete").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"" + "x".repeat(51) + "\"}"))
                .andExpect(status().isBadRequest());
        verify(service, times(1)).delete(any());
    }

    @Test
    void nestedDriverNameUsesApplyValidationAndPeersRemainOptional() throws Exception {
        String body = """
                {"carId":"C","drivers":"张三","purpose":"采样",
                 "departurePlace":"公司","destination":"现场","useDate":"2026-11-05"}
                """;
        mvc.perform(post("/field-vehicle/save").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());
        verify(service).save(argThat(form -> form.getPeers() == null && form.getExpectedReturnDate() == null));
        mvc.perform(post("/field-vehicle/save").contentType(MediaType.APPLICATION_JSON).content(body.replace("张三", "  ")))
                .andExpect(status().isBadRequest());
        verify(service, times(1)).save(any());
    }

    @Test
    void fuelAllowsNoDateButRequiresPositiveLitresAndBoundsRemark() throws Exception {
        String body = "{\"mainId\":\"M\",\"amount\":100.00,\"fuelQuantity\":15.500}";
        mvc.perform(post("/field-vehicle/fuel/save").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());
        mvc.perform(post("/field-vehicle/fuel/save").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mainId\":\"M\",\"amount\":100.00}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/field-vehicle/fuel/save").contentType(MediaType.APPLICATION_JSON)
                        .content(body.replace("15.500", "0")))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/field-vehicle/fuel/save").contentType(MediaType.APPLICATION_JSON)
                        .content(body.substring(0, body.length() - 1) + ",\"remark\":\"" + "长".repeat(1001) + "\"}"))
                .andExpect(status().isBadRequest());
        verify(service, times(1)).saveFuel(any());
    }

    @Test
    void invalidDateFormatReturnsChineseParameterError() throws Exception {
        mvc.perform(post("/field-vehicle/return/update").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"M\",\"endDate\":\"bad-date\",\"endMileage\":100}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.msg").value("请求格式错误，请检查日期、数值或JSON格式"));
        verifyNoInteractions(service);
    }
}
