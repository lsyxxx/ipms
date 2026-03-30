package com.abt.wxapp.user.invoice.entity;

import com.abt.common.config.ValidateGroup;
import com.abt.wxapp.db.AuditInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@Entity
@Table(name = "open_user_inv_title")
@DynamicUpdate
@JsonInclude(JsonInclude.Include.NON_NULL)
@EntityListeners({AuditingEntityListener.class})
public class OpenUserInvTitle extends AuditInfo {

    @Id
    @Column(name = "id", length = 50, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotNull(message = "关联用户ID不能为空", groups = {ValidateGroup.Save.class})
    @Column(name = "userid", length = 50)
    private String userId;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "title_type", columnDefinition = "tinyint COMMENT '0-单位, 1-个人'")
    private TitleTypeEnum titleType;

    @NotNull(message = "单位名称不能为空", groups = {ValidateGroup.Save.class})
    @Column(name = "company_name", length = 100)
    private String companyName;

    @NotNull(message = "税号不能为空", groups = {ValidateGroup.Save.class})
    @Column(name = "tax_no", length = 50)
    private String taxNo;

    @Column(name = "com_addr", length = 200)
    private String companyAddress;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "bank", length = 100)
    private String bank;

    @Column(name = "account", length = 50)
    private String account;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;
}