package com.abt.material.model;

import com.abt.common.model.RequestForm;
import lombok.Getter;
import lombok.Setter;

/**
 *
 */
@Getter
@Setter
public class StockOrderRequestForm extends RequestForm {

    private Integer stockType;

    private String warehouseIdStr;
}
