package com.abt.material.entity;

import com.abt.common.config.CommonJpaAuditListener;
import com.abt.common.model.AuditInfo;
import com.abt.common.service.CommonJpaAudit;
import com.abt.sys.model.WithQuery;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import java.math.BigDecimal;

/**
 * 库存信息，不可直接修改数据。
 * 更新库存数据则插入一条数据
 */
@Getter
@Setter
@Table(name = "stock_inventory")
@Entity
@NamedEntityGraphs({
        @NamedEntityGraph(name = "Inventory.materialDetail", attributeNodes = @NamedAttributeNode("materialDetail")),
        @NamedEntityGraph(name = "Inventory.warehouse", attributeNodes = @NamedAttributeNode("warehouse")),
})
@EntityListeners(CommonJpaAuditListener.class)
@NoArgsConstructor
public class Inventory extends AuditInfo implements CommonJpaAudit, WithQuery<Inventory> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 物品id
     */
    @Column(name="m_id")
    private String materialId;

    /**
     * 当前库存数量
     */
    @Column(name="quantity_")
    private int quantity;

    /**
     * 仓库id
     */
    @Column(name="wh_id")
    private String warehouseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "m_id", referencedColumnName = "id", insertable = false, updatable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private MaterialDetail materialDetail;

    /**
     * 关联的stock_order
     */
    @Column(name="o_id")
    private String orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "o_id", referencedColumnName = "id", insertable = false, updatable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @JsonIgnore
    @NotFound(action= NotFoundAction.IGNORE)
    private StockOrder order;

    @Transient
    private String materialName;
    @Transient
    private String materialSpec;
    @Transient
    private String materialUnit;
    @Transient
    private String materialType;
    /**
     * 单价
     */
    @Transient
    private BigDecimal unitPrice;
    /**
     * 库存总价格
     */
    @Transient
    private BigDecimal totalPrice;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wh_id", referencedColumnName = "id", insertable = false, updatable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Warehouse warehouse;

    @Transient
    private String warehouseName;
    @Transient
    private String warehouseAddress;

    /**
     * 原有库存
     */
    @Transient
    private Integer lastInventory;

    public Inventory(String materialId, String warehouseId) {
        this.materialId = materialId;
        this.warehouseId = warehouseId;
    }

    @Override
    public Inventory afterQuery() {
        if (this.materialDetail != null) {
            this.materialName = this.materialDetail.getName();
            this.materialSpec = this.materialDetail.getSpecification();
            this.materialUnit = this.materialDetail.getUnit();
            this.unitPrice = this.materialDetail.getPrice();
            if (this.unitPrice != null) {
                this.totalPrice = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
            }
            if (this.materialDetail.getMaterialType() != null) {
                this.materialType = this.materialDetail.getMaterialType().getName();
            }
        }
        if (this.warehouse != null) {
            this.warehouseName = this.warehouse.getName();
            this.warehouseAddress = this.warehouse.getAddress();
        }
        return this;
    }
}
