package com.abt.wxapp.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * 安全上下文工具类
 * <p>在 Controller / Service 中通过静态方法获取当前登录用户信息。</p>
 *
 * <pre>{@code
 * // 获取当前用户 openid
 * String openid = SecurityUtil.currentOpenid().orElseThrow();
 *
 * // 获取完整用户信息
 * WxUserDetails user = SecurityUtil.currentUser().orElseThrow();
 * }</pre>
 */
public final class SecurityUtil {

    private SecurityUtil() {}

    /**
     * 获取当前登录用户，未登录时返回 {@link Optional#empty()}
     */
    public static Optional<WxUserDetails> currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof WxUserDetails userDetails) {
            return Optional.of(userDetails);
        }
        return Optional.empty();
    }

    /**
     * 获取当前用户的 openid，未登录时返回 {@link Optional#empty()}
     */
    public static Optional<String> currentOpenid() {
        return currentUser().map(WxUserDetails::getOpenid);
    }

    /**
     * 获取当前用户的 userId，未登录时返回 {@link Optional#empty()}
     */
    public static Optional<String> currentUserId() {
        return currentUser().map(WxUserDetails::getUserId);
    }
}
