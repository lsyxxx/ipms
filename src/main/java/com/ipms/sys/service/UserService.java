package com.ipms.sys.service;

import com.ipms.sys.mapper.UserMapper;
import com.ipms.sys.model.dto.AuthUserDetails;
import com.ipms.sys.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserService implements UserDetailsService, UserDetailsPasswordService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> userList() {
        return userMapper.findAll();
    }

    public User findById(Long id) {
        return userMapper.findById(id);
    }

    public Long count() {
        return userMapper.count();
    }

    public void addUser(User user) {
        userMapper.insert(user);
    }

    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        log.info("updatePassword---user: {}, newPassword: {}", user, newPassword);
        return null;
    }

    /**
     * 根据登录名和密码验证用户
     * @param loginName: login name
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String loginName) throws UsernameNotFoundException {
        log.info("loadUserByUsername - {}", loginName);
        User user = userMapper.findByLoginName(loginName);
        if (user == null) {
            log.error("loginname '" + loginName + "' is not found");
            throw new UsernameNotFoundException("loginname " + loginName + " is not found");
        }
        //验证密码
        String encodePwd = passwordEncoder.encode(user.getPassword());
        log.info("encode password = {}", encodePwd);


        return new AuthUserDetails(user);
    }

}
