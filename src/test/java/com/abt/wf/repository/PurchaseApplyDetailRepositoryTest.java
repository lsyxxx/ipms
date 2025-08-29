package com.abt.wf.repository;

import com.abt.wf.model.PurchaseSummaryAmount;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PurchaseApplyDetailRepositoryTest {

    @Autowired
    private PurchaseApplyDetailRepository purchaseApplyDetailRepository;

    @Test
    void summaryGiftTotalAmount() {
        final List<PurchaseSummaryAmount> list = purchaseApplyDetailRepository.summaryGiftTotalAmount("礼品类", null, null);
        assertNotNull(list);
        list.forEach(psa -> {
            System.out.printf("id: %s, 名称: %s, 金额: %s, 数量: %d\n", psa.getDetailId(), psa.getName(), psa.getTotalAmount() == null ? "" : psa.getTotalAmount(), psa.getQuantity());
        });
    }

    @Test
    void sumPurchase() {
        final Double sum = purchaseApplyDetailRepository.sumPurchase("礼品类", LocalDate.of(2025, 1, 1), LocalDate.of(2025, 8, 1));
        System.out.println(sum.toString());

    }
}