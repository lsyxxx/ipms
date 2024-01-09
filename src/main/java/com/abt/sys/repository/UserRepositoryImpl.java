package com.abt.sys.repository;

import com.abt.common.model.User;
import com.abt.common.util.TimeUtil;
import com.abt.sys.model.dto.UserView;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 查询用户信息
 * 仅查询
 */
@Component
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository{
    private final JdbcTemplate jdbcTemplate;

    @Override
    public UserView getUserBy(String userId) {
        return jdbcTemplate.queryForObject("select * from [User] where Id = ?",
                new UserViewRowMapper(), userId);
    }

    @Override
    public User getSimpleUserInfo(String userId) {
        return jdbcTemplate.queryForObject("select [Id], [Account], [Name] from [dbo].[User] where Id = ?", (rs, rowNum) -> {
            User user = new User();
            user.setCode(rs.getString("Account"));
            user.setId(rs.getString("Id"));
            user.setUsername(rs.getString("Name"));
            return user;
        }, userId);
    }

    class UserViewRowMapper implements RowMapper<UserView> {

        @Nullable
        @Override
        public UserView mapRow(ResultSet rs, int rowNum) throws SQLException {
            UserView user = new UserView();
            user.setId(rs.getString("Id"));
            user.setAccount(rs.getString("Account"));
            user.setPassword(rs.getString("Password"));
            user.setName(rs.getString("Name"));
            user.setSex(rs.getInt("Sex"));
            user.setStatus(rs.getInt("Status"));
            user.setBizCode(rs.getString("BizCode"));
            user.setCreateTime(TimeUtil.from(rs.getTimestamp("CreateTime")));
            user.setCreateId(rs.getString("CreateId"));
            user.setTypeName(rs.getString("TypeName"));
            user.setTypeId(rs.getString("TypeId"));
            user.setTel(rs.getString("Tel"));
            user.setTman(rs.getString("Tman"));
            user.setLevelPost(rs.getString("Levelpost"));
            user.setPapers(rs.getString("Papers"));
            user.setMobile(rs.getString("Mobile"));
            user.setIdCard(rs.getString("IDCARD"));
            user.setEdu(rs.getString("Edu"));
            user.setAddress(rs.getString("Address"));
            user.setRzDay(TimeUtil.from(rs.getTimestamp("RZDay")));
            user.setNativeStr(rs.getString("Native"));
            user.setThpapers(rs.getString("Thpapers"));
            user.setSpec(rs.getString("Spec"));
            user.setZpImage(rs.getString("ZPImage"));
            user.setBirthday(TimeUtil.from(rs.getTimestamp("Birthday")));
            user.setFnote(rs.getString("Fnote"));
            user.setJpost(rs.getString("Jpost"));
            user.setDman(rs.getString("Dman"));
            user.setFaAddress(rs.getString("FaAddress"));
            user.setAtel(rs.getString("Atel"));
            user.setUserSign(rs.getString("UserSign"));
            user.setEmailAddress(rs.getString("emailaddress"));
            user.setOrgFromId(rs.getString("OrgFromId"));
            user.setOrgFromIdName(rs.getString("orgFromIdName"));
            user.setBypapers(rs.getString("bypapers"));
            user.setEdupapers(rs.getString("edupapers"));
            user.setIdcardzhengmian(rs.getString("idcardzhengmian"));
            user.setIdcardfanmianpapers(rs.getString("idcardfanmianpapers"));
            user.setIdcardfanmianpapers(rs.getString("idcardqitapapers"));
            user.setTpost(rs.getString("tpost"));
            user.setEmpnum(rs.getString("empnum"));

            return user;
        }
    }

}
