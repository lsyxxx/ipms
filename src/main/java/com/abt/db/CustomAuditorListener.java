package com.abt.db;

import com.abt.common.util.TokenUtil;
import com.abt.sys.model.dto.UserView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * jpa自动审计
 */
@Component
@Slf4j
public class CustomAuditorListener implements AuditorAware<String> {


    @NonNull
    @Override
    public Optional<String> getCurrentAuditor() {
        log.info("CustomAuditorListener....");
        try {
            UserView user = TokenUtil.getUserFromAuthToken();
            if (user != null) {
                return Optional.of(user.getId());
            }
        } catch (Exception e) {
            log.error("未获取用户信息! -- 错误信息：{}, 不添加审计用户信息", e.getMessage());
        }

        return Optional.empty();
    }
}
