package com.abt.finance.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import java.time.LocalDateTime;

/**
 * 回款通知。暂时不保存数据库
 */
//@Table(name = "fi_rec_payment_notify",
//        indexes = {@Index(name = "idx_userid", columnList = "userid"),})
//@NamedEntityGraphs({
//        @NamedEntityGraph(
//                name = "ReceivePaymentNotify.withReceivePayment",
//                attributeNodes = @NamedAttributeNode("receivePayment")
//        ),
//})
//@Entity
//@DynamicUpdate
//@DynamicInsert
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReceivePaymentNotify {

//    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 通知渠道
     */
//    @Column(name="channel_")
    private String channel = CHANNEL_ALL;

    public static final String CHANNEL_ALL = "all";
    public static final String CHANNEL_ANDROID = "android";
    public static final String CHANNEL_HMOS = "hmos";
    public static final String CHANNEL_IOS = "ios";
    /**
     * 电脑端通知
     */
    public static final String CHANNEL_WEB = "web";

    /**
     * 通知用户
     */
//    @Column(name="user_id")
    private String userid;
//    @Column(name="user_name")
    private String username;

    public static final String THIRD_PARTY_JPUSH = "jpush";
    /**
     * 第三方
     */
//    @Column(name="third_party")
    private String thirdParty;

    /**
     * 第三方推送id, 如jpush reg_id
     */
    @Column(name="push_reg_id")
    private String pushRegId;

    /**
     * 是否已读
     */
    private boolean read;

    /**
     * 查看时间
     */
    private LocalDateTime readTime;

    @JsonIgnore
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "id", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), insertable=false, updatable=false)
//    @NotFound(action= NotFoundAction.IGNORE)
    private ReceivePayment receivePayment;

    /**
     * 简要说明
     */
//    @Column(name="desc_")
    private String desc;


    public static ReceivePaymentNotify createJpushAll(String userid, String username, String desc, String pushRegId) {
        ReceivePaymentNotify notify = new ReceivePaymentNotify();
        notify.setUserid(userid);
        notify.setUsername(username);
        notify.setChannel(CHANNEL_ALL);
        notify.setThirdParty(THIRD_PARTY_JPUSH);
        notify.setDesc(desc);
        notify.setPushRegId(pushRegId);
        return notify;
    }

}
