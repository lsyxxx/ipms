package com.abt.sys.repository;

import com.abt.sys.model.entity.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 员工查询
 */

@Component
@RequiredArgsConstructor
public class EmployeeRepositoryImpl implements EmployeeRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Employee findByJobNumber(String jobNumber) {
        jdbcTemplate.queryForObject("select e.Id, e.JobNumber, e.Name, e.Sex, e.Dept, o.Name as deptName, e.banzhudept, so.Name as banzhudeptName " +
                        "from T_employee e " +
                        "left join Org o on e.Dept = o.Id " +
                        "left join Org so on e.banzhudept = so.Id " +
                        "where e.JobNumber = ?",
                new EmployeeRepositoryImpl.EmployeeMapper(), jobNumber);

        return null;
    }


    class EmployeeMapper implements RowMapper<Employee> {

        @Nullable
        @Override
        public Employee mapRow(ResultSet rs, int rowNum) throws SQLException {
            Employee employee = new Employee();
            employee.setId(rs.getString("Id"));
            employee.setJobNumber(rs.getString("JobNumber"));
            employee.setName(rs.getString("Name"));
            employee.setSex(rs.getInt("Sex"));
            employee.setDeptI(rs.getString("Dept"));
            employee.setDeptName(rs.getString("deptName"));
            employee.setSubDeptId(rs.getString("banzhudept"));
            employee.setSubDeptName(rs.getString("banzhudeptName"));



            return employee;
        }
    }

}
