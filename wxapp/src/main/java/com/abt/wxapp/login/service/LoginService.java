package com.abt.wxapp.login.service;

/**
 * 登录服务
 */
public interface LoginService {

    /**
     * 微信账号登录：按 openid 查找或创建开放用户，返回用于签发 JWT 的主体
     *
     * @param code wx.login 返回的 code
     */
    String wxLogin(String code);
}
