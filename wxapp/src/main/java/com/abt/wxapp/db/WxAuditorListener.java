package com.abt.wxapp.db;

import com.abt.wxapp.security.SecurityUtil;
import com.abt.wxapp.security.WxUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * JPA 自动审计，从 SecurityContext 中获取当前登录用户 ID
 */
@Slf4j
@Component
public class WxAuditorListener implements AuditorAware<String> {

    @NonNull
    @Override
    public Optional<String> getCurrentAuditor() {
        try {
            final Optional<WxUserDetails> opt = SecurityUtil.currentUser();
            if (opt.isPresent()) {
                return Optional.of(opt.get().getUserId());
            }
        } catch (Exception e) {
            log.error("WXAPP: 未获取用户信息! -- 错误信息：{}, 不添加审计用户信息", e.getMessage());
        }
        return Optional.empty();
    }
}
