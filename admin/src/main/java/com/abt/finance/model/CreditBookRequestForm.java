package com.abt.finance.model;

import com.abt.common.model.RequestForm;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 *
 */
@Getter
@Setter
public class CreditBookRequestForm extends RequestForm {
    private String businessId;
    private List<String> companies;

    private List<String> serviceList;
    private List<String> payAccountList;
}
