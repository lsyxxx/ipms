package com.abt.material.model;

import com.abt.material.entity.Inventory;
import com.abt.material.entity.MaterialDetail;
import com.abt.material.entity.MaterialType;
import com.abt.material.entity.Warehouse;
import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 *
 */
@Getter
@Setter
@ToString
public class MaterialDetailDTO implements IMaterialDetailDTO {
    @ExcelProperty(index = 0)
    private String materialTypeName;
    @ExcelProperty(index = 1)
    private String name;
    @ExcelProperty(index = 2)
    private String specification;
    @ExcelProperty(index = 3)
    private String unit;
    @ExcelProperty(index = 4)
    private String warehouseName;
    /**
     * 当前库存
     */
    @ExcelProperty(index = 5)
    private Double currentInventory;
    @ExcelProperty(index = 6)
    private Double checkInventory;
    @ExcelProperty(index = 7)
    private String id;
    @ExcelProperty(index = 8)
    private String remark;
    @ExcelIgnore
    private String materialTypeId;

    @ExcelIgnore
    private BigDecimal price;
    @ExcelIgnore
    private String usage;

    @ExcelIgnore
    private String warehouseId;


    @ExcelIgnore
    private MaterialDetail materialDetail;
    @ExcelIgnore
    private MaterialType materialType;
    @ExcelIgnore
    private Inventory inventory;
    @ExcelIgnore
    private Warehouse warehouse;
    /**
     * 错误信息
     */
    @ExcelIgnore
    private String error;

    public boolean hasError() {
        return StringUtils.isNotBlank(error);
    }
}
