package com.ipms.sys.mapper;

import com.ipms.sys.model.entity.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User, User> {

    @Override
    @Select("SELECT * FROM SYS_USER WHERE ID = #{id}")
    User findById(@Param("id") Long id);


    @Select("SELECT * FROM SYS_USER WHERE LOGIN_NAME = #{loginName}")
    User findByLoginName(@Param("loginName") String loginName);

    @Select("SELECT * FROM SYS_USER WHERE ID = #{id} AND STATUS='1'")
    User findEnabledById(@Param("id") Long id);

    @Override
    @Select("SELECT * FROM SYS_USER")
    List<User> findAll();

    @Select("SELECT count(1) FROM SYS_USER")
    long count();

    @Override
    @Insert("INSERT INTO SYS_USER (username, login_name, password, position, department) VALUES (#{u.userName}, #{u.loginName}, #{u.password}, #{u.position}, #{u.department})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    long insert(@Param("u") User u);

    @Update("UPDATE SYS_USER SET username=#{user.userName}, login_name=#{user.loginName}, position=#{user.position}, department=#{user.department} WHERE id = #{user.id}")
    void update(@Param("user") User user);
}
