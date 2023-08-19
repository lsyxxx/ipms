package com.ipms.sys.service.impl;

import com.ipms.sys.exception.InvalidTokenException;
import com.ipms.sys.model.dto.AuthUserDetails;
import com.ipms.sys.model.dto.UserView;
import com.ipms.sys.service.TokenService;
import com.ipms.sys.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Optional;

/**
 * 从webapi 获取user
 */
@Slf4j
@Service
public class UserFromWebApiServiceImpl implements UserService<UserView, String>, TokenService<String>, UserDetailsService {
    public final static String TOKEN_KEY = "X-Token";

    @Override
    public String getToken(HttpServletRequest request) {
        return request.getParameter(TOKEN_KEY);
    }


    /**
     * 通过http 从webapi获取user info
     * @return
     */
    @Override
    public Optional<UserView> userInfoBy(String token) {
        UserView user = new UserView();

        //TODO

        return Optional.of(user);
    }


    /**
     *
     */
    @Override
    public UserDetails loadUserByUsername(String token) throws UsernameNotFoundException {
        if (token == null) {
            throw new InvalidTokenException("Token is null!");
        }
        Optional<UserView> oUser = userInfoBy(token);
        if (!oUser.isPresent()) {
            throw new InvalidTokenException("Cannot find user by token: " + token);
        }
        UserView user = oUser.get();
        UserDetails ud = new AuthUserDetails<UserView>(user, user.getPassword(), user.getName());
        return ud;
    }
}
