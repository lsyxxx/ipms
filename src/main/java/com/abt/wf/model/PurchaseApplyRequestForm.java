package com.abt.wf.model;

import com.abt.common.model.RequestForm;
import lombok.*;

/**
 * 采购流程
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PurchaseApplyRequestForm extends RequestForm {
    /**
     * 列表是否包含详细记录
     */
    private boolean hasDetails = false;

}
