package com.abt.wxapp.login.service;

import com.abt.wxapp.exception.BusinessException;
import com.abt.wxapp.security.JwtUtil;
import com.abt.wxapp.security.WxUserDetails;
import com.abt.wxapp.wxapi.service.WxApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 登录服务
 */
@Service
public interface LoginService {

    /**
     * 微信账号登录
     * @param code wx.login返回code
     */
    void wxLogin(String code);
}
