package com.abt.wxapp.wxapi.service;


/**
 * 调用微信相关接口
 */
public interface WxApiService {


    /**
     * 根据wx.login api及appid+secret获取用户session_key+openid
     * 官方api: https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/login.html
     * @param code wx.login中返回的用户code
     */
    String getUserOpenid(String code);

}
