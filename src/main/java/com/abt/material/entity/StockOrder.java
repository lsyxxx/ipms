package com.abt.material.entity;

import com.abt.common.config.CommonJpaAuditListener;
import com.abt.common.config.ValidateGroup;
import com.abt.common.model.AuditInfo;
import com.abt.common.service.CommonJpaAudit;
import com.abt.material.model.MaterialDetailDTO;
import com.abt.sys.model.WithQuery;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 出入库单
 */
@Getter
@Setter
@Entity
@Table(name = "stock_order")
@NamedEntityGraphs({
        @NamedEntityGraph(name = "StockOrder.withStock", attributeNodes = @NamedAttributeNode("stockList")),
        @NamedEntityGraph(name = "StockOrder.withWarehouse", attributeNodes = @NamedAttributeNode("warehouse")),
})
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@EntityListeners(CommonJpaAuditListener.class)
public class StockOrder extends AuditInfo implements CommonJpaAudit, WithQuery<StockOrder> {

    @Id
    @GeneratedValue(generator  = "timestampIdGenerator")
    @GenericGenerator(name = "timestampIdGenerator", type = com.abt.common.config.TimestampIdGenerator.class)
    private String id;

    /**
     * 单据日期
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(groups = {ValidateGroup.Save.class}, message = "单据日期不能为空")
    @Column(name="order_date", nullable = false)
    private LocalDate orderDate;

    /**
     * 仓库id
     */
    @Column(name="wh_id")
    private String warehouseId;

    @Column(name="wh_name", length = 200)
    private String warehouseName;

    /**
     * 仓库地点
     */
    @NotNull(groups = {ValidateGroup.Save.class}, message = "仓库地点不能为空")
    @Column(name="wh_address", length = 500)
    private String warehouseAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action= NotFoundAction.IGNORE)
    @JsonIgnore
    @JoinColumn(name = "wh_id", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)  , insertable = false, updatable = false)
    private Warehouse warehouse;

    /**
     * 附件json
     */
    @Column(name="file_list", columnDefinition = "TEXT")
    private String fileList;

    /**
     * 入库
     */
    public static final int STOCK_TYPE_IN = 1;

    /**
     * 出库
     */
    public static final int STOCK_TYPE_OUT = 2;
    /**
     * 盘点
     */
    public static final int STOCK_TYPE_CHECK = 3;

    /**
     * 出入库类型，出库/入库
     * 1：入库；2：出库。
     * 枚举保存在Category中
     */
    @NotNull(message = "必须传入出入库类型")
    @Column(name="stock_type", columnDefinition = "TINYINT")
    private Integer stockType;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "stockOrder")
    @JsonIgnoreProperties({"stockList"})
    private List<Stock> stockList;

    /**
     * 交货/收货部门id
     */
    @Column(name="dept_id")
    private String deptId;
    @Column(name="dept_name")
    private String deptName;

    /**
     * 交货/收货人 工号
     */
    @Column(name="job_number")
    private String jobNumber;
    @Column(name="user_name")
    private String username;

    @Column(name="remark_", length = 1000)
    private String remark;

    @Column(name="is_del", columnDefinition = "BIT")
    private boolean isDeleted = false;

    @Transient
    private String bizType;

    @Transient
    private List<MaterialDetailDTO> materialDetailDTOList;

    @Transient
    private List<MaterialDetailDTO> errorList;

    @Transient
    private boolean hasError = false;

    public void addStock(Stock stock) {
        if (this.stockList == null) {
            this.stockList = new ArrayList<>();
        }
        stockList.add(stock);
    }

    @Override
    public StockOrder afterQuery() {
        if (this.warehouse != null) {
            this.warehouseId = this.warehouse.getId();
            this.warehouseName = this.warehouse.getName();
            this.warehouseAddress = this.warehouse.getAddress();
        }
        return this;
    }
}
