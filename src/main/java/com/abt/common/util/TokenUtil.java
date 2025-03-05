package com.abt.common.util;

import com.abt.common.model.IToken;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.exception.InvalidTokenException;
import com.abt.sys.model.dto.UserView;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 */
@Slf4j
public class TokenUtil {

    /**
     * 从Authentication获取认证的用户信息
     * 无法获取token或用户则抛出InvalidTokenException
     * @return UserView
     */
    public static UserView getUserFromAuthToken() {
        Authentication token = SecurityContextHolder.getContext().getAuthentication();
        if (token == null || token.getPrincipal() == null) {
            log.error("未能从Authentication中获取token/用户!");
            throw new InvalidTokenException(MessageUtil.getAccessor().getMessage("ex.token.invalid.common"));
        }
        UserView user = (UserView) token.getPrincipal();
        log.info("从认证的token中获取user - {}", user.simpleInfo());
        return (UserView) token.getPrincipal();
    }

    public static String getUseridFromAuthToken() {
        return getUserFromAuthToken().getId();
    }


    public static String getUserJobNumberFromAuthToken() {
        return getUserFromAuthToken().getEmpnum();
    }

    /**
     * 获取token value
     * @return 如果没有认证则返回空字符串
     *         如果没有token值，则返回空字符串
     */
    public static String getToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            log.warn("没有认证信息!");
            return "";
        }
        Object token = auth.getCredentials();
        if (token == null) {
            return "";
        }
        return (String) token;
    }

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static void checkFormToken(HttpSession session, String service, String reqToken) {
        if (session == null) {
            throw new BusinessException("session超时，请刷新后再次尝试");
        }
        if (StringUtils.isBlank(service)) {
            throw new BusinessException("未传入service名称");
        }
        final Object attribute = session.getAttribute(service);
        if (attribute == null) {
            throw new BusinessException(String.format("Token无效!(service=%s)", service));
        }
        String token = (String) attribute;
        if (token.isEmpty() || StringUtils.isBlank(reqToken) || !reqToken.equals(token)) {
            log.warn("token无效! user: {}, service: {}", TokenUtil.getUseridFromAuthToken(), service);
            throw new InvalidTokenException(String.format("Token无效，请刷新后重试!(service:%s)", service));
        }
    }

    public static void removeToken(HttpSession session, String service) {
        if (session == null) {
            return;
        }
        if (StringUtils.isBlank(service)) {
            return;
        }
        session.removeAttribute(service);
    }

}
