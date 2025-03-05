package com.abt.finance.model;

import com.abt.common.model.RequestForm;
import lombok.Getter;
import lombok.Setter;

/**
 *
 */
@Getter
@Setter
public class InvoiceRequestForm extends RequestForm {
    private String refCode;
}
