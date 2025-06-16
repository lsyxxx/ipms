package com.abt.material.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 月度库存统计DTO
 */
@Data
@NoArgsConstructor
public class MonthlyStockStatsDTO {
    
    /**
     * 年份
     */
    private Integer year;
    
    /**
     * 月份
     */
    private Integer month;
    
    /**
     * 物料类型ID
     */
    private String materialTypeId;
    
    /**
     * 物料类型名称
     */
    private String materialTypeName;
    
    /**
     * 物料ID
     */
    private String materialId;
    
    /**
     * 物料名称
     */
    private String materialName;
    
    /**
     * 规格型号
     */
    private String specification;

    /**
     * 物料单价
     */
    private BigDecimal price;
    
    /**
     * 单位
     */
    private String unit;
    
    /**
     * 出入库类型 (使用枚举)
     */
    private StockType stockType;
    
    /**
     * 总数量
     */
    private Double totalQuantity = 0.0;
    
    /**
     * 总价值
     */
    private BigDecimal totalValue = BigDecimal.ZERO;
    
    /**
     * 记录数量
     */
    private Long recordCount;
    
    /**
     * 仓库ID
     */
    private String warehouseId;
    
    /**
     * 仓库名称
     */
    private String warehouseName;

    /**
     * 构造函数用于JPQL查询结果映射（不包含仓库信息）
     */
    public MonthlyStockStatsDTO(Integer year, Integer month, String materialTypeId, 
                               String materialTypeName, String materialId, String materialName, 
                               String specification, BigDecimal price, String unit, Integer stockTypeValue, 
                               Double totalQuantity, BigDecimal totalValue, Long recordCount) {
        this.year = year;
        this.month = month;
        this.materialTypeId = materialTypeId;
        this.materialTypeName = materialTypeName;
        this.materialId = materialId;
        this.materialName = materialName;
        this.specification = specification;
        this.price = price;
        this.unit = unit;
        this.stockType = StockType.fromValue(stockTypeValue);
        this.totalQuantity = totalQuantity;
        this.totalValue = totalValue;
        this.recordCount = recordCount;
    }

    /**
     * 构造函数用于JPQL查询结果映射（包含仓库信息）
     */
    public MonthlyStockStatsDTO(Integer year, Integer month, String materialTypeId, 
                               String materialTypeName, String materialId, String materialName, 
                               String specification, BigDecimal price, String unit, Integer stockTypeValue, 
                               Double totalQuantity, BigDecimal totalValue, Long recordCount,
                               String warehouseId, String warehouseName) {
        this(year, month, materialTypeId, materialTypeName, materialId, materialName, 
             specification, price, unit, stockTypeValue, totalQuantity, totalValue, recordCount);
        this.warehouseId = warehouseId;
        this.warehouseName = warehouseName;
    }

    /**
     * 添加便利方法
     */
    public Integer getStockTypeValue() {
        return stockType != null ? stockType.getValue() : null;
    }

    public String getStockTypeDescription() {
        return stockType != null ? stockType.getDescription() : "";
    }

    /**
     * 复制物料信息
     * @param dto
     */
    public void copyMaterial(MonthlyStockStatsDTO dto) {
        this.materialTypeId = dto.getMaterialTypeId();
        this.materialTypeName = dto.getMaterialTypeName();
        this.materialId = dto.getMaterialId();
        this.materialName = dto.getMaterialName();
        this.specification = dto.getSpecification();
        this.unit = dto.getUnit();
        this.stockType = dto.getStockType();
        this.year = dto.getYear();
    }
}