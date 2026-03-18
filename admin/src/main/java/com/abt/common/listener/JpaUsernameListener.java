package com.abt.common.listener;

import com.abt.common.model.AuditInfo;
import com.abt.common.util.TokenUtil;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.extern.slf4j.Slf4j;
import com.abt.sys.model.dto.UserView;

/**
 *
 */
@Slf4j
public class JpaUsernameListener {

    @PrePersist
    @PreUpdate
    public <T extends AuditInfo> void persist(T entity) {
        UserView user = TokenUtil.getUserFromAuthToken();
        if (user == null) {
            log.warn("未能从Token中获取用户信息，添加审计失败!");
            return;
        }
        entity.setCreateUsername(user.getUsername());
    }
}
