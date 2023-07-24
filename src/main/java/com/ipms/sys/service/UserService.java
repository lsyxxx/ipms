package com.ipms.sys.service;

import com.ipms.sys.mapper.UserMapper;
import com.ipms.sys.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public List<User> userList() {
//        userMapper.findAll();
        List<User> users = new ArrayList<>();
        userMapper.findAll().forEach(u -> users.add(u));
        return users;
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
}
