package com.abt.market.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 销售合同与委托单关联检测实体对象
 */
@Data
@NoArgsConstructor
public class ContractEntrust implements Serializable {

    /** 1. 合同id */
    private String contractId;

    /** 2. 合同编号 */
    private String contractCode;

    /** 3. 合同名称 */
    private String contractName;

    /** 4. 合同客户名称 */
    private String customerName;

    /** 5. 委托单编号 */
    private String entrustId;

    /** 6. 委托单项目名称 */
    private String projectName;

    /** 7. 委托单甲方公司 */
    private String jiafangCompany;

    /** 8. 样品数量 */
    private Integer sampleCount;


    public ContractEntrust(String contractId, String contractCode, String contractName, String customerName,
                           String entrustId, String projectName, String jiafangCompany, Long sampleCount) {
        this.contractId = contractId;
        this.contractCode = contractCode;
        this.contractName = contractName;
        this.customerName = customerName;
        this.entrustId = entrustId;
        this.projectName = projectName;
        this.jiafangCompany = jiafangCompany;
        // 安全转换 Long -> Integer
        this.sampleCount = (sampleCount != null) ? sampleCount.intValue() : 0;
    }
}