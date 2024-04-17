package com.abt.wf.repository;

import com.abt.wf.entity.WorkflowBase;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.abt.common.util.TimeUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * 适用于RequestForm
 */
public abstract class AbstractBaseQueryRepositoryImpl {

    /**
     * 获取查询结果的列
     */
    public Set<String> getColumnMetaData(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        Set<String> columnNames = new HashSet<>();
        for (int i = 1; i <= columnCount; i++) {
            columnNames.add(metaData.getColumnName(i).toLowerCase());
        }
        return columnNames;
    }

    public static String todoSql(String entityTable) {
        return "select null as inv_task_id, null as inv_task_def_id, null as inv_task_name, " +
                "null as inv_task_assignee_id, null as inv_task_assignee_name, " +
                "t.ID_ as cur_task_id, t.TASK_DEF_KEY_ as cur_task_def_id, t.NAME_ as cur_task_name, t.ASSIGNEE_ as cur_task_assignee_id, " +
                "? as cur_task_assignee_name, " +
                "e.*, e.copy as copy_users,  su.Name as create_username1 " +
                "from ACT_RU_TASK t " +
                "left join " + entityTable + " e on e.proc_inst_id = t.PROC_INST_ID_ " +
                "left join [dbo].[User] su on e.create_userid = su.Id " +
                "where 1=1 " +
                "and e.is_del = 0 " +
                "and t.TASK_DEF_KEY_ not like '%apply%' ";
    }

    public static String countTodoSql(String entityTable) {
        return "select count(1) " +
                "from ACT_RU_TASK t " +
                "left join " + entityTable + " e on e.proc_inst_id = t.PROC_INST_ID_ " +
//                "left join [dbo].[User] su on e.create_userid = su.Id " +
                "where 1=1 " +
                "and e.is_del = 0 " +
                "and t.TASK_DEF_KEY_ not like '%apply%' ";
    }

    public static String applySql(String entityTable) {
        return "select null as inv_task_id, null as inv_task_def_id, null as inv_task_name, " +
                "null as inv_task_assignee_id, null as inv_task_assignee_name, " +
                "t.ID_ as cur_task_id, t.TASK_DEF_KEY_ as cur_task_def_id, t.NAME_ as cur_task_name, t.ASSIGNEE_ as cur_task_assignee_id, " +
                "su.Name as cur_task_assignee_name, " +
                "e.*, e.copy as copy_users,  ? as create_username1 " +
                "from " + entityTable + " e " +
                "left join ACT_RU_TASK t on e.proc_inst_id = t.PROC_INST_ID_ " +
                "left join [dbo].[User] su on t.ASSIGNEE_ = su.Id " +
                "where 1=1 " +
                "and e.is_del = 0 ";
    }

    public static String countApplySql(String entityTable) {
        return "select count(1) " +
                "from " + entityTable + " e " +
                "left join ACT_RU_TASK t on e.proc_inst_id = t.PROC_INST_ID_ " +
//                "left join [dbo].[User] su on t.ASSIGNEE_ = su.Id " +
                "where 1=1 " +
                "and e.is_del = 0 ";
    }


    public static String doneSql(String entityTable) {
        return "select t.ID_ as inv_task_id, t.TASK_DEF_KEY_ as inv_task_def_id, t.NAME_ as inv_task_name, " +
                "? as inv_task_assignee_id, ? as inv_task_assignee_name, " +
                "rt.ID_ as cur_task_id, rt.TASK_DEF_KEY_ as cur_task_def_id, rt.NAME_ as cur_task_name, rt.ASSIGNEE_ as cur_task_assignee_id, " +
                "tu.Name as cur_task_assignee_name, " +
                "e.*, e.copy as copy_users, su.Name as create_username1 " +
                "from ACT_HI_TASKINST t " +
                "left join " + entityTable  + " e on e.proc_inst_id = t.PROC_INST_ID_ " +
                "left join ACT_RU_TASK rt on rt.PROC_INST_ID_ = t.PROC_INST_ID_ " +
                "left join [dbo].[User] su on e.create_userid = su.Id " +
                "left join [dbo].[User] tu on rt.ASSIGNEE_ = tu.Id " +
                "where 1=1 and e.is_del = 0  " +
                "and t.END_TIME_ is not NULL " +
                //仅查询未删除的，去掉apply节点
                "and t.TASK_DEF_KEY_ not like '%apply%' ";
    }

    public static String countDoneSql(String entityTable) {
        return "select count(1) " +
                "from ACT_HI_TASKINST t " +
                "left join " + entityTable  + " e on e.proc_inst_id = t.PROC_INST_ID_ " +
                "left join ACT_RU_TASK rt on rt.PROC_INST_ID_ = t.PROC_INST_ID_ " +
//                "left join [dbo].[User] su on e.create_userid = su.Id " +
//                "left join [dbo].[User] tu on rt.ASSIGNEE_ = tu.Id " +
                "where 1=1 and e.is_del = 0  " +
                "and t.END_TIME_ is not NULL " +
                //仅查询未删除的，去掉apply节点
                "and t.TASK_DEF_KEY_ not like '%apply%' ";
    }

    public <T extends WorkflowBase> void workflowBaseAndTaskSetter(T form, ResultSet rs) throws SQLException {
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
        form.setCreateUsername(rs.getString("create_username1"));
        form.setCreateDeptId(rs.getString("create_dept_id"));
        form.setCreateDeptName(rs.getString("create_dept_name"));
        form.setCreateTeamId(rs.getString("create_team_id"));
        form.setCreateTeamName(rs.getString("create_team_name"));
        form.setCopy(rs.getString("copy_users"));

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
    }

    String commonBetween(String startDate, String endDate, List<Object> params) {
        String sql = "";
        if (StringUtils.isNotBlank(startDate)) {
            sql += "and e.create_date >= ? ";
            params.add(startDate);
        }
        if (StringUtils.isNotBlank(endDate)) {
            sql += "and e.create_date <= ? ";
            params.add(endDate);
        }
        return sql;
    }

    abstract String conditionSql(String sql, List<Object> array, String... params);
    abstract String between(String startDate, String endDate, List<Object> params);
}
