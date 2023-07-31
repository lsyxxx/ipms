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
public class UserService implements UserDetailsService, UserDetailsPasswordService, CrudService<User, Long> {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 所有正常、禁用用户
     * @return
     */
    @Override
    public List<User> findAll() {
        return userMapper.findVisibleUsers();
    }

    /**
     * 删除用户：设置删除标志
     */
    @Override
    public void delete(User user) {
        userMapper.updateStatus(User.Status.DELETE.ordinal(), user.getId());
    }

    public User findById(Long id) {
        return userMapper.findById(id);
    }

    /**
     * 更新用户基本信息
     * @param user
     */
    @Override
    public void update(User user) {
        userMapper.updateUser(user);
    }

    @Override
    public Long insert(User user) {
        return userMapper.insert(user);
    }

    public Long count() {
        return userMapper.count();
    }

    /**
     * 禁用用户
     * @param user
     */
    public void ban(User user) {
        userMapper.updateStatus(User.Status.BAN.ordinal(), user.getId());
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
