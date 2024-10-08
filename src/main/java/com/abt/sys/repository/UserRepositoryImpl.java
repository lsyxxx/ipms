package com.abt.sys.repository;

import com.abt.common.model.User;
import com.abt.common.util.TimeUtil;
import com.abt.sys.model.dto.UserRole;
import com.abt.sys.model.dto.UserView;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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
        List<User> list = jdbcTemplate.query("select [Id], empnum, [Name] from [dbo].[User] where Id = ?", new SimpleUserRowMapper(), userId);
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }


    public static final int USER_ENABLED = 0;
    public static final int USER_DISABLED = 1;

    public List<User> getAllSimpleUser(Integer status) {
//        List<Object> params = new ArrayList<>();
        String sql = "select u.Id, u.empnum, u.Name from [dbo].[User] u " +
                "where 1=1 ";
        if (status != null && status == USER_ENABLED) {
            //用户启用
            sql += "and u.Status = 0  ";
        } else if (status != null && status == USER_DISABLED) {
            sql += "and u.Status = 1 ";
        }

        return jdbcTemplate.query(sql, new SimpleUserRowMapper());
    }


    @Override
    public User getEmployeeDeptByJobNumber(String jobNumber) {
        String sql = "select e.JobNumber as jobNumber, e.Name, e.Id, o.Id as deptId, o.Name as deptName, e.banzhudept as teamId, so.Name as teamName " +
                "from T_EmployeeInfo e " +
                "left join Org o on e.Dept = o.Id " +
                "left join Org so on e.banzhudept = so.Id " +
                //启用的
                "where e.JobNumber = ? ";

        final List<User> list = jdbcTemplate.query(sql, new UserDeptRowMapper(), jobNumber);
        if (!CollectionUtils.isEmpty(list)) {
            return list.get(0);
        }
        return new User();
    }


    @Override
    public User getEmployeeDeptByUserid(String userid) {
        String sql = "select u.Id, u.empnum as jobNumber,  u.Name, d.Dept as deptId, d.deptName, d.banzhudept as teamId, d.banzuName as teamName " +
                "from [dbo].[User] u " +
                "left join ( " +
                    "select e.Id, e.JobNumber, e.Dept, o.Name as deptName, e.banzhudept, f.Name as banzuName " +
                    "from T_EmployeeInfo e " +
                    "left join Org o on e.Dept = o.Id " +
                    "left join Org f on e.banzhudept = f.Id " +
                ") d on u.empnum = d.JobNumber " +
                "where u.Id = ? ";
        return jdbcTemplate.queryForObject(sql, new UserDeptRowMapper(), userid);
    }


    @Override
    public List<UserRole> getUserRoleByUserid(String userid) {
        String sql = "select u.Id as userid, u.Name as username, u.empnum as jobNumber, ro.Id as roleId, ro.Name as roleName " +
                "from [dbo].[User] u " +
                "left join Relevance rl on u.Id = rl.FirstId " +
                "left join [dbo].[Role] ro on rl.SecondId = ro.Id " +
                "where 1=1 " +
                "and u.Id = ? " +
                "and rl.[Key] = 'UserRole'";
        return jdbcTemplate.query(sql, new UserRoleRowMapper(), userid);
    }

    @Override
    public List<UserRole> getUserByRole(String roleId) {
        String sql = "select u.Id as userid, u.Name as username, u.empnum as jobNumber, ro.Id as roleId, ro.Name as roleName " +
                "from [dbo].[User] u " +
                "left join Relevance rl on u.Id = rl.FirstId " +
                "left join [dbo].[Role] ro on rl.SecondId = ro.Id " +
                "where 1=1 " +
                "and ro.Id = ? " +
                "and rl.[Key] = 'UserRole'";

        return jdbcTemplate.query(sql, new UserRoleRowMapper(), roleId);
    }


    class UserRoleRowMapper implements RowMapper<UserRole> {

        @Nullable
        @Override
        public UserRole mapRow(ResultSet rs, int rowNum) throws SQLException {
            UserRole userRole = new UserRole();
            //userid, username, roleId, roleName, jobNumber
            userRole.setId(rs.getString("userid"));
            userRole.setUsername(rs.getString("username"));
            userRole.setCode(rs.getString("jobNumber"));
            userRole.setRoleId(rs.getString("roleId"));
            userRole.setRoleName(rs.getString("roleName"));
            return userRole;
        }
    }

    class UserDeptRowMapper implements RowMapper<User> {

        @Nullable
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setCode(rs.getString("jobNumber"));
            user.setId(rs.getString("Id"));
            user.setUsername(rs.getString("Name"));
            user.setDeptId(rs.getString("deptId"));
            user.setDeptName(rs.getString("deptName"));
            user.setTeamId(rs.getString("teamId"));
            user.setTeamName(rs.getString("teamName"));
            return user;
        }
    }


    class SimpleUserRowMapper implements RowMapper<User> {
        @Nullable
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setCode(rs.getString("empnum"));
            user.setId(rs.getString("Id"));
            user.setUsername(rs.getString("Name"));
            return user;
        }
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
