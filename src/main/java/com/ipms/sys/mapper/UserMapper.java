package com.ipms.sys.mapper;

import com.ipms.sys.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper extends BaseMapper<User, User> {

    @Select("SELECT * FROM JSH_USER WHERE ID = #{id}")
    User findById(@Param("id") Integer id);

    @Select("SELECT * FROM JSH_USER")
    List<User> findAll();

    @Select("SELECT count(1) FROM JSH_USER")
    long count();

    @Insert("INSERT INTO JSH_USER (username, login_name, passowrd, position, department) VALUES (#{u.userName}, #{u.loginName}, #{u.password}, #{u.position}, #{u.department})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    long insert(User u);
}
