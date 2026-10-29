package com.abt.testing.service.impl;

import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.dto.UserView;
import com.abt.sys.model.entity.CustomerInfo;
import com.abt.sys.repository.CustomerInfoRepository;
import com.abt.sys.service.CompanyService;
import com.abt.testing.entity.Agreement;
import com.abt.testing.entity.EnumLib;
import com.abt.testing.repository.AgreementRepository;
import com.abt.testing.repository.EntrustRepository;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** 验证精确匹配、数据库字节长度和整批校验，不启动应用或修改数据库。 */
class PreAgreementImportServiceImplTest {
    private AgreementRepository repository;
    private CustomerInfoRepository customers;
    private PreAgreementImportServiceImpl service;
    private AgreementServiceImpl agreementService;
    private final CustomerInfo client = new CustomerInfo().setId("client").setCustomerName("甲方全称");
    private final CustomerInfo abt = new CustomerInfo().setId("abt").setCustomerName("西安阿伯塔资环分析测试技术有限公司");
    private final CustomerInfo grd = new CustomerInfo().setId("grd").setCustomerName("西安吉瑞达地质科技有限公司");
    private final CustomerInfo dc = new CustomerInfo().setId("dc").setCustomerName("陕西道常能源技术有限公司");

    @BeforeEach
    void setup() {
        repository = mock(AgreementRepository.class);
        customers = mock(CustomerInfoRepository.class);
        CompanyService companies = mock(CompanyService.class);
        when(customers.findAllByIsAvtiveIs("1")).thenReturn(List.of(client));
        when(companies.findYCompanyList()).thenReturn(List.of(abt, grd, dc));
        when(repository.findByAgreementCodeIn(anyCollection())).thenReturn(List.of());
        EnumLib type = new EnumLib();
        type.setFid("1");
        type.setFname("检验检测服务类");
        agreementService = new AgreementServiceImpl(repository, abt, grd, dc,
                mock(EntrustRepository.class), Map.of(), Map.of("1", type), List.of(type));
        service = new PreAgreementImportServiceImpl(repository, customers, companies, agreementService, List.of(type));
        UserView user = new UserView().setId("test-user").setName("测试用户").setAccount("tester");
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, ""));
    }

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void savesAllThreeCompaniesWithIdsAndDefaultType() throws Exception {
        var result = service.save(excel(new String[][]{
                {" ajc2026001 ", "检验合同", client.getCustomerName(), abt.getCustomerName()},
                {"JC2026001", "检测合同", client.getCustomerName(), grd.getCustomerName()},
                {"任意编号01", "道常合同", client.getCustomerName(), dc.getCustomerName()}}));
        assertTrue(result.getErrors().isEmpty());
        assertEquals(3, result.getSavedCount());
        ArgumentCaptor<Iterable<Agreement>> captor = ArgumentCaptor.forClass((Class) Iterable.class);
        verify(repository).saveAllAndFlush(captor.capture());
        List<Agreement> saved = (List<Agreement>) captor.getValue();
        assertEquals("AJC2026001", saved.get(0).getAgreementCode());
        assertEquals("dc", saved.get(2).getYCompanyId());
        assertTrue(saved.stream().allMatch(a -> "1".equals(a.getAgreementType())
                && "client".equals(a.getJCompanyId()) && "1".equals(a.getIsFinish())));
    }

    @Test
    void reportsAllFieldsAtPhysicalExcelRowsAndDoesNotSaveValidRows() throws Exception {
        var result = service.save(excel(new String[][]{
                {"AJC2026001", "有效合同", client.getCustomerName(), abt.getCustomerName()},
                null,
                {"", "", client.getCustomerName() + " ", "阿伯塔"}}));
        assertEquals(2, result.getTotalCount());
        assertEquals(0, result.getSavedCount());
        assertEquals(4, result.getErrors().size());
        assertTrue(result.getErrors().stream().allMatch(e -> e.getRowNumber() == 4));
        assertEquals(List.of("合同编号", "合同名称", "甲方单位", "乙方单位"),
                result.getErrors().stream().map(e -> e.getField()).toList());
        verify(repository, never()).saveAllAndFlush(any());
    }

    @Test
    void enforcesVarcharBytesAndRejectsUnrepresentableCharacters() throws Exception {
        var result = service.save(excel(new String[][]{
                {"AJC2026001", "中".repeat(101), client.getCustomerName(), abt.getCustomerName()},
                {"AJC2026002", "合同😀", client.getCustomerName(), abt.getCustomerName()}}));
        assertEquals(2, result.getErrors().size());
        assertTrue(result.getErrors().stream().allMatch(e -> "合同名称".equals(e.getField())));
        verify(repository, never()).saveAllAndFlush(any());
    }

    @Test
    void rejectsDuplicateNumbersInFileAndDatabase() throws Exception {
        Agreement existing = new Agreement();
        existing.setAgreementCode("AJC2026001");
        when(repository.findByAgreementCodeIn(anyCollection())).thenReturn(List.of(existing));
        var result = service.save(excel(new String[][]{
                {"ajc2026001", "合同1", client.getCustomerName(), abt.getCustomerName()},
                {"AJC2026001", "合同2", client.getCustomerName(), abt.getCustomerName()}}));
        assertEquals(4, result.getErrors().size());
        verify(repository, never()).saveAllAndFlush(any());
    }

    @Test
    void rejectsAmbiguousClientAndInvalidHeader() throws Exception {
        when(customers.findAllByIsAvtiveIs("1")).thenReturn(List.of(client,
                new CustomerInfo().setId("other").setCustomerName(client.getCustomerName())));
        var result = service.save(excel(new String[][]{
                {"AJC2026001", "合同", client.getCustomerName(), abt.getCustomerName()}}));
        assertEquals("甲方单位", result.getErrors().get(0).getField());
        assertTrue(result.getErrors().get(0).getMessage().contains("同名"));
        try (var workbook = new XSSFWorkbook()) {
            workbook.createSheet().createRow(0).createCell(0).setCellValue("错误表头");
            var output = new ByteArrayOutputStream();
            workbook.write(output);
            var headers = service.save(new MockMultipartFile("file", "test.xlsx", "", output.toByteArray()));
            assertEquals(4, headers.getErrors().size());
            assertTrue(headers.getErrors().stream().allMatch(e -> e.getRowNumber() == 1));
        }
        verify(repository, never()).saveAllAndFlush(any());
    }

    @Test
    void templateContainsTextColumnsAndThreeCompanyDropdown() throws Exception {
        var output = new ByteArrayOutputStream();
        service.findTemplate(output);
        try (var workbook = new XSSFWorkbook(new ByteArrayInputStream(output.toByteArray()))) {
            var sheet = workbook.getSheetAt(0);
            assertEquals("乙方单位", sheet.getRow(0).getCell(3).getStringCellValue());
            assertEquals("@", sheet.getColumnStyle(0).getDataFormatString());
            assertEquals(3, sheet.getDataValidations().get(0).getValidationConstraint().getExplicitListValues().length);
        }
    }

    @Test
    void retainsExistingNumberRulesAndAllowsDcWithoutInventedPrefix() {
        assertDoesNotThrow(() -> agreementService.validatePreAgreementCode("任意编号", "dc"));
        assertThrows(BusinessException.class, () -> agreementService.validatePreAgreementCode("", "dc"));
        assertThrows(BusinessException.class, () -> agreementService.validatePreAgreementCode("WRONG", "abt"));
        assertThrows(BusinessException.class, () -> agreementService.validatePreAgreementCode("WRONG", "grd"));
        assertThrows(BusinessException.class, () -> agreementService.validatePreAgreementCode("ABC", "unknown"));
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void splitsDuplicateLookupsBelowSqlServerParameterLimit() throws Exception {
        String[][] rows = new String[501][];
        for (int i = 0; i < rows.length; i++) {
            rows[i] = new String[]{"D" + i, "合同", client.getCustomerName(), dc.getCustomerName()};
        }
        var result = service.save(excel(rows));
        assertEquals(501, result.getSavedCount());
        ArgumentCaptor<java.util.Collection<String>> batches = ArgumentCaptor.forClass((Class) java.util.Collection.class);
        verify(repository, times(2)).findByAgreementCodeIn(batches.capture());
        assertEquals(List.of(500, 1), batches.getAllValues().stream().map(java.util.Collection::size).toList());
    }

    private MockMultipartFile excel(String[][] values) throws Exception {
        try (var workbook = new XSSFWorkbook()) {
            var sheet = workbook.createSheet();
            var header = sheet.createRow(0);
            String[] headers = {"合同编号", "合同名称", "甲方单位", "乙方单位"};
            for (int c = 0; c < headers.length; c++) header.createCell(c).setCellValue(headers[c]);
            for (int r = 0; r < values.length; r++) {
                if (values[r] == null) continue;
                var row = sheet.createRow(r + 1);
                for (int c = 0; c < values[r].length; c++) row.createCell(c).setCellValue(values[r][c]);
            }
            var output = new ByteArrayOutputStream();
            workbook.write(output);
            return new MockMultipartFile("file", "test.xlsx", "", output.toByteArray());
        }
    }
}
