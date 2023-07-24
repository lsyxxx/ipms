package com.ipms.sys.mapper;

import com.ipms.sys.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper extends BaseMapper<User, User> {

    @Select("SELECT * FROM SYS_USER WHERE ID = #{id}")
    User findById(@Param("id") Long id);

    @Select("SELECT * FROM SYS_USER")
    List<User> findAll();

    @Select("SELECT count(1) FROM SYS_USER")
    long count();

    @Insert("INSERT INTO SYS_USER (username, login_name, passowrd, position, department) VALUES (#{u.userName}, #{u.loginName}, #{u.password}, #{u.position}, #{u.department})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    long insert(User u);
}
