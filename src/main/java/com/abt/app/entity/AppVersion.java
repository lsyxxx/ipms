package com.abt.app.entity;

import com.abt.common.config.CommonJpaAuditListener;
import com.abt.common.model.AuditInfo;
import com.abt.common.service.InsertJpaUser;
import com.abt.common.service.UserJpaAudit;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 手机版本
 */

@Getter
@Setter
@Entity
@Table(name = "app_ver")
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@JsonInclude(JsonInclude.Include.NON_NULL)
@EntityListeners({InsertJpaUser.class})
public class AppVersion extends AuditInfo implements UserJpaAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 版本号：1.0.x格式
     */
    @NotNull
    @Column(name="ver_", columnDefinition = "VARCHAR(128)")
    private String version;

    /**
     * 移动平台，如安卓，ios, 平板
     */
    @NotNull
    @Column(name="platform", columnDefinition = "VARCHAR(128)")
    private String platform = PLATFORM_ANDROID_APP;

    /**
     * 安卓手机app
     */
    public static final String PLATFORM_ANDROID_APP = "Android_app";

    /**
     * ios app
     */
    public static final String FLATFORM_IOS_APP = "iOS_app";
    /**
     * ipad
     */
    public static final String FLATFORM_IOS_PAD = "iOS_pad";

    /**
     * 安卓pad
     */
    public static final String FLATFORM_ANDROID_PAD = "Android_pad";

    /**
     * 备份地址
     */
    @Column(name="url_", columnDefinition = "VARCHAR(500)")
    private String url;

    /**
     * 保存的文件名
     */
    @Column(name="file_name", columnDefinition = "VARCHAR(128)")
    private String fileName;

    /**
     * 完整地址
     */
    @Column(name="full_url", columnDefinition = "VARCHAR(500)")
    private String fullUrl;


    /**
     * 说明/change Log
     */
    @Column(name="desc_", columnDefinition = "VARCHAR(1000)")
    private String description;

    /**
     * 当前正在运行的版本
     */
    @Column(name="is_run", columnDefinition = "BIT")
    private boolean isRun = false;

    public void run() {
        this.isRun = true;
    }



}