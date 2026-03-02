package com.abt.market.model;

import com.abt.common.model.RequestForm;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class SaleAgreementRequestForm extends RequestForm {
    /**
     * 乙方单位
     */
    private String partyB;
    /**
     * 甲方
     */
    private String partyA;
    /**
     * 合同属性
     * 开口/闭口
     */
    private String attribute;

    /**
     * 登记年月
     */
    private String registerYM;

    //localStartDate 和 localEndDate用父类的
    /**
     * 签订时间开始 (按照领导规范使用 LocalDate)
     */
    private LocalDate signDateStart;

    /**
     * 签订时间结束 (按照领导规范使用 LocalDate)
     */
    private LocalDate signDateEnd;

}