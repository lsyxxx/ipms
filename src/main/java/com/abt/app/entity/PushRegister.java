package com.abt.app.entity;

import com.abt.common.config.ValidateGroup;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 推送注册的id对应的用户
 */
@Getter
@Setter
@Entity
@Table(name = "jpush_register")
public class PushRegister {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name="user_name", length = 32)
    private String username;

    @NotNull(message = "userid不能为空!", groups = {ValidateGroup.All.class})
    @Column(name="user_id", length = 128)
    private String userid;

    /**
     * 注册id
     */
    @NotNull(groups = {ValidateGroup.All.class}, message = "registerId不能为空")
    @Column(name="reg_id", length = 128)
    private String registerId;

    /**
     * 极光推送
     */
    public static final String PUSH_PLATFORM_JPUSH = "jpush";

    /**
     * 推送平台，如极光推送
     */
    @Column(name="push_platform", length = 128)
    private String pushPlatform = PUSH_PLATFORM_JPUSH;
    public static final String APP_PLATFORM_ANDROID = "android";
    /**
     * 鸿蒙
     */
    public static final String APP_PLATFORM_HARMONY = "harmony";
    public static final String APP_PLATFORM_IOS = "ios";
    @Column(name="app_platform", length = 128)
    private String appPlatform = APP_PLATFORM_ANDROID;


}