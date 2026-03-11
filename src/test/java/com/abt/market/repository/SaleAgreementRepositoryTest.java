package com.abt.market.repository;

import com.abt.market.entity.SaleAgreement;
import com.abt.market.model.ContractEntrust;
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
import org.springframework.util.Assert;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
@SpringBootTest
public class SaleAgreementRepositoryTest {

    @Autowired
    private SaleAgreementRepository saleAgreementRepository;

//    @Test
//    public void testFindByQuery_BoundaryWithMissingDay() {
//        Integer signDateStartInt = 20240301;
//        Integer signDateEndInt = 20260302;
//        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "sortNo"));
//        Page<SaleAgreement> pageResult = saleAgreementRepository.findByQuery(
//                null, null, null, null, null,
//                null, null,
//                signDateStartInt,
//                signDateEndInt,
//                pageRequest
//        );
//        System.out.println("总共查到满足条件的数据条数: " + pageResult.getTotalElements() + " 条");
//        for(SaleAgreement sale : pageResult.getContent()) {
//            System.out.println("-> 查到的合同 ID: " + sale.getId());
//            System.out.println("-> 合同名称: " + sale.getName());
//            System.out.println("-> 签订日期: " + sale.getSignYear() + "年" + sale.getSignMonth() + "月" + sale.getSignDay() + "日");//
//        }
//    }


    @Test
    void findContractEntrustSampleCountList() {
        List<ContractEntrust> resultList = saleAgreementRepository.findContractEntrustSampleCountList();

        System.out.println("查询成功！共查出数据条数: " + resultList.size());
        Assert.notNull(resultList, "查询结果不能为 null");

        System.out.println("数据明细 (全部展示)");
        for (int i = 0; i < resultList.size(); i++) {
            ContractEntrust item = resultList.get(i);
            System.out.println("第 " + (i + 1) + " 条: " +
                    "合同ID=" + item.getContractId() +
                    ", 合同编号=" + item.getContractCode() +
                    ", 合同名称=" + item.getContractName() +
                    ", 客户=" + item.getCustomerName() +
                    ", 委托单=" + item.getEntrustId() +
                    ", 项目名称=" + item.getProjectName() +
                    ", 甲方公司=" + item.getJiafangCompany() +
                    ", 样品数量=" + item.getSampleCount());
        }
    }
}