package com.abt.wf.model;

import com.abt.common.model.RequestForm;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 差旅报销搜索
 * criteria: 申请人(user),申请日期(startDate-endDate),出差人包含(staff), 审批编号(id), 状态(state), 分页
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TripRequestForm extends RequestForm {

    /**
     * 出差人员包含
     */
    private String staff;
}
