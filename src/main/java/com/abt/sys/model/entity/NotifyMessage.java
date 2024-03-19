package com.abt.sys.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * 抄送/通知 对象
 */
@Data
@NoArgsConstructor
@Table(name = "sys_msg")
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
     * 系统消息
     */
    public static final String SYSTEM = "System";

    public static NotifyMessage systemMessage(String to) {
        NotifyMessage msg = new NotifyMessage();
        msg.setTo(to);
        return msg;
    }

}
