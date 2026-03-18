package com.abt.market.model;

import com.abt.common.model.RequestForm;
import lombok.Getter;
import lombok.Setter;

/**
 * 临时表查询
 */
@Getter
@Setter
public class TestTempRequestForm extends RequestForm {

    private String tempMid;

}
