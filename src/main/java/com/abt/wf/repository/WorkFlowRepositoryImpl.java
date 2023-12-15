package com.abt.wf.repository;

import com.abt.common.util.TimeUtil;
import com.abt.wf.model.TaskDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @description 流程查询
 */
@RequiredArgsConstructor
@Repository
public class WorkFlowRepositoryImpl implements WorkFlowRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<TaskDTO> findByStartUserid(String userid) {
        return jdbcTemplate.query("select p.PROC_INST_ID_, p.BUSINESS_KEY_, p.START_USER_ID_, p.START_TIME_ as PROC_START_TIME_, p.END_TIME_ as PROC_END_TIME_, p.PROC_DEF_ID_, p.PROC_DEF_KEY_, p.DELETE_REASON_ as PROC_DELETE_REASON_, p.STATE_, " +
                "t.ID_ AS TASK_ID_, t.TASK_DEF_KEY_, t.NAME_, t.ASSIGNEE_, t.DESCRIPTION_, t.START_TIME_ as TASK_START_TIME_, t.END_TIME_ as TASK_END_TIME_, t.DELETE_REASON_ as TASK_DELETE_REASON_" +
                "from ACT_HI_PROCINST p " +
                "left join ACT_HI_TASKINST t on p.PROC_INST_ID_ = t.PROC_INST_ID_ " +
                "left join User u on p.START_USER_ID = u." +
                "where p.START_USER_ID_ = ? " +
                "and BUSINESS_KEY_ not like '%PREVIEW_USER_%' ORDER BY PROC_START_TIME_, TASK_START_TIME_",
                new Object[]{userid}, new TaskDTORowMapper());

    }

    class TaskDTORowMapper implements RowMapper<TaskDTO> {

        @Override
        public TaskDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            TaskDTO dto = new TaskDTO();

            dto.setProcessInstanceId(rs.getString("PROC_INST_ID_"));
            dto.setBusinessKey(rs.getString("BUSINESS_KEY_"));
            dto.setStartUserid(rs.getString("START_USER_ID_"));
            dto.setProcessStartTime(TimeUtil.from(rs.getDate("PROC_START_TIME_")));
            dto.setProcessEndTime(TimeUtil.from(rs.getDate("PROC_END_TIME_")));
            dto.setProcessDefinitionId(rs.getString("PROC_DEF_ID_"));
            dto.setProcessDefinitionKey(rs.getString("PROC_DEF_KEY_"));
            dto.setState(rs.getString("STATE_"));
            dto.setProcessDeleteReason(rs.getString("PROC_DELETE_REASON_"));
            //--- task
            dto.setTaskInstanceId(rs.getString("TASK_ID_"));
            dto.setTaskDefName(rs.getString("NAME_"));
            dto.setTaskDefKey(rs.getString("TASK_DEF_KEY_"));
            dto.setAssigneeId(rs.getString("ASSIGNEE_"));
            dto.setTaskDescription(rs.getString("DESCRIPTION_"));
            dto.setTaskStartTime(TimeUtil.from(rs.getDate("TASK_START_TIME_")));
            dto.setTaskEndTime(TimeUtil.from(rs.getDate("TASK_END_TIME_")));
            dto.setTaskDeleteReason(rs.getString("TASK_DELETE_REASON_"));

            return dto;
        }
    }




}
