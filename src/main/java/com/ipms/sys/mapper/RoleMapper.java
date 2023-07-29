package com.ipms.sys.mapper;

import com.ipms.sys.model.entity.Role;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role, Role>{

    @Override
    @Insert("INSERT INTO sys_role (name, type, description, enabled, sort, tenant_id) VALUES (#{role.name}, #{role.type}, #{role.description}, #{role.enabled}, #{role.sort}, #{role.userId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    long insert(Role role);


    @Select("SELECT * FROM sys_role where id = #{id} and delete_flag='0' and enabled=1")
    Role findEnabledBy(@Param("id") Long id);

    @Override
    @Select("SELECT * FROM sys_role")
    List<Role> findAll();

    @Override
    Role findById(Long id);










}





