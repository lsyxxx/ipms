package com.abt.sys.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 抄送/通知 对象
 */
@Data
@NoArgsConstructor
@Table(name = "sys_notify")
@DynamicInsert
@DynamicUpdate
@Entity
public class NotifyMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    /**
     * 接收人
     */
    @Column(name="to_", columnDefinition="VARCHAR(128)")
    private String to;

    /**
     * 表示发送给所有人
     */
    public static final String TO_ALL = "TO_ALL";

    /**
     * 发送人
     */
    @Column(name="from_", columnDefinition="VARCHAR(128)")
    private String from = SYSTEM;

    @Column(name="message_", columnDefinition="VARCHAR(1000)")
    private String message;
    /**
     * 点击跳转url
     */
    @Column(name="url_", columnDefinition="VARCHAR(128)")
    private String url;

    /**
     * 是否已读
     * true: 已读
     * false: 未读
     */
    @Column(name="is_read", columnDefinition="BIT")
    private boolean isRead = false;

    /**
     * 是否删除消息,soft delete
     */
    @Column(name="is_del", columnDefinition="BIT")
    private boolean isDelete = false;

    /**
     * 发送时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ssd")
    private LocalDateTime sendTime;

    /**
     * 消息类型：抄送等
     */
    @Column(name="type_", columnDefinition="VARCHAR(16)")
    private String type = NotifyMessageType.copy.name();
    @Column(name="type_desc", columnDefinition="VARCHAR(16)")
    private String typeDesc;
    /**
     * 服务类型
     * Constats.SERVICE_*.
     */
    @Column(name="service_", columnDefinition="VARCHAR(16)")
    private String service;


    /**
     * 系统消息
     */
    public static final String SYSTEM = "System";

    public static NotifyMessage systemMessage(String to, String link, String message) {
        NotifyMessage msg = new NotifyMessage();
        msg.setTo(to);
        msg.setUrl(link);
        msg.setMessage(message);
        msg.setSendTime(LocalDateTime.now());
        return msg;
    }

}
