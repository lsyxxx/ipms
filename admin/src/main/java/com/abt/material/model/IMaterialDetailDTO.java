package com.abt.material.model;

import com.abt.material.entity.Inventory;
import com.abt.material.entity.MaterialDetail;
import com.abt.material.entity.MaterialType;
import com.abt.material.entity.Warehouse;

import java.math.BigDecimal;

public interface IMaterialDetailDTO {
    String getId();
    String getName();
    String getSpecification();
    String getMaterialTypeId();
    String getMaterialTypeName();
    String getUnit();
    BigDecimal getPrice();
    String getUsage();
    String getRemark();
    Double getCurrentInventory();
    String getWarehouseId();
    String getWarehouseName();

    MaterialDetail getMaterialDetail();
    MaterialType getMaterialType();
    Inventory getInventory();
    Warehouse getWarehouse();

}
