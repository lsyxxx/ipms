package com.ipms.sys.mapper;

import com.ipms.sys.model.entity.RoleFunc;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.security.Provider;
import java.util.List;
import java.util.Objects;

@Mapper
public interface RoleFuncMapper extends BaseMapper<RoleFunc, RoleFunc>{

    @Override
    @Insert("insert into sys_role_func (role_id, func_id) values (#{rf.roleId}, #{rf.funcId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    long insert(@Param("rf") RoleFunc rf);


    @InsertProvider(type = RoleFuncSQLProvider.class, method = "insertByProvider")
    void insert2(RoleFunc roleFunc);



    class RoleFuncSQLProvider{
        public String insertByProvider(RoleFunc roleFunc) {
            SQL sql = new SQL()
                    .INSERT_INTO("sys_role_func")
                    .INTO_COLUMNS("role_id, func_id")
                    .INTO_VALUES("#{roleId}, #{funcId}");
            if (ObjectUtils.isEmpty(roleFunc.getCreateUser())) {
                sql.INTO_COLUMNS("create_user").INTO_VALUES("#{createUser}");
            }
            return sql.toString();
        }

    }

}
