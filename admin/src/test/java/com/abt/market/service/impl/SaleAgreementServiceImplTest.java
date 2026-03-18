package com.abt.market.service.impl;

import com.abt.market.entity.SaleAgreement;
import com.abt.market.model.SaleAgreementRequestForm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
public class SaleAgreementServiceImplTest {

    @Autowired
    private SaleAgreementServiceImpl saleAgreementServiceImpl;

    /**
     * 测试 1：测试刚刚新增的时间范围查询逻辑
     */
    @Test
    public void testFindByQuery() {

        SaleAgreementRequestForm form = new SaleAgreementRequestForm();
        form.setPage(1);
        form.setLimit(10);
        form.setLocalStartSignDate(LocalDate.of(2024, 1, 1));
        form.setLocalEndSignDate(LocalDate.of(2026, 12, 31));

        Page<SaleAgreement> resultPage = saleAgreementServiceImpl.findByQuery(form);

        System.out.println("总共找到符合条件的记录数: " + resultPage.getTotalElements() + " 条");

        if (resultPage.getTotalElements() > 0) {
            System.out.println("========= 以下为查询到的全部数据详细信息 =========");
            List<SaleAgreement> content = resultPage.getContent();
            for (int i = 0; i < content.size(); i++) {
                System.out.println("第 " + (i + 1) + " 条数据 -> " + content.get(i));
            }
            System.out.println("==================================================");
        }
        System.out.println("findByQuery 测试完成\n");
    }
}