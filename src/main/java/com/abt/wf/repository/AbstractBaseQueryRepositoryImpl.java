package com.abt.wf.repository;

import com.abt.wf.entity.WorkflowBase;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.abt.common.util.TimeUtil;

/**
 * 适用于RequestForm
 */
public abstract class AbstractBaseQueryRepositoryImpl<T extends WorkflowBase> {

    abstract String between(String startDate, String endDate, List<Object> params);

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

    public void workflowBaseAndTaskSetter(T form, ResultSet rs) throws SQLException {
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

}
