package com.abt.wf.repository;

import static com.abt.common.util.QueryUtil.*;
import com.abt.common.util.TimeUtil;
import com.abt.wf.model.ReimburseForm;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */

@Component
@RequiredArgsConstructor
public class ReimburseTaskRepositoryImpl extends AbstractBaseQueryRepositoryImpl implements ReimburseTaskRepository {

    private final JdbcTemplate jdbcTemplate;

    //query: 分页, 审批编号, 状态，创建人，创建时间
    @Override
    public List<ReimburseForm> findReimburseWithCurrenTaskPageable(int page, int size, String entityId, String state, String createUserid, String startDate, String endDate) {
        String sql = "select r.*, su.Name as create_username1, " +
                "t.ID_ as cur_task_id, t.TASK_DEF_KEY_ as cur_task_def_id, t.NAME_ as cur_task_name, " +
                "t.ASSIGNEE_ as cur_task_assignee_id, u.Name as cur_task_assignee_name, " +
                "null as inv_task_id, null as inv_task_name, null as inv_task_assignee_id, null as inv_task_assignee_name, null as inv_task_def_id " +
                "from wf_rbs r " +
                "left join ACT_RU_TASK t on r.proc_inst_id = t.PROC_INST_ID_ " +
                "left join [dbo].[User] u on t.ASSIGNEE_ = u.Id " +
                "left join [dbo].[User] su on r.create_userid = su.Id " +
                "where 1=1 " +
                //仅查询未删除的
                "and r.is_del = 0 ";

        List<Object> params = new ArrayList<>();
        sql = conditionSql(sql, params, entityId, state, createUserid, startDate, endDate);

        sql += "order by r.create_date desc ";
        //分页
        if (isPaging(size)) {
            sql += pageSqlBySqlserver(page, size);
        }
        return jdbcTemplate.query(sql, new ReimburseTaskMapper(), params.toArray());
    }

    @Override
    public int countReimburseWithCurrenTaskPageable(String entityId, String state, String createUserid, String startDate, String endDate) {
        String sql = "select count(1) " +
                "from wf_rbs r " +
                "left join ACT_RU_TASK t on r.proc_inst_id = t.PROC_INST_ID_ " +
                "left join [dbo].[User] u on t.ASSIGNEE_ = u.Id " +
                "left join [dbo].[User] su on r.create_userid = su.Id " +
                "where 1=1 " +
                //仅查询未删除的
                "and r.is_del = 0 ";

        List<Object> params = new ArrayList<>();
        sql = conditionSql(sql, params, entityId, state, createUserid, startDate, endDate);

        return jdbcTemplate.queryForObject(sql, Integer.class, params.toArray());
    }

    //query: 分页, 审批编号, 状态，流程创建时间，参与人id, 待办/已办
    @Override
    public List<ReimburseForm> findTaskPageable(int page, int size, String entityId, String state, String invokedUserid,
                                                String startDate, String endDate, int todo) {
        List<Object> params = new ArrayList<>();
        String sql = "select t.ID_ as inv_task_id, t.TASK_DEF_KEY_ as inv_task_def_id, t.NAME_ as inv_task_name, " +
                "t.ASSIGNEE_ as inv_task_assignee_id, u.Name as inv_task_assignee_name, " +
                "rt.ID_ as cur_task_id, rt.TASK_DEF_KEY_ as cur_task_def_id, rt.NAME_ as cur_task_name, rt.ASSIGNEE_ as cur_task_assignee_id, " +
                "tu.Name as cur_task_assignee_name, " +
                "r.*,  su.Name as create_username1 " +
                "from ACT_HI_TASKINST t " +
                "inner join wf_rbs r on t.ROOT_PROC_INST_ID_ = r.proc_inst_id " +
                //当前审批人
                "left join ACT_RU_TASK rt on rt.PROC_INST_ID_ = t.PROC_INST_ID_ " +
                "left join [dbo].[User] u on t.ASSIGNEE_ = u.Id " +
                "left join [dbo].[User] su on r.create_userid = su.Id " +
                "left join [dbo].[User] tu on rt.ASSIGNEE_ = tu.Id " +
                "where 1=1 " +
                //不包含申请节点, 需要申请节点defId包含apply
                "and t.TASK_DEF_KEY_ not like '%apply%' " +
                //没有删除的
                "and r.is_del = 0 ";
        if (StringUtils.isNotBlank(state)) {
            sql += "and r.biz_state = ? ";
            params.add(state);
        }
        if (StringUtils.isNotBlank(entityId)) {
            sql += "and r.id like ? ";
            params.add(like(entityId));
        }
        if (StringUtils.isNotBlank(invokedUserid)) {
            sql += "and t.ASSIGNEE_ = ? ";
            params.add(invokedUserid);
        }
        if (TODO == todo) {
            //待办
            sql += "and t.END_TIME_ is NULL ";
        } else if (DONE == todo) {
            //已办
            sql += "and t.END_TIME_ is not NULL ";
        }

        sql += between(startDate, endDate, params);

        sql += "order by t.START_TIME_ desc ";
        if (isPaging(size)) {
            sql += pageSqlBySqlserver(page, size);
        }
        return jdbcTemplate.query(sql, new ReimburseTaskMapper(), params.toArray());
    }

