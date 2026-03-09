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

    /**
     * 合同签订时间开始日期
     */
    private LocalDate localStartSignDate;

    /**
     * 合同签订时间结束日期
     */
    private LocalDate localEndSignDate;

}