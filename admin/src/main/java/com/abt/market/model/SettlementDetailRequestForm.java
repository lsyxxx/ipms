package com.abt.market.model;

import com.abt.common.model.RequestForm;
import lombok.Getter;
import lombok.Setter;

/**
 * 结算单详细查询参数。
 */
@Getter
@Setter
public class SettlementDetailRequestForm extends RequestForm {
    private String entrustId;
    private String clientId;
}
