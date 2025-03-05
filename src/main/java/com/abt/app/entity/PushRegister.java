package com.abt.app.entity;

import com.abt.common.config.ValidateGroup;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 推送注册的id对应的用户
 * 一个用户可能有多个rid
 * 一个rid只能对应一个用户
 */
@Getter
@Setter
@Entity
@Table(name = "push_register")
@NoArgsConstructor
@AllArgsConstructor
public class PushRegister {

    /**
     * 注册id，约等于设备唯一
     */
    @Id
    @NotNull(groups = {ValidateGroup.All.class}, message = "registerId不能为空")
    @Column(name="reg_id", length = 128)
    private String registerId;

    @NotNull(message = "userid不能为空!", groups = {ValidateGroup.All.class})
    @Column(name="user_id", length = 128)
    private String userid;



    @Column(name="user_name", length = 32)
    private String username;


    /**
     * 推送平台，如极光推送
     */
    @Column(name="push_platform", length = 128)
    private String pushPlatform = PUSH_PLATFORM_JPUSH;

    @Column(name="app_platform", length = 128)
    private String appPlatform = APP_PLATFORM_ANDROID;

    /**
     * 极光推送
     */
    public static final String PUSH_PLATFORM_JPUSH = "jpush";
    public static final String APP_PLATFORM_ANDROID = "android";
    /**
     * 鸿蒙
     */
    public static final String APP_PLATFORM_HARMONY = "harmony";
    public static final String APP_PLATFORM_IOS = "ios";


}