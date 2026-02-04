package com.abt.market.model;

import com.abt.common.model.RequestForm;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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



}
