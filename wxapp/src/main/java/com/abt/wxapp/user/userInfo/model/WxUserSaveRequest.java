package com.abt.wxapp.user.userInfo.model;

import com.abt.chkmodule.model.ChannelEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 微信小程序用户信息保存请求
 */
@Getter
@Setter
public class WxUserSaveRequest {

    private String id;

    @NotBlank(message = "用户名称不能为空")
    @Size(max = 100, message = "用户名称最多输入100字")
    private String name;

    private ChannelEnum channel;

    private String openId;

    private String phone;

    @Size(max = 100, message = "地址最多输入100字")
    private String address;
}
