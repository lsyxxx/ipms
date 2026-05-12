package com.abt.wxapp.user.invoice.model;

import com.abt.wxapp.user.invoice.entity.TitleTypeEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 发票抬头保存请求
 */
@Getter
@Setter
public class InvTitleRequest {

    private String id;

    @NotBlank(message = "关联用户ID不能为空")
    private String userId;

    private TitleTypeEnum titleType;

    @NotBlank(message = "单位名称不能为空")
    private String companyName;

    @NotBlank(message = "税号不能为空")
    private String taxNo;

    private String companyAddress;

    private String phone;

    private String bank;

    private String account;

    private Boolean isDefault;
}
