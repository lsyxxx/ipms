package com.abt.finance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor
@Table(name = "T_fund_two_classify")
public class AccountItem {
    @Id
    @Size(max = 50)
    @Column(name = "Id", nullable = false, length = 50)
    private String id;

    @Size(max = 20)
    @NotNull
    @Column(name = "Code", nullable = false, length = 20)
    private String code;

    @Size(max = 100)
    @NotNull
    @Column(name = "Fname", nullable = false, length = 100)
    private String fname;

    @Size(max = 10)
    @Column(name = "IsAvtive", length = 10)
    private String isActive;

    @Size(max = 100)
    @NotNull
    @Column(name = "classId", nullable = false, length = 100)
    private String classId;

    @Size(max = 200)
    @NotNull
    @Column(name = "className", nullable = false, length = 200)
    private String className;

    @Size(max = 100)
    @NotNull
    @Column(name = "ClassCode", nullable = false, length = 100)
    private String classCode;

    @Size(max = 50)
    @NotNull
    @Column(name = "Operator", nullable = false, length = 50)
    @LastModifiedBy
    private String operator;

    @Size(max = 50)
    @NotNull
    @Column(name = "OperatorName", nullable = false, length = 50)
    private String operatorName;

    @Size(max = 50)
    @NotNull
    @Column(name = "CreateUserId", nullable = false, length = 50)
    @CreatedBy
    private String createUserId;

    @Size(max = 50)
    @NotNull
    @Column(name = "CreateUserName", nullable = false, length = 50)
    private String createUserName;

    @NotNull
    @Column(name = "CreateDate", nullable = false)
    @CreatedDate
    private LocalDateTime createDate;

    @NotNull
    @Column(name = "Operatedate", nullable = false)
    @LastModifiedDate
    private LocalDateTime operateDate;

    @Size(max = 200)
    @Column(name = "Note", length = 200)
    private String note;

    @Size(max = 200)
    @Column(name = "ZhaiYao", length = 200)
    private String zhaiYao;

    public boolean isEnabled() {
        return "1".equals(this.isActive);
    }

}