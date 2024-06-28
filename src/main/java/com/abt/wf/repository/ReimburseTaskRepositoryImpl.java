package com.abt.wf.repository;

import com.abt.common.util.TimeUtil;
import com.abt.wf.entity.Reimburse;
import com.abt.wf.model.ReimburseForm;
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
import static com.abt.common.util.QueryUtil.like;
import static com.abt.wf.config.WorkFlowConfig.DEF_KEY_RBS;

/**
 *
 */
@Repository
@AllArgsConstructor
public class ReimburseTaskRepositoryImpl extends AbstractBaseQueryRepositoryImpl implements ReimburseTaskRepository {
    private final JdbcTemplate jdbcTemplate;

    public static final String TABLE_RBS = "wf_rbs";

    @Override
    public List<Reimburse> findDoneList(int page, int limit, String userid, String username, String startDate, String endDate, String state,
                                        String idLike, String projectName) {
        List<Object> params = new ArrayList<>();
        String sql = doneSql(TABLE_RBS);
        sql = sql + "and t.PROC_DEF_KEY_ = '" + DEF_KEY_RBS + "' and t.assignee_ = ? ";
        params.add(userid);     //? as inv_task_assignee_id
        params.add(username);   //? as inv_task_assignee_name
        params.add(userid);        //and t.assignee_ = ?
        sql = conditionSql(sql, params, startDate, endDate, state, idLike, projectName);
        sql += "order by t.START_TIME_ desc ";
        if (isPaging(limit)) {
            sql += pageSqlBySqlserver(page, limit);
        }
        return jdbcTemplate.query(sql, new ReimburseTaskMapper() , params.toArray());
    }

    @Override
    public int countDoneList(String userid, String username, String startDate, String endDate, String state, String idLike, String projectName) {
        String sql = countDoneSql(TABLE_RBS);
        List<Object> params = new ArrayList<>();
        sql = sql + "and t.PROC_DEF_KEY_ = '" + DEF_KEY_RBS + "' and t.assignee_ = ? ";
        params.add(userid);
        sql = conditionSql(sql, params, startDate, endDate, state, idLike, projectName);
        return jdbcTemplate.queryForObject(sql, Integer.class, params.toArray());
    }

    @Override
    public List<Reimburse> findTodoList(int page, int limit, String userid, String username, String startDate, String endDate, String state, String idLike, String projectName) {
        List<Object> params = new ArrayList<>();
        String sql = todoSql(TABLE_RBS);
        sql = sql + "and t.PROC_DEF_ID_ like '%" + DEF_KEY_RBS + "%' and t.assignee_ = ? ";
        params.add(username); //? as cur_task_assignee_name,
        params.add(userid);   //rt.assignee_ = ?
        sql = conditionSql(sql, params, startDate, endDate, state, idLike, projectName);

        sql += "order by t.CREATE_TIME_ desc ";
        if (isPaging(limit)) {
            sql += pageSqlBySqlserver(page, limit);
        }
        return jdbcTemplate.query(sql, new ReimburseTaskMapper() , params.toArray());
    }

    @Override
    public int countTodoList(String userid, String username, String startDate, String endDate, String state, String idLike, String projectName) {
        List<Object> params = new ArrayList<>();
        String sql = countTodoSql(TABLE_RBS);
        sql = sql + "and t.PROC_DEF_ID_ like '%" + DEF_KEY_RBS + "%' and t.assignee_ = ? ";
        params.add(userid);   //rt.assignee_ = ?
        sql = conditionSql(sql, params, startDate, endDate, state, idLike, projectName);

        return jdbcTemplate.queryForObject(sql, Integer.class, params.toArray());
    }

    @Override
    public List<Reimburse> findUserApplyList(int page, int limit, String userid, String username, String startDate, String endDate, String state,
                                             String idLike, String projectName) {
        List<Object> params = new ArrayList<>();
        String sql = applySql(TABLE_RBS);
        sql = sql + "and e.create_userid = ? ";
        params.add(username);
        params.add(userid);     //e.create_userid = ?
        sql = conditionSql(sql, params, startDate, endDate, state, idLike, projectName);
        sql += "order by e.create_date desc ";
        if (isPaging(limit)) {
            sql += pageSqlBySqlserver(page, limit);
        }
        return jdbcTemplate.query(sql, new ReimburseTaskMapper() , params.toArray());
    }

    @Override
    String conditionSql(String sql, List<Object> array, String... params) {
        sql += between(params[0], params[1], array);

        if (StringUtils.isNotBlank(params[2])) {
            sql += "and e.biz_state = ? ";
            array.add(params[2]);
        }
        if (StringUtils.isNotBlank(params[3])) {
            sql += "and e.id like ? ";
            array.add(like(params[3]));
        }
        if (StringUtils.isNotBlank(params[4])) {
            sql += "and e.project like ? ";
            array.add(like(params[4]));
        }
        return sql;
    }


    @Override
    String between(String startDate, String endDate, List<Object> params) {
        return commonBetween(startDate, endDate, params);
    }

    class ReimburseTaskMapper implements RowMapper<Reimburse> {

        @Nullable
        @Override
        public Reimburse mapRow(ResultSet rs, int rowNum) throws SQLException {
            ReimburseForm form = new ReimburseForm();
            //-- 业务
            form.setId(rs.getString("id"));
            form.setCost(rs.getDouble("cost"));
            form.setVoucherNum(rs.getInt("voucher_num"));
            form.setRbsDate(TimeUtil.from(rs.getDate("rbs_date")));
            form.setRbsType(rs.getString("rbs_type"));
            form.setReason(rs.getString("reason_"));
            form.setCompany(rs.getString("company"));
            form.setDepartmentId(rs.getString("dept_id"));
            form.setDepartmentName(rs.getString("dept_name"));
            form.setTeamId(rs.getString("team_id"));
            form.setTeamName(rs.getString("team_name"));
            form.setProject(rs.getString("project"));
            form.setPdfFileList(rs.getString("pdf_file"));
            form.setOtherFileList(rs.getString("other_file"));
            form.setManagers(rs.getString("managers"));

            //workflow & task
            workflowBaseAndTaskSetter(form, rs);
            return form;
        }
    }
}
