package com.ipms.sys.mapper;

import com.ipms.sys.model.entity.RoleFunc;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleFuncMapper extends BaseMapper<RoleFunc, RoleFunc>{

    @Override
    @Insert("insert into sys_role_func (role_id, func_id) values (#{rf}, #{rf.funcId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    long insert(@Param("rf") RoleFunc rf);
}
