package com.abt.oa.entity;

import com.abt.common.service.impl.CommonJpaAudit;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "T_announcement_attachment")
@DynamicUpdate
@DynamicInsert
public class AnnouncementAttachment implements CommonJpaAudit {
    @Id
    @Size(max = 50)
    @Column(name = "Id", nullable = false, length = 50)
    private String id;

    @Size(max = 50)
    @NotNull
    @Column(name = "announcementId", nullable = false, length = 50)
    private String announcementId;

    @Size(max = 50)
    @NotNull
    @Column(name = "Title", nullable = false, length = 50)
    private String title;

    @Lob
    @Column(name = "FilePath")
    private String filePath;

    @Size(max = 50)
    @NotNull
    @Column(name = "ApplyUserName", nullable = false, length = 50)
    private String applyUserName;

    @Size(max = 50)
    @NotNull
    @Column(name = "ApplyUserID", nullable = false, length = 50)
    private String applyUserID;

    @Size(max = 2)
    @NotNull
    @Column(name = "IsSer", nullable = false, length = 2)
    private String isSer;

    @Lob
    @Column(name = "Note")
    private String note;

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
    private LocalDateTime operateDate;

    @Size(max = 50)
    @NotNull
    @Column(name = "OperatorName", nullable = false, length = 50)
    private String operatorName;

    @Size(max = 3)
    @Column(name = "FileType", length = 3)
    private String fileType;

    @Column(name = "ReadDate")
    private LocalDateTime readDate;

    @Size(max = 2)
    @Column(name = "isHf", length = 2)
    private String isHf;

    @Size(max = 2000)
    @Column(name = "Hfcontent", length = 2000)
    private String hfContent;

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
}