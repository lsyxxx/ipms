package com.abt.wf.entity;

import com.abt.common.config.ValidateGroup;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.abt.wf.config.Constants.*;
import static com.abt.wf.config.Constants.KEY_MANAGER;

/**
 * 差旅报销基础
 */
@Table(name = "wf_trip_main")
@Entity
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TripMain extends WorkflowBase {

    @Id
    @GeneratedValue(generator  = "timestampIdGenerator")
    @GenericGenerator(name = "timestampIdGenerator", type = com.abt.common.config.TimestampIdGenerator.class)
    private String id;

    /**
     * 出差人姓名，多人用逗号分隔
     */
    @NotNull(message = "出差人不能为空", groups = {ValidateGroup.Apply.class})
    @Column(name="staff_", length = 1000)
    private String staff;

    /**
     * 关联项目
     */
    @Column(name="project")
    private String project;

    /**
     * 收款人
     */
    @Column(name="rec_userid")
    private String receiveUserid;

    @Column(name="rec_username", length = 128)
    private String receiveUsername;

    /**
     * 收款账号
     */
    @Column(name="rec_account")
    private String receiveAccount;

    /**
     * 出差事由
     */
    @NotNull(message = "出差事由不能为空", groups = {ValidateGroup.Apply.class})
    @Column(name="reason", columnDefinition = "VARCHAR(1000)")
    private String reason;

    @NotNull(message = "出差开始日期不能为空", groups = {ValidateGroup.Apply.class})
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name="trip_start_date")
    private LocalDate tripStartDate;

    @NotNull(message = "出差结束日期不能为空", groups = {ValidateGroup.Apply.class})
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name="trip_end_date")
    private LocalDate tripEndDate;

    /**
     * 出差天数
     */
    @Column(name="day_sum")
    private int daySum;

    @NotNull(message = "业务归属不能为空", groups = {ValidateGroup.Apply.class})
    @Column(name = "company_")
    private String company;

    @Column(name="managers", columnDefinition = "VARCHAR(1600)")
    private String managers;

    @Column(name = "file_list", columnDefinition = "VARCHAR(MAX)")
    private String fileList;

    /**
     * 总金额
     */
    @NotNull(message = "报销总金额不能为空", groups = {ValidateGroup.Preview.class, ValidateGroup.Apply.class})
    @Column(name="sum_", columnDefinition = "DECIMAL(10,2)")
    private BigDecimal sum;

    /**
     * 单据及附件数量
     */
    @Max(value = 99, message = "单据数量不能超过99个", groups = {ValidateGroup.Apply.class})
    @Column(name="voucher_num", columnDefinition = "TINYINT")
    private int voucherNum;

//    @OneToMany(mappedBy = "main", fetch = FetchType.LAZY)

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "mid", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), insertable=false, updatable=false)
    private List<TripDetail> details = new ArrayList<>();

    @Transient
    private String decision;

    @Transient
    private String comment;

    @Transient
    private Map<String, Object> variableMap = new HashMap<>();

    public Map<String, Object> createVariableMap() {
        this.variableMap = new HashMap<>();
        variableMap.put(KEY_STARTER, this.getSubmitUserid());
        variableMap.put(KEY_COST, this.getSum());
        if (StringUtils.isBlank(this.getManagers())) {
            variableMap.put(KEY_MANAGER, List.of());
        } else {
            variableMap.put(KEY_MANAGER, List.of(this.getManagers().split(",")));
        }

        return this.variableMap;
    }

    public void addDetail(TripDetail detail) {
        if (this.details == null) {
            this.details = new ArrayList<>();
        }
        this.details.add(detail);
    }
}
