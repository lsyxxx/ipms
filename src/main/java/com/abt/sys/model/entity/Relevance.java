package com.abt.sys.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 关联表
 */
@Table(name = "Relevance")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EntityListeners(AuditingEntityListener.class)
public class Relevance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String Id;

    @Column(name="Description", columnDefinition="VARCAHR(100)")
    private String description;

    @Column(name="Key", columnDefinition="VARCHAR(100)")
    private String Key;

    @Column(name="Status", columnDefinition="TINYINT")
    private int Status;

    @LastModifiedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime operateTime;

    @LastModifiedBy
    @Column(name="OperatorId", columnDefinition="VARCHAR(128)")
    private String operatorId;

    @Column(name="FirstId", columnDefinition="VARCHAR(128)")
    private String firstId;

    @Column(name="SecondId", columnDefinition="VARCHAR(128)")
    private String secondId;
    
    @Column(name="ThirdId", columnDefinition="VARCHAR(128)")
    private String thirdId;

    @Column(name="ExtendInfo", columnDefinition="VARCHAR(100)")
    private String extendInfo;
}