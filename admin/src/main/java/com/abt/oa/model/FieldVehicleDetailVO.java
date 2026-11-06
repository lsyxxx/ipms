package com.abt.oa.model;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

/** 用车详情与还车预览，含完整加油明细。 */
@Getter
@Setter
public class FieldVehicleDetailVO extends FieldVehicleListVO {
    private List<FieldVehicleFuelVO> fuelItems = List.of();
}
