package com.abt.market.repository;

import com.abt.market.entity.SaleAgreement;
import org.camunda.bpm.engine.*;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;
@SpringBootTest

public class SaleAgreementRepositoryTest {

    @Autowired
    private SaleAgreementRepository saleAgreementRepository;




    @Test
    public void testFindByQuery_BoundaryWithMissingDay() {
        // 1. 模拟传参
        Integer signDateStartInt = 20240301;
        Integer signDateEndInt = 20260302;
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "sortNo"));
        // 2. 执行查询
        Page<SaleAgreement> pageResult = saleAgreementRepository.findByQuery(
                null, null, null, null, null,
                null, null,
                signDateStartInt,
                signDateEndInt,
                pageRequest
        );
        // 3. 打印结果
        System.out.println("\n\n\n================ 🏆 查询结果大揭秘 🏆 ================");
        System.out.println("总共查到满足条件的数据条数: " + pageResult.getTotalElements() + " 条");
        for(SaleAgreement sale : pageResult.getContent()) {
            System.out.println("-> 📄 查到的合同 ID: " + sale.getId());
            System.out.println("-> 🏷️ 合同名称: " + sale.getName());
            System.out.println("-> 📅 签订日期: " + sale.getSignYear() + "年" + sale.getSignMonth() + "月" + sale.getSignDay() + "日");
            System.out.println("--------------------------------------------------");

        }
    }
}