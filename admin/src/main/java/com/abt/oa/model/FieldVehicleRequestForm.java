package com.abt.oa.model;

import com.abt.common.model.RequestForm;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/** 野外用车分页条件，日期范围筛选实际用车日期。 */
@Getter
@Setter
public class FieldVehicleRequestForm extends RequestForm {
    @Size(max = 50, message = "车辆标识不能超过50个字符")
    private String carId;
    private Integer status;
    /** 司机姓名包含查询，空白不筛选。 */
    @Size(max = 100, message = "司机姓名不能超过100个字符")
    private String driverName;
    /** 同行人姓名包含查询，空白不筛选。 */
    @Size(max = 100, message = "同行人姓名不能超过100个字符")
    private String peerName;
}
