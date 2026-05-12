package com.abt.wxapp.user.client.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 委托人保存请求
 */
@Getter
@Setter
public class ClientRequest {

    private String id;

    @NotBlank(message = "关联用户ID不能为空")
    private String userId;

    @NotBlank(message = "委托人名称不能为空")
    private String clientName;

    private String contactName;

    private String contactPhone;

    private String contactEmail;

    private Boolean isDefault;
}
