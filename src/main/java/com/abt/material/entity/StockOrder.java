package com.abt.material.entity;

import com.abt.common.model.AuditInfo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.data.mapping.PersistentEntity;

import java.time.LocalDate;
import java.util.List;

/**
 * 出入库单
 */
@Getter
@Setter
@Entity
@Table(name = "T_stock_order")
public class StockOrder extends AuditInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Size(min = 1, max = 50)
    @NotNull
    @Column(name = "code_", length = 50)
    private String code;

    /**
     * 单据日期
     */
    @Column(name="order_date", nullable = false)
    private LocalDate orderDate;

    /**
     * 入库仓库地点
     */
    @Size(max = 128)
    @Column(name="stock_loc", nullable = false, length = 128)
    private String stockLocation;

    /**
     * 附件json
     */
    @Column(name="file_list", columnDefinition = "TEXT")
    private String fileList;


    /**
     * 出入库类型，出库/入库
     * 1：入库；2：出库。
     * 枚举保存在Category中
     */
    @Column(name="stock_type", columnDefinition = "TINYINT")
    private int stockType;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "id", referencedColumnName = "Id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), insertable=false, updatable=false)
    @NotFound(action= NotFoundAction.IGNORE)
    private List<Stock> stockList;

    





}
