package com.abt.testing.model;

import com.abt.common.model.RequestForm;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 合同相关查询条件
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AgreementRequestForm extends RequestForm {

    /**
     * 乙方id
     */
    private String yCompanyId;

}
