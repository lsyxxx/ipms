package com.abt.wxapp.wxapi.model;

import lombok.Data;

/**
 * 官方api返回的sessionkey及openid
 */
@Data
public class WxUserSession {

    private String sessionKey;

    private String openid;
}
