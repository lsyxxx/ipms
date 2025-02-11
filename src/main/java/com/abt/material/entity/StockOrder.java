package com.abt.material.entity;

import com.abt.common.config.CommonJpaAuditListener;
import com.abt.common.config.ValidateGroup;
import com.abt.common.entity.Category;
import com.abt.common.model.AuditInfo;
import com.abt.common.service.CommonJpaAudit;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
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
})
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@EntityListeners(CommonJpaAuditListener.class)
public class StockOrder extends AuditInfo implements CommonJpaAudit {

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
     * 入库仓库地点
     */
    @NotNull(groups = {ValidateGroup.Save.class}, message = "仓库地点不能为空")
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
    @NotNull
    @Column(name="stock_type", columnDefinition = "TINYINT")
    private int stockType = 0;


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "stockOrder")
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

}