    @Override
    public int countTask(String entityId, String state, String invokedUserid,
                         String startDate, String endDate, int todo) {
        List<Object> params = new ArrayList<>();
        String sql = "select count(1)" +
                "from ACT_HI_TASKINST t " +
                "inner join wf_rbs r on t.ROOT_PROC_INST_ID_ = r.proc_inst_id " +
                //当前审批人
                "left join ACT_RU_TASK rt on rt.PROC_INST_ID_ = t.PROC_INST_ID_ " +
                "left join [dbo].[User] u on t.ASSIGNEE_ = u.Id " +
                "left join [dbo].[User] su on r.create_userid = su.Id " +
                "left join [dbo].[User] tu on rt.ASSIGNEE_ = tu.Id " +
                "where 1=1 " +
                //不包含申请节点, 需要申请节点defId包含apply
                "and t.TASK_DEF_KEY_ not like '%apply%' " +
                //没有删除的
                "and r.is_del = 0 ";


        if (StringUtils.isNotBlank(state)) {
            sql += "and r.biz_state = ? ";
            params.add(state);
        }
        if (StringUtils.isNotBlank(entityId)) {
            sql += "and r.id like ? ";
            params.add(like(entityId));
        }
        if (StringUtils.isNotBlank(invokedUserid)) {
            sql += "and t.ASSIGNEE_ = ? ";
            params.add(invokedUserid);
        }
        if (TODO == todo) {
            //待办
            sql += "and t.END_TIME_ is NULL ";
        } else if (DONE == todo) {
            //已办
            sql += "and t.END_TIME_ is not NULL ";
        }

        sql += between(startDate, endDate, params);

        return jdbcTemplate.queryForObject(sql, Integer.class, params.toArray());
    }

    @Override
    String conditionSql(String sql, List<Object> array, String... params) {
        if (StringUtils.isNotBlank(params[0])) {
            sql += "and r.id like ? ";
            array.add(like(params[0]));
        }
        if (StringUtils.isNotBlank(params[1])) {
            sql += "and r.biz_state = ? ";
            array.add(params[1]);
        }
        if (StringUtils.isNotBlank(params[2])) {
            sql += "and r.create_userid = ? ";
            array.add(params[2]);
        }
        return sql;
    }

    /**
     * sql语句, 业务创建日期范围查询
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return sql 语句
     */
    @Override
    String between(String startDate, String endDate, List<Object> params) {
        String sql = "";
        if (StringUtils.isNotBlank(startDate)) {
            sql += "and r.create_date >= ? ";
            params.add(startDate);
        }
        if (StringUtils.isNotBlank(endDate)) {
            sql += "and r.create_date <= ? ";
            params.add(endDate);
        }
        return sql;
    }

    //query: 分页, 审批编号, 状态，创建时间
//    public List<ReimburseForm> findTodoListPageable(int page, int size) {
//
//    }



    class ReimburseTaskMapper implements RowMapper<ReimburseForm> {

        @Nullable
        @Override
        public ReimburseForm mapRow(ResultSet rs, int rowNum) throws SQLException {
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
            form.setLeader(rs.getBoolean("is_leader"));
            form.setManagers(rs.getString("managers"));

            //workflow & task
            workflowBaseAndTaskSetter(form, rs);
            return form;
        }
    }
}
