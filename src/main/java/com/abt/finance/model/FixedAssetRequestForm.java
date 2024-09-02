package com.abt.finance.model;

import com.abt.common.model.RequestForm;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 *
 */
@Getter
@Setter
public class FixedAssetRequestForm extends RequestForm {

    private String dept;
    private String assetType;

}
