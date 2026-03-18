package com.abt.sys.model.entity;

import com.abt.common.model.AuditInfo;
import com.abt.sys.config.SystemConstants;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Nationalized;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.abt.sys.config.SystemConstants.SYSMSG_TYPE_NAME_IMPORTANT;
import static com.abt.sys.config.SystemConstants.SYSMSG_TYPE_NAME_TIP;
import static com.abt.wf.config.Constants.DECISION_PASS;
import static com.abt.wf.config.Constants.DECISION_REJECT;


/**
 * 原有的SystemMessage
 */

@Getter
@Setter
@Entity
@DynamicInsert
@DynamicUpdate
@ToString
@Table(name = "SysMessage")
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners({AuditingEntityListener.class})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SystemMessage {
    @Id
    @Column(name = "Id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Size(max = 20)
    @Nationalized
    @Column(name = "TypeName", length = 20)
    private String typeName;

    @Column(name = "TypeId")
    private String typeId;

    @Column(name = "FromId")
    private String fromId;

    @NotNull
    @Column(name = "ToId", nullable = false)
    private String toId;

    @Size(max = 50)
    @Nationalized
    @Column(name = "FromName", length = 50)
    private String fromName;

    @Size(max = 50)
    @Nationalized
    @Column(name = "ToName", length = 50)
    private String toName;

    /**
     * 0:默认
     */
    @NotNull
    @ColumnDefault("0")
    @Column(name = "FromStatus", nullable = false)
    private Integer fromStatus = SystemConstants.FROM_STATUS_DEFAULT;

    /**
     * 9:已删除；0:默认未读；1：已读
     */
    @NotNull
    @ColumnDefault("0")
    @Column(name = "ToStatus", nullable = false)
    private Integer toStatus = SystemConstants.STATUS_UNREAD;



    @Size(max = 200)
    @Column(name = "Href", length = 200)
    private String href;

    @Size(max = 200)
    @Nationalized
    @Column(name = "Title", length = 200)
    private String title;

    @Size(max = 1000)
    @Nationalized
    @Column(name = "Content", length = 1000)
    private String content;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ssd")
    @ColumnDefault("getdate()")
    @Column(name = "CreateTime", nullable = false)
    private LocalDateTime createTime;

    @CreatedBy
    @Column(name = "CreateId")
    private String createId;

    /**
     * 读取时间，只记录第一次读的
     */
    @Column(name="ReadTime")
    private LocalDateTime readTime;

    @LastModifiedBy
    @Column(name="UpdateUserid")
    private String updateUserid;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ssd")
    @LastModifiedDate
    private LocalDateTime updateTime;

    /**
     * 服务名称
     */
    @Column(name="Service")
    private String service;

    @Column(name="MsgResult")
    private String msgResult;

    /**
     * 单据编号/业务实体id
     */
    @Column(name="entity_id")
    private String entityId;

    /**
     * 消息重要程度
     * 数字越大，越重要重要
     */
    @Column(name="priority_", columnDefinition = "SMALLINT")
    private int priority = 0;

    public static final int IMPORTANT = 999;

    /**
     * 重要信息
     */
    public void setImportantPriority() {
        this.priority = IMPORTANT;
    }

    //抄送
    public static final String MSG_RESULT_COPY = "copy";
    //审批通过
    public static final String MSG_RESULT_PASS = DECISION_PASS;
    //审批拒绝
    public static final String MSG_RESULT_REJECT = DECISION_REJECT;

    /**
     * 接收用户已删除
     */
    public void doDelete() {
        this.toStatus = SystemConstants.STATUS_DEL;
    }

    /**
     * 接收用户已读
     */
    public void doRead() {
        this.toStatus = SystemConstants.STATUS_READ;
        this.readTime = LocalDateTime.now();
    }


}