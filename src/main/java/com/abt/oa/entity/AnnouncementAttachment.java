package com.abt.oa.entity;

import com.abt.common.config.CommonJpaAuditListener;
import com.abt.common.service.impl.CommonJpaAudit;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
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

import java.time.LocalDateTime;

import static com.abt.oa.OAConstants.*;

@Getter
@Setter
@Entity
@Table(name = "T_announcement_attachment", indexes = {
        @Index(name = "idx_announcementId", columnList = "announcementId"),
        @Index(name = "idx_apply_user_id", columnList = "ApplyUserID, announcementId")
})
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners({AuditingEntityListener.class, CommonJpaAuditListener.class})
public class AnnouncementAttachment implements CommonJpaAudit {
    @Id
    @Size(max = 50)
    @Column(name = "Id", nullable = false, length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
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

    /**
     * 是否已读
     */
    @Size(max = 2)
    @NotNull
    @Column(name = "IsSer", nullable = false, length = 2)
    private String isSer = ANNOUNCEMENT_ATTACHMENT_UNREAD;

    @Lob
    @Column(name = "Note")
    private String note;

    @CreatedBy
    @Size(max = 50)
    @NotNull
    @Column(name = "CreateUserId", nullable = false, length = 50)
    private String createUserId;

    @Size(max = 50)
    @NotNull
    @Column(name = "CreateUserName", nullable = false, length = 50)
    private String createUserName;

    @CreatedDate
    @NotNull
    @Column(name = "CreateDate", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ssd")
    private LocalDateTime createDate;

    @LastModifiedBy
    @Size(max = 50)
    @NotNull
    @Column(name = "Operator", nullable = false, length = 50)
    private String operator;

    @LastModifiedDate
    @NotNull
    @Column(name = "Operatedate", nullable = false)
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

    @Column(name = "ReadDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ssd")
    private LocalDateTime readDate;

    @Size(max = 2)
    @Column(name = "isHf", length = 2)
    private String isHf = ANNOUNCEMENT_ATTACHMENT_UNHF;

    @Size(max = 2000)
    @Column(name = "Hfcontent", length = 2000)
    private String hfContent;

    public static final String TO_ALL_NAME = "all";
    public static final String TO_ALL_ID = "all";


    @ManyToOne
    @JoinColumn(name = "announcementId", referencedColumnName = "Id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), insertable = false, updatable = false)
    private Announcement announcement;


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

    public boolean isRead() {
        return ANNOUNCEMENT_ATTACHMENT_READ.equals(isSer);
    }

    public boolean isHf() {
        return ANNOUNCEMENT_ATTACHMENT_HF.equals(isHf);
    }

    /**
     * 创建实例，不要使用构造器
     * @param announcement 通知
     */
    public static AnnouncementAttachment create(Announcement announcement, String replyUserid, String replyUsername) {
        AnnouncementAttachment attachment = new AnnouncementAttachment();
        attachment.announcementId = announcement.getId();
        attachment.title = announcement.getTitle();
        attachment.filePath = announcement.getFilePath();
        attachment.note = announcement.getNote();
        attachment.applyUserID = replyUserid;
        attachment.applyUserName = replyUsername;
        attachment.setFileType(announcement.getFileType());
        return attachment;
    }
}