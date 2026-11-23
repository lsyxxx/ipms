package com.abt.wf.service.impl;

import com.abt.market.repository.SettlementMainRepository;
import com.abt.wf.entity.InvoiceApply;
import com.abt.wf.model.ContractInvoiceVO;
import com.abt.wf.repository.InvoiceApplyRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 合同开票记录按对应结算单返回含税标记。
 */
@ExtendWith(MockitoExtension.class)
class ContractInvoiceTest {
    @Mock
    private InvoiceApplyRepository invoiceApplyRepository;
    @Mock
    private SettlementMainRepository settlementMainRepository;
    @InjectMocks
    private InvoiceApplyServiceImpl service;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void returnsSettlementTaxFlagAndPreservesExistingJsonFields(Boolean includeTax) {
        InvoiceApply invoice = invoice("INV001", "ST001");
        when(invoiceApplyRepository.findRunningOrPassByContractNo("HT001")).thenReturn(List.of(invoice));
        when(settlementMainRepository.findTaxFlagByIdIn(Set.of("ST001"))).thenReturn(
                includeTax == null ? List.of() : List.<Object[]>of(new Object[]{"ST001", includeTax}));

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
        verify(settlementMainRepository).findTaxFlagByIdIn(Set.of("ST001"));
    }

    @Test
    void usesEachSettlementTaxFlag() {
        InvoiceApply taxed = invoice("INV1", "ST-T");
        InvoiceApply untaxed = invoice("INV2", "ST-F");
        InvoiceApply missing = invoice("INV3", "ST-X");
        InvoiceApply unbound = invoice("INV4", " ");
        when(invoiceApplyRepository.findRunningOrPassByContractNo("HT001"))
                .thenReturn(List.of(taxed, untaxed, missing, unbound));
        when(settlementMainRepository.findTaxFlagByIdIn(Set.of("ST-T", "ST-F", "ST-X")))
                .thenReturn(List.of(new Object[]{"ST-T", true}, new Object[]{"ST-F", false}));

        List<ContractInvoiceVO> result = service.findInvoicesByContractNo("HT001");

        assertEquals(Boolean.TRUE, result.get(0).getIncludeTax());
        assertEquals(Boolean.FALSE, result.get(1).getIncludeTax());
        assertNull(result.get(2).getIncludeTax());
        assertNull(result.get(3).getIncludeTax());
    }

    @Test
    void returnsEmptyListWithoutLookingUpSettlementWhenNoInvoicesExist() {
        when(invoiceApplyRepository.findRunningOrPassByContractNo("HT001")).thenReturn(List.of());

        assertTrue(service.findInvoicesByContractNo("HT001").isEmpty());

        verifyNoInteractions(settlementMainRepository);
    }

    private InvoiceApply invoice(String id, String settlementId) {
        InvoiceApply invoice = new InvoiceApply();
        invoice.setId(id);
        invoice.setContractNo("HT001");
        invoice.setSettlementId(settlementId);
        invoice.setInvoiceAmount(1000);
        invoice.setBusinessState("已通过");
        invoice.setCreateDate(LocalDateTime.of(2026, 11, 18, 10, 0));
        invoice.setIssueDate(invoice.getCreateDate());
        return invoice;
    }
}
