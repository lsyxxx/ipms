package com.abt.common.config;

import com.abt.common.model.User;
import com.abt.common.service.CommonJpaAudit;
import com.abt.common.util.TokenUtil;
import com.abt.sys.repository.UserRepository;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * jpa通用更新，创建时添加createUsername，
 * 需要实现CommonJpaAudit接口
 */
@Slf4j
public class CommonJpaAuditListener {

    private final UserRepository userRepository;

    public CommonJpaAuditListener(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 创建实体前
     */
    @PrePersist
    public <T extends CommonJpaAudit> void beforePersist(T entity) {
        String createUserid = entity.getCreateUserid();
        if (StringUtils.isNotBlank(createUserid)) {
//            final User user = userRepository.getSimpleUserInfo(createUserid);
//            if (user != null) {
//                entity.setCreateUsername(user.getUsername());
//            }
            entity.setCreateUsername(TokenUtil.getUserFromAuthToken().getUsername());
        }
        String updateUserid = entity.getUpdateUserid();
        if (StringUtils.isNotBlank(updateUserid)) {
//            final User user = userRepository.getSimpleUserInfo(updateUserid);
//            if (user != null) {
//                entity.setUpdateUsername(user.getUsername());
//            }
            entity.setUpdateUsername(TokenUtil.getUserFromAuthToken().getUsername());
        }
    }

    /**
     * 更新前，添加更新人名称
     */
    @PreUpdate
    public <T extends CommonJpaAudit> void beforeUpdate(T entity) {
        String updateUserid = entity.getUpdateUserid();
        if (StringUtils.isNotBlank(updateUserid)) {
//            final User user = userRepository.getSimpleUserInfo(updateUserid);
//            if (user != null) {
//                entity.setUpdateUsername(user.getUsername());
//            }
            entity.setUpdateUsername(TokenUtil.getUserFromAuthToken().getUsername());
        }
    }


}
