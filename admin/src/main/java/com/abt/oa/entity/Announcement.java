package com.abt.oa.entity;

import com.abt.common.config.CommonJpaAuditListener;
import com.abt.common.config.ValidateGroup;
import com.abt.common.service.CommonJpaAudit;
import com.abt.oa.OAConstants;
import com.abt.sys.model.entity.Org;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "T_announcement")
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@EntityListeners({AuditingEntityListener.class, CommonJpaAuditListener.class})
public class Announcement implements CommonJpaAudit {
    @Id
    @Size(max = 50)
    @Column(name = "Id", nullable = false, length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Size(max = 50)
    @NotNull(message = "标题不能为空", groups = {ValidateGroup.Save.class})
    @Column(name = "Title", nullable = false, length = 50)
    private String title;

    @Lob
    @Column(name = "FilePath")
    private String filePath;

    @Lob
    @Column(name = "Note")
    private String note;

    @Size(max = 50)
    @Column(name = "CreateUserId", length = 50)
    @CreatedBy
    private String createUserId;

    @Size(max = 50)
    @Column(name = "CreateUserName", length = 50)
    private String createUserName;

    @Column(name = "CreateDate")
    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ssd")
    private LocalDateTime createDate;

    @Size(max = 50)
    @Column(name = "Operator", nullable = false, length = 50)
    @LastModifiedBy
    private String operator;

    @Column(name = "Operatedate")
    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ssd")
    private LocalDateTime operateDate;

    @Size(max = 50)
    @Column(name = "OperatorName", length = 50)
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

    /**
     * 通知对象显示名称
     */
    @Lob
    @Column(name = "UserOrDept")
    private String userOrDept;

    /**
     * 通知对象部门，数组字符串
     */
    @Lob
    @Column(name = "orgs")
    private String orgs;

    /**
     * 通知对象的部门名称，数组字符串
     */
    @Lob
    @Column(name="org_names")
    private String orgNames;

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
    public boolean isTemp() {
        return this.status == OAConstants.ANNOUNCEMENT_STATUS_TEMP;
    }
}