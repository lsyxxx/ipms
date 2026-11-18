package com.abt.wf.service.impl;

import com.abt.market.repository.SaleAgreementRepository;
import com.abt.wf.entity.InvoiceApply;
import com.abt.wf.model.ContractInvoiceVO;
import com.abt.wf.repository.InvoiceApplyRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 合同开票记录含税信息与原返回字段兼容性验证。
 */
@ExtendWith(MockitoExtension.class)
class ContractInvoiceTest {
    @Mock
    private InvoiceApplyRepository invoiceApplyRepository;
    @Mock
    private SaleAgreementRepository saleAgreementRepository;
    @InjectMocks
    private InvoiceApplyServiceImpl service;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void returnsTaxFlagAndPreservesExistingJsonFields(Boolean includeTax) {
        InvoiceApply invoice = new InvoiceApply();
        invoice.setId("INV001");
        invoice.setContractNo("HT001");
        invoice.setInvoiceAmount(1000);
        invoice.setBusinessState("已通过");
        invoice.setCreateDate(LocalDateTime.of(2026, 11, 18, 10, 0));
        invoice.setIssueDate(invoice.getCreateDate());
        when(invoiceApplyRepository.findRunningOrPassByContractNo("HT001")).thenReturn(List.of(invoice));
        when(saleAgreementRepository.findIncludeTaxByContractNo("HT001")).thenReturn(includeTax);

        List<ContractInvoiceVO> result = service.findInvoicesByContractNo("HT001");

        assertEquals(1, result.size());
        assertEquals(includeTax, result.get(0).getIncludeTax());
        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        JsonNode original = mapper.valueToTree(invoice);
        JsonNode actual = mapper.valueToTree(result.get(0));
        original.fields().forEachRemaining(field ->
                assertEquals(field.getValue(), actual.get(field.getKey()), field.getKey()));
        assertEquals(original.size() + 1, actual.size());
        assertTrue(actual.has("includeTax"));
        assertEquals(mapper.valueToTree(includeTax), actual.get("includeTax"));
        verify(saleAgreementRepository).findIncludeTaxByContractNo("HT001");
    }

    @Test
    void returnsEmptyListWithoutLookingUpContractWhenNoInvoicesExist() {
        when(invoiceApplyRepository.findRunningOrPassByContractNo("HT001")).thenReturn(List.of());

        assertTrue(service.findInvoicesByContractNo("HT001").isEmpty());

        verifyNoInteractions(saleAgreementRepository);
    }
}
