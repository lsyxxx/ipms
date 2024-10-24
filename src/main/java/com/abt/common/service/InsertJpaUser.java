package com.abt.common.service;

import com.abt.common.util.TokenUtil;
import com.abt.sys.exception.InvalidTokenException;
import com.abt.sys.model.dto.UserView;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.extern.slf4j.Slf4j;

/**
 * jpa监听使用session中用户信息
 */
@Slf4j
public class InsertJpaUser {

    @PrePersist
    public <T extends UserJpaAudit> void insertUser(T entity) {
        try {
            UserView user = TokenUtil.getUserFromAuthToken();
            if (user == null) {
                log.warn("Session中无用户信息!");
                return;
            }
            String createUserid = entity.getCreateUserid();
            entity.setCreateUsername(user.getName());
            String updateUserid = entity.getUpdateUserid();
            entity.setUpdateUsername(user.getName());
        } catch (InvalidTokenException e) {
            log.error("InsertJpaUser.insertUser - 未能从Authentication中获取token/用户! 不添加审计信息");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    @PreUpdate
    public <T extends UserJpaAudit> void updateUser(T entity) {
        try {
            UserView user = TokenUtil.getUserFromAuthToken();
            if (user == null) {
                log.warn("Session中无用户信息!");
                return;
            }
            String updateUserid = entity.getUpdateUserid();
            entity.setUpdateUsername(user.getName());
        } catch (InvalidTokenException e) {
            log.error("InsertJpaUser.updateUser - 未能从Authentication中获取token/用户! 不添加审计信息");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }



}
