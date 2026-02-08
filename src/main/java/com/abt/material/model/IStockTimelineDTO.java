package com.abt.material.model;

import java.time.LocalDateTime;

public interface IStockTimelineDTO {
    void setMaterialId(String materialId);
    void setWarehouseId(String warehouseId);
    void setWarehouseName(String warehouseName);
    void setStockOrderId(String stockOrderId);
    void setStockType(int stockType);
    void setCreateUsername(String createUsername);
    void setCreateDate(LocalDateTime createDate);
    void setUsername(String username);
    void setDeptName(String deptName);
    void setNum(Double num);
    void setQuantity(Double quantity);

    String getMaterialId();
    String getMaterialName();
    String getWarehouseId();
    String getWarehouseName();
    String getStockOrderId();
    int getStockType();
    String getCreateUsername();
    LocalDateTime getCreateDate();
    String getUsername();
    String getDeptName();
    Double getNum();
    Double getQuantity();

}
