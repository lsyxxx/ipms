package com.abt.market.service.impl;

import com.abt.market.entity.SettlementMain;
import com.abt.market.entity.SettlementSummary;
import com.abt.market.repository.*;
import com.abt.sys.exception.BusinessException;
import com.abt.testing.repository.EntrustRepository;
import com.abt.testing.repository.SampleRegistCheckModeuleItemRepository;
import com.abt.testing.repository.SampleRegistRepository;
import com.abt.wf.repository.InvoiceApplyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 验证保存时根据优惠比例计算金额，不依赖数据库或前端计算结果。
 */
class SettlementDiscountTest {
    private SettlementMainRepository mainRepository;
    private SettlementServiceImpl service;

    @BeforeEach
    void setUp() {
        mainRepository = mock(SettlementMainRepository.class);
        service = new SettlementServiceImpl(mainRepository, mock(ExpenseItemRepository.class),
                mock(TestItemRepository.class), mock(SettlementRelationRepository.class),
                mock(SaleAgreementRepository.class), mock(SettlementSummaryRepository.class),
                mock(EntrustRepository.class), mock(InvoiceApplyRepository.class),
                mock(SampleRegistRepository.class), mock(SampleRegistCheckModeuleItemRepository.class),
                mock(StlmSmryTempRepository.class), mock(StlmTestTempRepository.class));
        when(mainRepository.save(any(SettlementMain.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @ParameterizedTest
    @CsvSource({"1000, 0.2, 200.00, 800.00, 900.00", "1000, 0, 0.00, 1000.00, 1100.00",
            "1000, 1, 1000.00, 0.00, 100.00", "10.03, 0.5, 5.02, 5.01, 105.01"})
    void saveCalculatesDiscountFromPercentage(double gross, double percentage, String discount,
                                              String net, String total) {
        SettlementMain main = settlement(gross);
        main.setDiscountPercentage(percentage);
        service.save(main);

        assertEquals(percentage, main.getDiscountPercentage());
        assertEquals(Double.parseDouble(discount), main.getDiscountAmount());
        assertEquals(new BigDecimal(net), main.getNetTestAmount());
        assertEquals(new BigDecimal(total), main.getTotalAmount());
        verify(mainRepository).save(main);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(doubles = {0})
    void saveDefaultsMissingDiscountAndExpenseToZero(Double percentage) {
        SettlementMain main = settlement(1000);
        main.setDiscountPercentage(percentage);
        main.setExpenseAmount(null);
        service.save(main);

        assertEquals(0.0, main.getDiscountPercentage());
        assertEquals(0.0, main.getDiscountAmount());
        assertEquals(new BigDecimal("1000.00"), main.getTotalAmount());
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.1, 1.1, Double.NaN, Double.POSITIVE_INFINITY})
    void saveRejectsInvalidPercentageBeforePersistence(double percentage) {
        SettlementMain main = settlement(1000);
        main.setDiscountPercentage(percentage);

        assertThrows(BusinessException.class, () -> service.save(main));
        verify(mainRepository, never()).save(any());
    }

    private SettlementMain settlement(double gross) {
        SettlementSummary summary = new SettlementSummary();
        summary.setAmount(gross);
        SettlementMain main = new SettlementMain();
        main.setId("discount-test");
        main.setSummaryTab(Set.of(summary));
        main.setExpenseAmount(new BigDecimal("100.00"));
        main.setDiscountAmount(999.0);
        main.setNetTestAmount(BigDecimal.ZERO);
        main.setTotalAmount(BigDecimal.ZERO);
        return main;
    }
}
