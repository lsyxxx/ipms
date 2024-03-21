package com.abt.sys.service.impl;

import com.abt.common.model.User;
import com.abt.common.util.MessageUtil;
import com.abt.http.dto.WebApiToken;
import com.abt.sys.model.dto.UserView;
import com.abt.sys.repository.UserRepository;
import com.abt.sys.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 使用sqlserver数据库
 */
@Service("sqlServerUserService")
@Slf4j
public class SqlServerUserServiceImpl implements UserService<UserView, User> {

    @Value("${webapi.http.api.userinfo}")
    private String userinfoApi;
    protected MessageSourceAccessor messages = MessageUtil.getAccessor();
    private final UserRepository userRepository;

    public SqlServerUserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<UserView> userInfoBy(User user) {
        final UserView userView = userRepository.getUserBy(user.getId());
        return Optional.of(userView);
    }

    @Override
    public WebApiToken getToken(HttpServletRequest request) {
        WebApiToken token = WebApiToken.of();
        String tokenValue = request.getHeader(token.getTokenKey());
        token.setTokenValue(tokenValue);
        return token;
    }

    @Override
    public User getSimpleUserInfo(User user) {
        return userRepository.getSimpleUserInfo(user.getId());
    }


    @Override
    public List<User> getAllSimpleUser(Integer status) {
        return userRepository.getAllSimpleUser(status);
    }

}
