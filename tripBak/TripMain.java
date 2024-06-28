package com.abt.wf.entity;

import com.abt.common.model.AuditInfo;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

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
     * 出差人
     */
    @Column(name="staff_")
    private String staff;

    /**
     * 出差事由
     */
    @Column(name="reason", columnDefinition = "VARCHAR(1000)")
    private String reason;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name="trip_start_date")
    private LocalDate tripStartDate;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name="trip_end_date")
    private LocalDate tripEndDate;

    @Column(name = "company_", columnDefinition = "VARCHAR(256)")
    private String company;

    @Column(name="managers", columnDefinition = "VARCHAR(1600)")
    private String managers;

    @Column(name = "other_file", columnDefinition = "VARCHAR(MAX)")
    private String otherFileList;



}
