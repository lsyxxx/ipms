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
        String sql = "select r.*, " +
                "t.ID_ as cur_task_id, t.TASK_DEF_KEY_ as cur_task_def_id, t.NAME_ as cur_task_name, " +
                "t.ASSIGNEE_ as cur_task_assignee_id, u.Name as cur_task_assignee_name, " +
                "null as inv_task_id, null as inv_task_name, null as inv_task_assignee_id, null as inv_task_assignee_name, null as inv_task_def_id " +
                "from wf_rbs r " +
                "left join ACT_RU_TASK t on r.proc_inst_id = t.PROC_INST_ID_ " +
                "left join [dbo].[User] u on t.ASSIGNEE_ = u.Id " +
                "where 1=1 ";

        List<Object> params = new ArrayList<>();
        if (StringUtils.isNotBlank(entityId)) {
            sql += "and r.id like ? ";
            params.add(like(entityId));
        }
        if (StringUtils.isNotBlank(state)) {
            sql += "and r.biz_state = ? ";
            params.add(state);
        }
        if (StringUtils.isNotBlank(createUserid)) {
            sql += "and r.create_userid = ? ";
            params.add(createUserid);
        }
        sql += between(startDate, endDate, params);

        sql += "order by r.create_date desc ";
        //分页
        if (!isPaging(size)) {
            sql += pageSqlBySqlserver(page, size);
        }
        return jdbcTemplate.query(sql, new ReimburseTaskMapper(), params.toArray());
    }

    //query: 分页, 审批编号, 状态，流程创建时间，参与人id, 待办/已办
    @Override
    public List<ReimburseForm> findTaskPageable(int page, int size, String entityId, String state, String invokedUserid,
                                                String startDate, String endDate, int todo) {
        List<Object> params = new ArrayList<>();
        String sql = "select t.ID_ as inv_task_id, t.TASK_DEF_KEY_ as inv_task_def_id, t.NAME_ as inv_task_name, " +
                "t.ASSIGNEE_ as inv_task_assignee_id, u.Name as inv_task_assignee_name, " +
                "null as cur_task_id, null as cur_task_def_id, null as cur_task_name, null as cur_task_assignee, null as cur_task_assignee_name, " +
                "r.* " +
                "from ACT_HI_TASKINST t " +
                "left join wf_rbs r on t.ROOT_PROC_INST_ID_ = r.proc_inst_id " +
                "left join [dbo].[User] u on t.ASSIGNEE_ = u.Id " +
                "where 1=1";
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
            sql += "and t.END_TIME_ is not NULL ";
        } else if (DONE == todo) {
            sql += "and t.END_TIME_ is NULL ";
        }

        sql += between(startDate, endDate, params);

        sql += "order by t.START_TIME_ desc ";
        if (!isPaging(size)) {
            sql += pageSqlBySqlserver(page, size);
        }
        return jdbcTemplate.query(sql, new ReimburseTaskMapper(), params.toArray());
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
            sql += "and r.create_date <= ?";
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
            form.setFileList(rs.getString("file_list"));
            form.setLeader(rs.getBoolean("is_leader"));
            form.setManagerList(rs.getString("manager_list"));

            //-- workflow
            form.setBusinessState(rs.getString("biz_state"));
            form.setProcessState(rs.getString("proc_state"));
            form.setFinished(rs.getBoolean("is_finished"));
            form.setDeleteReason(rs.getString("del_reason"));
            form.setServiceName(rs.getString("srv_name"));
            form.setEndTime(TimeUtil.from(rs.getTimestamp("end_time")));
            form.setProcessInstanceId(rs.getString("proc_inst_id"));
            form.setProcessDefinitionKey(rs.getString("proc_def_key"));
            form.setProcessDefinitionId(rs.getString("proc_def_id"));
            form.setUpdateUserid(rs.getString("update_userid"));
            form.setUpdateUsername(rs.getString("update_username"));
            form.setUpdateDate(TimeUtil.from(rs.getTimestamp("update_date")));
            form.setCreateDate(TimeUtil.from(rs.getTimestamp("create_date")));
            form.setCreateUserid(rs.getString("create_userid"));
            form.setCreateUsername(rs.getString("create_username"));

            //-- form
            form.setCurrentTaskId(rs.getString("cur_task_id"));
            form.setCurrentTaskDefId(rs.getString("cur_task_def_id"));
            form.setCurrentTaskName(rs.getString("cur_task_name"));
            form.setCurrentTaskAssigneeId(rs.getString("cur_task_assignee_id"));
            form.setCurrentTaskAssigneeName(rs.getString("cur_task_assignee_name"));
            form.setInvokedTaskId(rs.getString("inv_task_id"));
            form.setInvokedTaskName(rs.getString("inv_task_name"));
            form.setInvokedTaskAssigneeId(rs.getString("inv_task_assignee_id"));
            form.setInvokedTaskAssigneeName(rs.getString("inv_task_assignee_name"));
            form.setInvokedTaskDefId(rs.getString("inv_task_def_id"));

            return form;
        }
    }
}
