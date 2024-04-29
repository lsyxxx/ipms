package com.abt.testing.entity;

import com.abt.common.config.CommonJpaAuditListener;
import com.abt.common.config.ValidateGroup;
import com.abt.common.service.impl.CommonJpaAudit;
import com.abt.sys.model.entity.CustomerInfo;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
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
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;


/**
 * 合同，使用原有的T_Agreement。
 * 预登记合同只用id, 合同号，合同类型，合同名称
 */

@Getter
@Setter
@Entity
@Table(name = "T_Agreement")
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@EntityListeners({AuditingEntityListener.class, CommonJpaAuditListener.class})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Agreement implements CommonJpaAudit {
    @Id
    @Size(max = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "Id", nullable = false, length = 50)
    private String id;

    @NotNull(message = "合同编号不能为空", groups = {ValidateGroup.Insert.class})
    @Size(max = 20)
    @Column(name = "AgreementCode", length = 20)
    private String agreementCode;

    @Size(max = 20)
    @NotNull(message = "合同类型不能为空", groups = {ValidateGroup.Save.class})
    @Column(name = "AgreementType", nullable = false, length = 20)
    private String agreementType;
    @Transient
    private String agreementTypeName;

    @Size(max = 200)
    @NotNull(message = "合同名称不能为空", groups = {ValidateGroup.Save.class})
    @Column(name = "AgreementName", length = 200)
    private String agreementName;

    /**
     * 甲方单位id
     */
    @Size(max = 50)
    @Column(name = "JCompanyId", length = 50)
    @NotNull(message = "甲方不能为空", groups = {ValidateGroup.Save.class})
    private String jCompanyId;

    @Transient
    private String jCompanyName;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "jCompanyId")
    private CustomerInfo jCompany;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "yCompanyId")
    private CustomerInfo yCompany;


    /**
     * 乙方单位id
     */
    @Size(max = 50)
    @Column(name = "YCompanyId", length = 50)
    @NotNull(message = "乙方不能为空", groups = {ValidateGroup.Save.class})
    private String yCompanyId;

    @Transient
    private String yCompanyName;

    @Column(name = "YyPrice", precision = 18, scale = 2)
    private BigDecimal yyPrice;

    @Column(name = "JsPrice", precision = 18, scale = 2)
    private BigDecimal jsPrice;

    @Column(name = "QdPrice", precision = 18, scale = 2)
    private BigDecimal qdPrice;

    @Column(name = "WcPrice", precision = 18, scale = 2)
    private BigDecimal wcPrice;

    @Column(name = "ZbPrice", precision = 18, scale = 2)
    private BigDecimal zbPrice;

    @Column(name = "QdDate")
    private LocalDate qdDate;

    @Column(name = "SxDate")
    private LocalDate sxDate;

    @Column(name = "HtDate")
    private LocalDate htDate;

    @Column(name = "ZbjendDate")
    private LocalDate zbjendDate;

    @Size(max = 50)
    @Column(name = "Qdbm", length = 50)
    private String qdbm;

    @Size(max = 500)
    @Column(name = "Note", length = 500)
    private String note;

    @Size(max = 2)
    @Column(name = "IsQcdx", length = 2)
    private String isQcdx;

    @Size(max = 2)
    @Column(name = "Isjcxm", length = 2)
    private String isjcxm;

    @Size(max = 2)
    @Column(name = "IsJcrw", length = 2)
    private String isJcrw;

    @Size(max = 2)
    @Column(name = "IsJcff", length = 2)
    private String isJcff;

    @Size(max = 2)
    @Column(name = "IsYwqtxq", length = 2)
    private String isYwqtxq;

    @Size(max = 2)
    @Column(name = "IsJcnlRang", length = 2)
    private String isJcnlRang;

    @Size(max = 2)
    @Column(name = "IsRyNum", length = 2)
    private String isRyNum;

    @Size(max = 2)
    @Column(name = "IsSbNum", length = 2)
    private String isSbNum;

    @Size(max = 2)
    @Column(name = "IsSshj", length = 2)
    private String isSshj;

    @Size(max = 2)
    @Column(name = "IsSyhc", length = 2)
    private String isSyhc;

    @Size(max = 2)
    @Column(name = "IsZy", length = 2)
    private String isZy;

    @Size(max = 2)
    @Column(name = "IsKhzd", length = 2)
    private String isKhzd;

    @Size(max = 2)
    @Column(name = "IsSfxyxzjcff", length = 2)
    private String isSfxyxzjcff;

    @Size(max = 2)
    @Column(name = "Issfxyfbjc", length = 2)
    private String issfxyfbjc;

    @Size(max = 2)
    @Column(name = "IsKhsffbjc", length = 2)
    private String isKhsffbjc;

    @Size(max = 2)
    @Column(name = "isSfyyNote", length = 2)
    private String isSfyyNote;

    @Size(max = 100)
    @Column(name = "PsComment", length = 100)
    private String psComment;

    @Size(max = 50)
    @Column(name = "PsEmpId", length = 50)
    private String psEmpId;

    @Column(name = "Psdate")
    private LocalDate psdate;

    @Size(max = 500)
    @Column(name = "ShyjComment", length = 500)
    private String shyjComment;

    @Size(max = 100)
    @Column(name = "ShRyEmpid", length = 100)
    private String shRyEmpid;

    @Column(name = "Shdate")
    private Instant shdate;

    @Size(max = 500)
    @Column(name = "plJlun", length = 500)
    private String plJlun;

    @Size(max = 50)
    @Column(name = "PzRqz", length = 50)
    private String pzRqz;

    @Column(name = "Pzdate")
    private LocalDate pzdate;

    @Size(max = 100)
    @Column(name = "Wbyy", length = 100)
    private String wbyy;

    @Size(max = 200)
    @Column(name = "Fbproject", length = 200)
    private String fbproject;

    @Size(max = 2)
    @Column(name = "FbfIssuzhiqingk1", length = 2)
    private String fbfIssuzhiqingk1;

    @Size(max = 2)
    @Column(name = "FbfIssuzhiqingk2", length = 2)
    private String fbfIssuzhiqingk2;

    @Size(max = 2)
    @Column(name = "Fbfjcshebeifenbao", length = 2)
    private String fbfjcshebeifenbao;

    @Size(max = 2)
    @Column(name = "Fbfjcsbsjqk1", length = 2)
    private String fbfjcsbsjqk1;

    @Size(max = 2)
    @Column(name = "Fbfjcsshj", length = 2)
    private String fbfjcsshj;

    @Size(max = 2)
    @Column(name = "Fbffwzlgl", length = 2)
    private String fbffwzlgl;

    @Size(max = 2)
    @Column(name = "Fbffwzlg2", length = 2)
    private String fbffwzlg2;

    @Size(max = 2)
    @Column(name = "Fbffwzlg3", length = 2)
    private String fbffwzlg3;

    @Size(max = 2)
    @Column(name = "Fbfgltxjlyxqk1", length = 2)
    private String fbfgltxjlyxqk1;

    @Size(max = 2)
    @Column(name = "Fbfgltxjlyxqk2", length = 2)
    private String fbfgltxjlyxqk2;

    @Size(max = 2)
    @Column(name = "Fbfgltxjlyxqk3", length = 2)
    private String fbfgltxjlyxqk3;

    @Size(max = 2)
    @Column(name = "Fbfgltxjlyxqk4", length = 2)
    private String fbfgltxjlyxqk4;

    @Size(max = 200)
    @Column(name = "Fbpsyj", length = 200)
    private String fbpsyj;

    @Size(max = 50)
    @Column(name = "FbPsrqz", length = 50)
    private String fbPsrqz;

    @Column(name = "FbPsdate")
    private LocalDate fbPsdate;

    @Size(max = 200)
    @Column(name = "FbShjy", length = 200)
    private String fbShjy;

    @Size(max = 50)
    @Column(name = "FbShqz", length = 50)
    private String fbShqz;

    @Column(name = "FbShDate")
    private Instant fbShDate;

    @Size(max = 200)
    @Column(name = "Fbpzjy", length = 200)
    private String fbpzjy;

    @Size(max = 50)
    @Column(name = "Fbpzrqz", length = 50)
    private String fbpzrqz;

    @Column(name = "FbPzDate")
    private LocalDate fbPzDate;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "CreateDate", nullable = false, columnDefinition = "DATETIME")
    @CreatedDate
    private LocalDateTime createDate;

    @CreatedBy
    @Size(max = 50)
    @Column(name = "CreateUserId", length = 50)
    private String createUserId;

    @Size(max = 50)
    @Column(name = "CreateUserName", length = 50)
    private String createUserName;

    @Size(max = 50)
    @Column(name = "FlowInstanceId", length = 50)
    private String flowInstanceId;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "Operatedate", nullable = false, columnDefinition = "DATETIME")
    @LastModifiedDate
    private LocalDateTime operateDate;

    @Size(max = 50)
    @Column(name = "OperatorName", nullable = false, length = 50)
    private String operatorName;

    @Size(max = 50)
    @LastModifiedBy
    @Column(name = "Operator", nullable = false, length = 50)
    private String operator;

    @Size(max = 2)
    @Column(name = "IsFinish", length = 2)
    private String isFinish;

    @Size(max = 2)
    @Column(name = "IsType", length = 2)
    private String isType;

    @Size(max = 50)
    @Column(name = "JiaFangCompany", length = 50)
    private String jiaFangCompany;

    @Override
    public String getCreateUserid() {
        return this.createUserId;
    }

    @Override
    public void setCreateUsername(String username) {
        this.createUserName = username;
    }

    @Override
    public String getUpdateUserid() {
        return  this.operator;
    }

    @Override
    public void setUpdateUsername(String username) {
        this.operatorName = username;
    }

    @Override
    public String toString() {
        return "Agreement{" +
                "id='" + id + '\'' +
                ", agreementCode='" + agreementCode + '\'' +
                ", agreementType='" + agreementType + '\'' +
                ", agreementName='" + agreementName + '\'' +
                ", jCompanyId='" + jCompanyId + '\'' +
                ", yCompanyId='" + yCompanyId + '\'' +
                '}';
    }

}