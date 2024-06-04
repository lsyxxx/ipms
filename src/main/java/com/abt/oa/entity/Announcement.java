package com.abt.oa.entity;

import com.abt.common.service.impl.CommonJpaAudit;
import com.abt.oa.OAConstants;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "T_announcement")
@DynamicInsert
@DynamicUpdate
public class Announcement implements CommonJpaAudit {
    @Id
    @Size(max = 50)
    @Column(name = "Id", nullable = false, length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Size(max = 50)
    @NotNull
    @Column(name = "Title", nullable = false, length = 50)
    private String title;

    @Lob
    @Column(name = "FilePath")
    private String filePath;

    @Lob
    @Column(name = "Note")
    private String note;

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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ssd")
    private LocalDateTime createDate;

    @Size(max = 50)
    @NotNull
    @Column(name = "Operator", nullable = false, length = 50)
    @LastModifiedBy
    private String operator;

    @NotNull
    @Column(name = "Operatedate", nullable = false)
    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ssd")
    private LocalDateTime operateDate;

    @Size(max = 50)
    @NotNull
    @Column(name = "OperatorName", nullable = false, length = 50)
    private String operatorName;

    @Size(max = 3)
    @Column(name = "FileType", length = 3)
    private String fileType;

    /**
     * OAConstants.ANNOUNCEMENT_ZDTYPE
     */
    @Size(max = 50)
    @Column(name = "ZdType", length = 50)
    private String zdType;

    @Lob
    @Column(name = "UserOrDept")
    private String userOrDept;

    @Lob
    @Column(name = "nodeDesignates")
    private String nodeDesignates;

    @Lob
    @Column(name = "nodeDesignateTxts")
    private String nodeDesignateTxts;

    @Size(max = 2)
    @Column(name = "isHf", length = 2)
    private String isHf;

    /**
     * 0: 草稿
     * 1: 发布, 发布撤销后是草稿
     * 2: 删除
     */
    @ColumnDefault("0")
    @Column(name = "Status", nullable = false, columnDefinition = "tinyint")
    private int status;

    @Column(name = "PublishTime")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ssd")
    private LocalDateTime publishTime;

    @Column(name="PublishUser")
    private String publishUser;

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
        return this.operator;
    }

    @Override
    public void setUpdateUsername(String username) {
        this.operatorName = username;
    }

    public void doPublish(String publishUser) {
        this.status = OAConstants.ANNOUNCEMENT_STATUS_PUBLISH;
        this.publishTime = LocalDateTime.now();
        this.publishUser = publishUser;
    }

    public void doTemp(){
        this.status = OAConstants.ANNOUNCEMENT_STATUS_TEMP;
        this.publishTime = null;
        this.publishUser = null;
    }

}