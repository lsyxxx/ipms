package com.abt.oa.entity;

import com.abt.common.util.TimeUtil;
import com.abt.sys.model.WithQuery;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Nationalized;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * 请假
 */

@Getter
@Setter
@Entity
@Immutable
@Table(name = "T_FrmLeaveReq")
public class FrmLeaveReq implements WithQuery<FrmLeaveReq> {
    @Id
    @Size(max = 50)
    @NotNull
    @Column(name = "Id", nullable = false, length = 50)
    private String id;

    @Size(max = 10)
    @NotNull
    @Nationalized
    @Column(name = "ApplyUserName", nullable = false, length = 10)
    private String applyUserName;

    @Size(max = 20)
    @Nationalized
    @Column(name = "RequestType", length = 20)
    private String requestType;

    @Column(name = "StartDate")
    private LocalDate startDate;

    @Size(max = 30)
    @Column(name = "StartTime", length = 30)
    private String startTime;

    @Column(name = "EndDate")
    private LocalDate endDate;

    @Size(max = 30)
    @Column(name = "EndTime", length = 30)
    private String endTime;

    @Size(max = 500)
    @Nationalized
    @Column(name = "RequestComment", length = 500)
    private String requestComment;

    @Size(max = 500)
    @Column(name = "Attachment", length = 500)
    private String attachment;

    @NotNull
    @Column(name = "CreateDate", nullable = false)
    private LocalDateTime createDate;

    @Size(max = 50)
    @Column(name = "CreateUserName", length = 50)
    private String createUserName;

    @NotNull
    @Column(name = "Operatedate", nullable = false)
    private LocalDateTime operatedate;

    @Size(max = 50)
    @NotNull
    @Column(name = "OperatorName", nullable = false, length = 50)
    private String operatorName;

    @Size(max = 50)
    @NotNull
    @Column(name = "Operator", nullable = false, length = 50)
    private String operator;

    @Size(max = 200)
    @Column(name = "Note", length = 200)
    private String note;

    @Lob
    @Column(name = "FilePathUrl")
    private String filePathUrl;

    @Size(max = 2)
    @Column(name = "IsFinish", length = 2)
    private String isFinish;

    @Column(name = "HourTime", precision = 18, scale = 2)
    private BigDecimal hourTime;
    @Size(max = 50)
    @Column(name = "ApplyUserID", length = 50)
    private String applyUserID;

    @Column(name = "DayTime", precision = 18, scale = 2)
    private BigDecimal dayTime;
    @Size(max = 50)
    @Column(name = "RequestTypeName", length = 50)
    private String requestTypeName;

    @Column(name = "CreateUserId")
    private String createUserId;


    @Column(name = "FlowInstanceId")
    private String flowInstanceId;

    @Transient
    private LocalDateTime startDateTime;
    @Transient
    private LocalDateTime endDateTime;

    @Override
    public FrmLeaveReq afterQuery() {
        this.startDateTime = TimeUtil.toLocalDateTime(this.startDate, this.startTime);
        this.endDateTime = TimeUtil.toLocalDateTime(this.endDate, this.endTime);
        return this;
    }

}