package com.abt.wf.repository;

import com.abt.wf.entity.Loan;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.abt.common.util.QueryUtil.*;

/**
 * 借款申请流程
 */
@Repository
@AllArgsConstructor
public class LoanTaskRepositoryImpl extends AbstractBaseQueryRepositoryImpl implements LoanTaskRepository {
    private final JdbcTemplate jdbcTemplate;

    public static final String TABLE_LOAN = "wf_loan";

    //criteria 申请日期（起止日期） 流程状态 审批编号 支付方式,项目，借款部门
    @Override
    String conditionSql(String sql, List<Object> array, String... params) {
        sql += between(params[0], params[1], array);

        if (StringUtils.isNotBlank(params[2])) {
            sql += "and e.state = ? ";
            array.add(params[2]);
        }
        if (StringUtils.isNotBlank(params[3])) {
            sql += "and e.id like ? ";
            array.add(like(params[3]));
        }
        if (StringUtils.isNotBlank(params[4])) {
            sql += "and e.pay_type = ? ";
            array.add(params[4]);
        }
        if (StringUtils.isNotBlank(params[5])) {
            sql += "and e.project like ? ";
            array.add(like(params[5]));
        }
        if (StringUtils.isNotBlank(params[6])) {
            sql += "and e.dept_id = ? ";
            array.add(params[6]);
        }
        return sql;
    }

    @Override
    String between(String startDate, String endDate, List<Object> params) {
        return commonBetween(startDate, endDate, params);
    }


    @Override
    public List<Loan> findDoneList(int page, int limit, String userid, String username, String startDate, String endDate, String state, String idLike, String payType, String deptId, String project) {
        List<Object> params = new ArrayList<Object>();
        String sql = doneSql(TABLE_LOAN);
        sql = sql + "and t.PROC_DEF_KEY_ = 'rbsLoan' and t.assignee_ = ? ";
        params.add(userid);     //? as inv_task_assignee_id
        params.add(username);   //? as inv_task_assignee_name
        params.add(userid);        //and t.assignee_ = ?
        sql = conditionSql(sql, params, startDate, endDate, state, idLike, payType, project, deptId);
        sql += "order by t.START_TIME_ desc ";
        if (!isPaging(limit)) {
            sql += pageSqlBySqlserver(page, limit);
        }
        return jdbcTemplate.query(sql, new LoanRowMapper(), params.toArray());

    }

    @Override
    public List<Loan> findTodoList(int page, int limit, String assigneeId, String assigneeName, String startDate, String endDate, String state,
                                   String idLike, String payType, String deptId, String project) {
        List<Object> params = new ArrayList<>();
        String sql = todoSql(TABLE_LOAN);
        sql = sql + "and t.PROC_DEF_ID_ like '%rbsLoan%' and t.assignee_ = ? ";
        params.add(assigneeName); //? as cur_task_assignee_name,
        params.add(assigneeId);   //rt.assignee_ = ?
        sql = conditionSql(sql, params, startDate, endDate, state, idLike, payType, project, deptId);

        sql += "order by t.CREATE_TIME_ desc ";
        if (!isPaging(limit)) {
            sql += pageSqlBySqlserver(page, limit);
        }
        return jdbcTemplate.query(sql, new LoanRowMapper(), params.toArray());
    }

    @Override
    public List<Loan> findUserApplyList(int page, int limit, String applyUserid, String applyUsername, String startDate, String endDate, String state,
                                        String idLike, String payType, String deptId, String project) {

        List<Object> params = new ArrayList<>();
        String sql = applySql(TABLE_LOAN);
        sql = sql + "and t.PROC_DEF_ID_ like '%rbsLoan%' and e.create_userid = ? ";
        params.add(applyUsername);
        params.add(applyUserid);
        sql = conditionSql(sql, params, startDate, endDate, state, idLike, payType, project, deptId);
        sql += "order by e.create_date desc ";
        if (!isPaging(limit)) {
            sql += pageSqlBySqlserver(page, limit);
        }
        return jdbcTemplate.query(sql, new LoanRowMapper(), params.toArray());
    }

    class LoanRowMapper implements RowMapper<Loan> {

        @Nullable
        @Override
        public Loan mapRow(ResultSet rs, int rowNum) throws SQLException {
            Loan form = new Loan();
            form.setId(rs.getString("id"));
            form.setVoucherNum(rs.getInt("vch_num"));
            form.setDeptId(rs.getString("dept_id"));
            form.setDeptName(rs.getString("dept_name"));
            form.setLoanAmount(rs.getDouble("loan_amt"));
            form.setReason(rs.getString("reason_"));
            form.setProject(rs.getString("project"));
            form.setReceiveUser(rs.getString("rec_user"));
            form.setReceiveBank(rs.getString("rec_bank"));
            form.setReceiveAccount(rs.getString("rec_account"));
            form.setPayType(rs.getString("pay_type"));
            workflowBaseAndTaskSetter(form, rs);
            return form;
        }
    }
}
