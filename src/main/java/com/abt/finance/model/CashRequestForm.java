package com.abt.finance.model;

import com.abt.common.model.RequestForm;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * 资金流入流出查询参数
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CashRequestForm extends RequestForm {
    private String businessId;
    private String payAccount;
}
