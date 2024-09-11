package com.abt.common.service;

import com.abt.common.util.TokenUtil;
import com.abt.sys.model.dto.UserView;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.extern.slf4j.Slf4j;

/**
 * jpa监听使用session中用户信息
 */
@Slf4j
public class insertJpaUser {

    @PrePersist
    public <T extends UserJpaAudit> void insertUser(T entity) {
        UserView user = TokenUtil.getUserFromAuthToken();
        if (user == null) {
            log.warn("Session中无用户信息!");
            return;
        }
        String createUserid = entity.getCreateUserid();
        entity.setCreateUsername(user.getName());
    }

    @PreUpdate
    public <T extends UserJpaAudit> void updateUser(T entity) {
        UserView user = TokenUtil.getUserFromAuthToken();
        if (user == null) {
            log.warn("Session中无用户信息!");
            return;
        }
        String updateUserid = entity.getUpdateUserid();
        entity.setUpdateUsername(user.getName());
    }



}
