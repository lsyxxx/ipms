package com.abt.oa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "T_car_Info")
@Immutable
public class CarInfo {
    @Id
    @Size(max = 50)
    @Column(name = "Id", nullable = false, length = 50)
    private String id;

    @Column(name = "PurchAseData")
    private LocalDateTime purchAseData;

    @Column(name = "OrgPrice", precision = 18, scale = 2)
    private BigDecimal orgPrice;

    @Size(max = 20)
    @Column(name = "Brand", length = 20)
    private String brand;

    @Size(max = 20)
    @NotNull
    @Column(name = "CarNo", nullable = false, length = 20)
    private String carNo;

    @Size(max = 10)
    @Column(name = "CarType", length = 10)
    private String carType;

    @NotNull
    @Column(name = "InsuredStartData", nullable = false)
    private LocalDateTime insuredStartData;

    @NotNull
    @Column(name = "InsuredEndData", nullable = false)
    private LocalDateTime insuredEndData;

    @NotNull
    @Column(name = "ApprovalData", nullable = false)
    private LocalDateTime approvalData;

    @Column(name = "MainTainData")
    private LocalDateTime mainTainData;

    @Column(name = "NextMainTainData")
    private LocalDateTime nextMainTainData;

    @Column(name = "ValidData")
    private LocalDateTime validData;

    @NotNull
    @Column(name = "InitialKil", nullable = false, precision = 18, scale = 2)
    private BigDecimal initialKil;

    @NotNull
    @Column(name = "SyInitialKil", nullable = false, precision = 18, scale = 2)
    private BigDecimal syInitialKil;

    @Size(max = 20)
    @Column(name = "Driver", length = 20)
    private String driver;

    @Size(max = 20)
    @Column(name = "DriverType", length = 20)
    private String driverType;

    @Size(max = 50)
    @Column(name = "DriverNote", length = 50)
    private String driverNote;

    @Size(max = 200)
    @Column(name = "Note", length = 200)
    private String note;

    @Size(max = 2)
    @Column(name = "IsAvtive", length = 2)
    private String isAvtive;

    @Size(max = 2)
    @Column(name = "IsUse", length = 2)
    private String isUse;

    @Size(max = 50)
    @NotNull
    @Column(name = "CreateUserID", nullable = false, length = 50)
    private String createUserID;

    @NotNull
    @Column(name = "CreateDate", nullable = false)
    private LocalDateTime createDate;

    @NotNull
    @Column(name = "Operatedate", nullable = false)
    private LocalDateTime operatedate;

    @Size(max = 50)
    @NotNull
    @Column(name = "Operator", nullable = false, length = 50)
    private String operator;

    @Size(max = 100)
    @Column(name = "CarPlace", length = 100)
    private String carPlace;

    @Column(name = "SubmitDate")
    private LocalDateTime submitDate;

    @Column(name = "InsuredStartDatashangye")
    private LocalDateTime insuredStartDatashangye;

    @Column(name = "InsuredEndDatashangye")
    private LocalDateTime insuredEndDatashangye;

    @Size(max = 50)
    @Column(name = "OilCarNo", length = 50)
    private String oilCarNo;

    @Size(max = 50)
    @Column(name = "EtcCarNo", length = 50)
    private String etcCarNo;

    @Size(max = 50)
    @Column(name = "XianHaoDay", length = 50)
    private String xianHaoDay;

    @Column(name = "CheWeiStartdate")
    private LocalDateTime cheWeiStartdate;

    @Column(name = "CheWeiStartEndDate")
    private LocalDateTime cheWeiStartEndDate;

    @Column(name = "GaosuyearCardStart")
    private LocalDateTime gaosuyearCardStart;

    @Column(name = "GaosuyearCardEnd")
    private LocalDateTime gaosuyearCardEnd;

    @Lob
    @Column(name = "FilePath")
    private String filePath;

}