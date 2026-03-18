package com.abt.material.entity;

import com.abt.common.config.CommonJpaAuditListener;
import com.abt.common.model.AuditInfo;
import com.abt.common.service.CommonJpaAudit;
import com.abt.sys.model.WithQuery;
import cn.idev.excel.annotation.ExcelProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 库存预警设置
 */
@Getter
@Setter
@Entity
@Table(name = "stock_inventory_alert")
@NoArgsConstructor
@AllArgsConstructor
@NamedEntityGraphs({
        @NamedEntityGraph(name = "InventoryAlert.materialDetail", attributeNodes = @NamedAttributeNode("materialDetail")),
        @NamedEntityGraph(name = "InventoryAlert.warehouse", attributeNodes = @NamedAttributeNode("warehouse")),
})
@EntityListeners(CommonJpaAuditListener.class)
public class InventoryAlert extends AuditInfo implements CommonJpaAudit, WithQuery<InventoryAlert> {

    @EmbeddedId
    private InventoryId id;

    @ExcelProperty(index = 0)
    @Transient
    private String materialTypeName;

    @ExcelProperty(index = 1)
    @Column(name="m_name")
    private String materialName;
    @ExcelProperty(index = 2)
    @Transient
    private String materialSpec;
    @ExcelProperty(index = 3)
    @Transient
    private String materialUnit;
    @ExcelProperty(index = 4)
    @Transient
    private String warehouseName;
    /**
     * 预警数量
     */
    @ExcelProperty(index = 5)
    @Column(name="alert_num", columnDefinition = "DECIMAL(9,2)")
    private Double alertNum;
    @ExcelProperty(index = 6)
    @Transient
    private String materialId;

    @ExcelProperty(index = 7)
    @Column(name="remark_", length = 1000)
    private String remark;


    @Column(name="sort_no")
    private int sortNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "m_id", referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT), updatable = false, insertable = false)
    private MaterialDetail materialDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wh_id", referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT), updatable = false, insertable = false)
    private Warehouse warehouse;
    @Transient
    private String warehouseAddress;
    @Transient
    private String errorMsg;

    @Transient
    private String warehouseId;

    @Transient
    private boolean isError = false;


    public boolean isError() {
        this.isError = !StringUtils.isBlank(errorMsg);
        return isError;
    }

    @Override
    public InventoryAlert afterQuery() {
        if (this.materialDetail != null) {
            this.materialSpec = this.materialDetail.getSpecification();
            this.materialUnit = this.materialDetail.getUnit();
        }
        if (this.warehouse != null) {
            this.warehouseName = this.warehouse.getName();
            this.warehouseAddress = this.warehouse.getAddress();
        }
        return this;
    }
}