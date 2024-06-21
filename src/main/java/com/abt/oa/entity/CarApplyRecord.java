package com.abt.oa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import java.time.LocalDateTime;

/**
 * 只读
 */
//@Getter
//@Setter
//@Entity
//@Table(name = "T_card_apply")
//@NoArgsConstructor
//@Immutable
public class CarApplyRecord {

    /**
     * 采用snowflake雪花算法
     */
    @Id
    @Size(max = 50)
    @Column(name = "Id", nullable = false, length = 50)
    private String id;

    @Size(max = 50)
    @NotNull
    @Column(name = "ApplyUserID", nullable = false, length = 50)
    private String applyUserID;

    @Size(max = 40)
    @Column(name = "Peer", length = 40)
    private String peer;

    @Column(name = "PlanKil", nullable = false, columnDefinition = "DECIMAL(18,2)")
    private Double planKil;

    @Size(max = 100)
    @NotNull
    @Column(name = "UseDesc", nullable = false, length = 100)
    private String useDesc;

    @Size(max = 40)
    @Column(name = "UseCarDept", length = 40)
    private String useCarDept;

    @NotNull
    @Column(name = "DepartureDate", nullable = false)
    private LocalDateTime departureDate;

    @Size(max = 30)
    @Column(name = "DepartureTime", length = 30)
    private String departureTime;

    @Size(max = 100)
    @Column(name = "DepartureAddress", length = 100)
    private String departureAddress;

    @Size(max = 100)
    @Column(name = "Destination", length = 100)
    private String destination;

    @Column(name = "ReturnDate")
    private LocalDateTime returnDate;

    @Column(name = "Backdate")
    private LocalDateTime backdate;

    @Column(name = "CollectPrice", nullable = false, columnDefinition = "DECIMAL(18,2)")
    private Double collectPrice;

    @Column(name = "backKil", nullable = false, columnDefinition = "DECIMAL(18,2)")
    private Double backKil;

    @Column(name = "CurKil", nullable = false, columnDefinition = "DECIMAL(18,2)")
    private Double curKil;

    @Column(name = "OilFree", nullable = false, columnDefinition = "DECIMAL(18,2)")
    private Double oilFree;

    @Column(name = "OilL", nullable = false, columnDefinition = "DECIMAL(18,2)")
    private Double oilL;

    @Size(max = 50)
    @Column(name = "CarId", length = 50)
    private String carId;

    @Size(max = 200)
    @Column(name = "Note", length = 200)
    private String note;

    @Size(max = 50)
    @NotNull
    @Column(name = "CreateUserId", nullable = false, length = 50)
    private String createUserId;

    @Size(max = 50)
    @NotNull
    @Column(name = "Operator", nullable = false, length = 50)
    private String operator;

    @Size(max = 50)
    @NotNull
    @Column(name = "CreateUserName", nullable = false, length = 50)
    private String createUserName;

    @NotNull
    @Column(name = "CreateDate", nullable = false)
    private LocalDateTime createDate;

    @NotNull
    @Column(name = "Operatedate", nullable = false)
    private LocalDateTime operatedate;

    @Size(max = 50)
    @NotNull
    @Column(name = "OperatorName", nullable = false, length = 50)
    private String operatorName;

    @Size(max = 50)
    @Column(name = "FlowInstanceId", length = 50)
    private String flowInstanceId;

    @Size(max = 50)
    @Column(name = "Driver", length = 50)
    private String driver;

    @Size(max = 50)
    @Column(name = "ApplyUserName", length = 50)
    private String applyUserName;

    @Size(max = 3)
    @Column(name = "IsFinish", length = 3)
    private String isFinish;

    @Column(name = "YcDate")
    private LocalDateTime ycDate;

    @Transient
    private String carNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CarId", referencedColumnName = "Id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), insertable=false, updatable=false)
    @NotFound(action= NotFoundAction.IGNORE)
    private CarInfo carInfo;


    @Override
    public String toString() {
        return "CarApplyRecord{" +
                "carNo='" + carNo + '\'' +
                ", ycDate=" + ycDate +
                ", isFinish='" + isFinish + '\'' +
                ", applyUserName='" + applyUserName + '\'' +
                ", driver='" + driver + '\'' +
                ", flowInstanceId='" + flowInstanceId + '\'' +
                ", operatorName='" + operatorName + '\'' +
                ", operatedate=" + operatedate +
                ", createDate=" + createDate +
                ", createUserName='" + createUserName + '\'' +
                ", operator='" + operator + '\'' +
                ", createUserId='" + createUserId + '\'' +
                ", note='" + note + '\'' +
                ", carId='" + carId + '\'' +
                ", oilL=" + oilL +
                ", oilFree=" + oilFree +
                ", curKil=" + curKil +
                ", backKil=" + backKil +
                ", collectPrice=" + collectPrice +
                ", backdate=" + backdate +
                ", returnDate=" + returnDate +
                ", destination='" + destination + '\'' +
                ", departureAddress='" + departureAddress + '\'' +
                ", departureTime='" + departureTime + '\'' +
                ", departureDate=" + departureDate +
                ", useCarDept='" + useCarDept + '\'' +
                ", useDesc='" + useDesc + '\'' +
                ", planKil=" + planKil +
                ", peer='" + peer + '\'' +
                ", applyUserID='" + applyUserID + '\'' +
                ", id='" + id + '\'' +
//                ", carNo='" + this.getCarInfo().getCarNo() + '\'' +
                '}';
    }
}