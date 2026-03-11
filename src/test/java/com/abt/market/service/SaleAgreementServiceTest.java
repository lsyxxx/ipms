package com.abt.market.service;

import com.abt.market.model.ContractEntrust;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.List;

@SpringBootTest
class SaleAgreementServiceTest {

    @Autowired
    private SaleAgreementService saleAgreementService;

    @Test
    void getContractEntrustSampleCountList() {
        //  这里填入想测试的真实合同 ID
        String testId = "d74b95ad-28c6-4c86-8af6-e55f34463b56";
        List<ContractEntrust> resultList = saleAgreementService.getContractEntrustSampleCountList(testId);
        Assert.notNull(resultList, "Service 返回的列表不能为 null");
        System.out.println("总条数: " + resultList.size());
        for (int i = 0; i < resultList.size(); i++) {
            ContractEntrust item = resultList.get(i);
            System.out.println(String.format("第 %d 条 -> [合同ID: %s, 编号: %s, 名称: %s, 客户: %s, 委托单: %s, 项目: %s, 甲方公司: %s, 样品数: %d]",
                    (i + 1),
                    item.getContractId(),
                    item.getContractCode(),
                    item.getContractName(),
                    item.getCustomerName(),
                    item.getEntrustId(),
                    item.getProjectName(),
                    item.getJiafangCompany(),
                    item.getSampleCount()));
        }
    }
}