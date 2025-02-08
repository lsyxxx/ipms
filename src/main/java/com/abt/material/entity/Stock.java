package com.abt.material.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;


/**
 * 出入库的物品
 */
@Getter
@Setter
@Entity
@Table(name = "T_Stock")
public class Stock {
    @Id
    @Size(max = 50)
    @Column(name = "Id", nullable = false, length = 50)
    private String id;

    @Size(max = 50)
    @NotNull
    @Column(name = "stockcatalogId", nullable = false, length = 50)
    private String stockcatalogId;

    @Size(max = 50)
    @NotNull
    @Column(name = "stockDetailId", nullable = false, length = 50)
    private String stockDetailId;

    @Size(max = 50)
    @NotNull
    @Column(name = "stockDetailXhgz", nullable = false, length = 50)
    private String stockDetailXhgz;

    @NotNull
    @Column(name = "Num", nullable = false)
    private Integer num;

    @Size(max = 50)
    @Column(name = "unit", length = 50)
    private String unit;

    @NotNull
    @Column(name = "Price", nullable = false, precision = 18, scale = 2)
    private BigDecimal price;

    @NotNull
    @Column(name = "TotalPrice", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalPrice;

    @Size(max = 50)
    @NotNull
    @Column(name = "Jieshouren", nullable = false, length = 50)
    private String jieshouren;

    @NotNull
    @Column(name = "JieshouDate", nullable = false)
    private Instant jieshouDate;

    @Size(max = 50)
    @NotNull
    @Column(name = "Yanshouren", nullable = false, length = 50)
    private String yanshouren;

    @NotNull
    @Column(name = "YanshouDate", nullable = false)
    private Instant yanshouDate;

    @Size(max = 500)
    @Column(name = "remark", length = 500)
    private String remark;

    @Size(max = 2000)
    @Column(name = "FilePath", length = 2000)
    private String filePath;

    @NotNull
    @Column(name = "Xh", nullable = false)
    private Integer xh;

    /**
     * 关联单据id
     */
    @Column(name="order_id", length = 64)
    private String orderId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn
    private StockOrder stockOrder;

}