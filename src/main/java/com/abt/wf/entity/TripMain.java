package com.abt.wf.entity;

import com.abt.common.config.ValidateGroup;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
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
    @NotNull(message = "出差人不能为空", groups = {ValidateGroup.Save.class})
    @Column(name="staff_")
    private String staff;

    /**
     * 收款人
     */
    @NotNull(message = "收款人不能为空", groups = {ValidateGroup.Save.class})
    @Column(name="rec_user", length = 32)
    private String receiveUser;

    /**
     * 出差事由
     */
    @NotNull(message = "出差事由不能为空", groups = {ValidateGroup.Save.class})
    @Column(name="reason", columnDefinition = "VARCHAR(1000)")
    private String reason;

    @NotNull(message = "出差开始日期不能为空", groups = {ValidateGroup.Save.class})
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name="trip_start_date")
    private LocalDate tripStartDate;

    @NotNull(message = "出差结束日期不能为空", groups = {ValidateGroup.Save.class})
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name="trip_end_date")
    private LocalDate tripEndDate;

    /**
     * 出差天数
     */
    @Column(name="day_sum")
    private int daySum;

    @NotNull(message = "业务归属不能为空")
    @Column(name = "company_")
    private String company;

    @Column(name="managers", columnDefinition = "VARCHAR(1600)")
    private String managers;

    @Column(name = "other_file", columnDefinition = "VARCHAR(MAX)")
    private String fileList;

    /**
     * 总金额
     */
    @NotNull(groups = {ValidateGroup.Preview.class, ValidateGroup.Save.class})
    @Column(name="sum_", columnDefinition = "DECIMAL(10,2)")
    private BigDecimal sum;

    /**
     * 删除的级联操作
     */
    @OneToMany(mappedBy = "main", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
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



}