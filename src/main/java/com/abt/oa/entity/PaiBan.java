package com.abt.oa.entity;

import com.abt.sys.model.WithQuery;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 排班管理
 */
@Getter
@Setter
@Entity
@Table(name = "T_paiban")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Immutable
public class PaiBan implements WithQuery<PaiBan> {
    @Id
    @Size(max = 50)
    @Column(name = "Id", nullable = false, length = 50)
    private String id;

    @NotNull
    @Column(name = "Paibandate", nullable = false)
    private LocalDate paibandate;

    @Size(max = 2)
    @NotNull
    @Column(name = "paibanType", nullable = false, length = 2)
    private String paibanType;

    @Size(max = 20)
    @NotNull
    @Column(name = "xingqi", nullable = false, length = 20)
    private String xingqi;

    @Size(max = 50)
    @NotNull
    @Column(name = "CreateUserId", nullable = false, length = 50)
    private String createUserId;

    @Size(max = 50)
    @NotNull
    @Column(name = "CreateUserName", nullable = false, length = 50)
    private String createUserName;

    @NotNull
    @Column(name = "CreateDate", nullable = false)
    private LocalDateTime createDate;

    @Size(max = 50)
    @NotNull
    @Column(name = "Operator", nullable = false, length = 50)
    private String operator;

    @NotNull
    @Column(name = "Operatedate", nullable = false)
    private LocalDateTime operatedate;

    @Size(max = 50)
    @NotNull
    @Column(name = "OperatorName", nullable = false, length = 50)
    private String operatorName;


    @Transient
    private String typeName;

    @Override
    public PaiBan afterQuery() {
        this.translateTypeName();
        return this;
    }

    /**
     * 正常工作日
     */
    public static final String TYPE_WORK = "1";
    public static final String TYPE_WORK_NAME = "正常";
    /**
     * 公休
     */
    public static final String TYPE_REST = "2";
    public static final String TYPE_REST_NAME = "公休";

    /**
     * 法定
     */
    public static final String TYPE_OFFICIAL = "3";
    public static final String TYPE_OFFICIAL_NAME = "法定";

    public String translateTypeName() {
        return switch (this.paibanType) {
            case TYPE_WORK -> TYPE_WORK_NAME;
            case TYPE_REST -> TYPE_REST_NAME;
            case TYPE_OFFICIAL -> TYPE_OFFICIAL_NAME;
            default -> "未知的排班类型(" + this.paibanType + ")";
        };
    }

}