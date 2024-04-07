package com.abt.wf.repository;

import com.abt.wf.entity.TripReimburse;
import com.abt.wf.model.TripReimburseForm;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.abt.common.util.QueryUtil.*;

/**
 *
 */
@Component
@AllArgsConstructor
public class TripReimburseTaskRepositoryImpl extends AbstractBaseQueryRepositoryImpl<TripReimburse> implements TripReimburseTaskRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<TripReimburse> findTaskWithCurrenTaskPageable(int page, int size, String entityId, String state, String createUserid, String startDate, String endDate, String staff) {
        String sql = "select t.*, su.Name as create_username1, " +
                "t.ID_ as cur_task_id, t.TASK_DEF_KEY_ as cur_task_def_id, t.NAME_ as cur_task_name, " +
                "t.ASSIGNEE_ as cur_task_assignee_id, u.Name as cur_task_assignee_name, " +
                "null as inv_task_id, null as inv_task_name, null as inv_task_assignee_id, null as inv_task_assignee_name, null as inv_task_def_id " +
                "from wf_trip t " +
                "left join ACT_RU_TASK t on r.proc_inst_id = t.PROC_INST_ID_ " +
                "left join [dbo].[User] u on t.ASSIGNEE_ = u.Id " +
                "left join [dbo].[User] su on r.create_userid = su.Id " +
                "where 1=1 " +
                //仅查询未删除的
                "and r.is_del = 0"
                ;

        List<Object> params = new ArrayList<>();
        if (StringUtils.isNotBlank(entityId)) {
            sql += "and t.id like ? ";
            params.add(like(entityId));
        }
        if (StringUtils.isNotBlank(state)) {
            sql += "and t.biz_state = ? ";
            params.add(state);
        }
        if (StringUtils.isNotBlank(createUserid)) {
            sql += "and t.create_userid = ? ";
            params.add(createUserid);
        }
        sql += between(startDate, endDate, params);
        if (!isPaging(size)) {
            sql += pageSqlBySqlserver(page, size);
        }

        return jdbcTemplate.query(sql, new TripReimburseRowMapper(), params.toArray());
    }

    @Override
    public List<TripReimburse> findTaskPageable(int page, int size, String entityId, String state, String invokedUserid, String startDate, String endDate, String staff, int todo) {
        return List.of();
    }

    @Override
    String between(String startDate, String endDate, List<Object> params) {
        String sql = "";
        if (StringUtils.isNotBlank(startDate)) {
            sql += "and t.create_date >= ? ";
            params.add(startDate);
        }
        if (StringUtils.isNotBlank(endDate)) {
            sql += "and t.create_date <= ? ";
            params.add(endDate);
        }
        return sql;
    }

    class TripReimburseRowMapper implements RowMapper<TripReimburse> {
        @Nullable
        @Override
        public TripReimburse mapRow(ResultSet rs, int rowNum) throws SQLException {
            TripReimburse form = new TripReimburse();
            //业务



            TripReimburseTaskRepositoryImpl.super.workflowBaseAndTaskSetter(form, rs);
            return form;
        }
    }

}
