package com.ipms.sys.service;

import com.ipms.sys.mapper.UserMapper;
import com.ipms.sys.model.AuthUserDetails;
import com.ipms.sys.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserService implements UserDetailsService, UserDetailsPasswordService {

    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public List<User> userList() {
        return userMapper.findAll();
    }

    public User findById(Integer id) {
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
     *
     * @param loginName: login name
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String loginName) throws UsernameNotFoundException {
        User user = userMapper.findByLoginName(loginName);
        if (user == null) {
            throw new UsernameNotFoundException("loginname " + loginName + " is not found");
        }
        return new AuthUserDetails(user);
    }
}
