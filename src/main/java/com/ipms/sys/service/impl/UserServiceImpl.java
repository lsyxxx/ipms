package com.ipms.sys.service.impl;

import com.ipms.sys.model.dto.UserView;
import com.ipms.sys.service.HttpConnectService;
import com.ipms.sys.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 从webapi 获取user
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService<UserView, String> {
    public final static String TOKEN_KEY = "X-Token";

    public final HttpConnectService httpConnectService;


    @Override
    public String getToken(HttpServletRequest request) {
        return request.getHeader(TOKEN_KEY);
    }


    /**
     * 通过http 从webapi获取user info
     * @return
     */
    @Override
    public Optional<UserView> userInfoBy(String token) {
        //TODO: 从webapi获取user
        String requestUrl = httpConnectService.createUrlWithToken(token);
        UserView user = (UserView) httpConnectService.doConnect(requestUrl);
        return Optional.of(user);
    }


}
