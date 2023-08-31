package com.abt.sys.service.impl;

import com.abt.common.util.JsonUtil;
import com.abt.common.util.MessageUtil;
import com.abt.http.dto.WebApiDto;
import com.abt.http.dto.WebApiToken;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.exception.InvalidTokenException;
import com.abt.sys.model.dto.UserView;
import com.abt.http.service.HttpConnectService;
import com.abt.sys.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * 从webapi 获取user
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService<UserView, WebApiToken> {
    private final HttpConnectService<WebApiDto> httpConnectService;

    @Value("${webapi.http.api.userinfo}")
    private String userinfoApi;
    protected MessageSourceAccessor messages = MessageUtil.getAccessor();
    @Override
    public WebApiToken getToken(HttpServletRequest request) {
        WebApiToken token = WebApiToken.of();
        String tokenValue = request.getHeader(token.getTokenKey());
        token.setTokenValue(tokenValue);
        return token;
    }


    /**
     * 通过http 从webapi获取user info
     * @return Optional<UserView>
     */
    @Override
    public Optional<UserView> userInfoBy(WebApiToken token) {
        //1. validate parameters
        if (token.isInvalidToken()) {
            log.error("token is null! 请提供token信息");
            throw new InvalidTokenException(messages.getMessage("ex.token.invalid.common"));
        }
        log.info("webapi uri userinfo == {}", userinfoApi);
        //2. do service
        WebApiDto dto = httpConnectService.get(userinfoApi, token);
        if (dto.isFail()) {
            log.error("通过api: {} 获取用户信息失败. code={}, message={}", userinfoApi, dto.getCode(), dto.getMessage());
            throw new BusinessException(dto.getMessage(), dto.getCode());
        }
        UserView user = JsonUtil.ObjectMapper().convertValue(dto.getResult(), UserView.class);
        log.trace("Get user info from api. code={}, message={}, user={}:{}", dto.getCode(), dto.getMessage(), user.getId(), user.getName());
        return Optional.of(user);
    }


}
