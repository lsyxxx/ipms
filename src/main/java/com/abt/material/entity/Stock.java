package com.abt.material.entity;

import com.abt.common.model.AuditInfo;
import com.abt.sys.model.WithQuery;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;


/**
 * 出入库的物品详情
 */
@Getter
@Setter
@Entity
@NamedEntityGraphs({
        @NamedEntityGraph(name = "Stock.withStockOrder", attributeNodes = @NamedAttributeNode("stockOrder")),
})
@Table(name = "stock_dtl")
public class Stock implements WithQuery<Stock> {
    @Id
    @Size(max = 64)
    @Column(name = "Id", nullable = false, length = 64)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 关联单据id
     */
    @NotNull
    @Size(max = 64)
    @Column(name="order_id", length = 64, nullable = false)
    private String orderId;

    @Size(max = 64)
    @NotNull
    @Column(name = "material_id", nullable = false, length = 64)
    private String materialId;

    @Size(max = 128)
    @NotNull
    @Column(name = "material_name", nullable = false, length = 128)
    private String materialName;

    @Size(max = 128)
    @Column(name = "xhgz", length = 128)
    private String specification;

    @NotNull
    @Column(name = "num", nullable = false)
    private Integer num;

    @Size(max = 50)
    @Column(name = "unit", length = 50)
    private String unit;

    /**
     * 物品单价
     */
    @Column(name = "price",  precision = 18, scale = 2)
    private BigDecimal price;

    /**
     * 物品总价
     */
    @Column(name = "total_price", precision = 18, scale = 2)
    private BigDecimal totalPrice;

    @Size(max = 500)
    @Column(name = "remark", length = 500)
    private String remark;

    @Column(name = "file_path", columnDefinition = "TEXT")
    private String filePath;

    @Column(name = "sort_no")
    private Integer sortNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), insertable=false, updatable=false)
    @NotFound(action= NotFoundAction.IGNORE)
    @JsonIgnore
    private StockOrder stockOrder;

    @Transient
    private LocalDate orderDate;

    @Transient
    private String warehouseAddress;

    @Transient
    private int stockType;

    @Transient
    private String jobNumber;
    @Transient
    private String username;
    @Transient
    private String deptName;

    /**
     * 业务类型，可自定义
     */
    @Column(name="biz_type")
    private String bizType;

    /**
     * 添加stockOrder信息
     */
    @Override
    public Stock afterQuery() {
        if (stockOrder != null) {
            this.orderDate = stockOrder.getOrderDate();
            this.stockType = stockOrder.getStockType();
            this.jobNumber = stockOrder.getJobNumber();
            this.username = stockOrder.getUsername();
            this.deptName = stockOrder.getDeptName();
        }

        return this;
    }
}