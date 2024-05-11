package com.abt.wf.repository;

import com.abt.common.util.TimeUtil;
import com.abt.wf.entity.TripReimburse;
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
public class TripReimburseTaskRepositoryImpl extends AbstractBaseQueryRepositoryImpl implements TripReimburseTaskRepository {

    private final JdbcTemplate jdbcTemplate;
    public static final String TABLE_ENTITY = "wf_trip";



    //已办主数据
    @Override
    public List<TripReimburse> findDoneMainList(int page, int limit, String entityId, String state, String userid, String username,
                                                String startDate, String endDate) {
        List<Object> params = new ArrayList<>();
        String sql = doneSql(TABLE_ENTITY);
        sql = sql + "and t.PROC_DEF_KEY_ = 'rbsTrip' and t.assignee_ = ? and e.root_id is null ";

        params.add(userid);     //? as inv_task_assignee_id
        params.add(username);   //? as inv_task_assignee_name
        params.add(userid);        //and t.assignee_ = ?

        sql = conditionSql(sql, params, startDate, endDate, state, entityId);
        sql += "order by t.START_TIME_ desc ";
        if (isPaging(limit)) {
            sql += pageSqlBySqlserver(page, limit);
        }

        return jdbcTemplate.query(sql, new TripReimburseRowMapper(), params.toArray());
    }

    @Override
    public int countDoneList(int page, int limit, String entityId, String state, String userid, String startDate, String endDate) {
        List<Object> params = new ArrayList<Object>();
        String sql = countDoneSql(TABLE_ENTITY);
        sql = sql + " and t.PROC_DEF_KEY_ = 'rbsTrip' and t.assignee_ = ? and e.root_id is null ";
        params.add(userid);        //and t.assignee_ = ?
        sql = conditionSql(sql, params, startDate, endDate, state, entityId);

        return jdbcTemplate.queryForObject(sql, Integer.class, params.toArray());
    }

    @Override
    public List<TripReimburse> findTodoMainList(int page, int limit, String entityId, String state, String userid, String username,
                                                String startDate, String endDate) {
        List<Object> params = new ArrayList<>();
        String sql = todoSql(TABLE_ENTITY);
        sql = sql + "and t.PROC_DEF_ID_ like '%rbsTrip%' and t.assignee_ = ?  and e.root_id is null ";
        params.add(username); //? as cur_task_assignee_name,
        params.add(userid);   //rt.assignee_ = ?
        sql = conditionSql(sql, params, startDate, endDate, state, entityId);

        sql += "order by t.CREATE_TIME_ desc ";
        if (isPaging(limit)) {
            sql += pageSqlBySqlserver(page, limit);
        }
        return jdbcTemplate.query(sql, new TripReimburseRowMapper(), params.toArray());
    }

    @Override
    public int countTodoList(int page, int limit, String entityId, String state, String userid, String startDate, String endDate) {
        List<Object> params = new ArrayList<>();
        String sql = countTodoSql(TABLE_ENTITY);
        sql = sql + "and t.PROC_DEF_ID_ like '%rbsTrip%' and t.assignee_ = ? and e.root_id is null  ";
        params.add(userid);   //rt.assignee_ = ?
        sql = conditionSql(sql, params, startDate, endDate, state, entityId);
        return jdbcTemplate.queryForObject(sql, Integer.class, params.toArray());
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
        return sql;
    }

    @Override
    String between(String startDate, String endDate, List<Object> params) {
//        String sql = "";
//        if (StringUtils.isNotBlank(startDate)) {
//            sql += "and t.create_date >= ? ";
//            params.add(startDate);
//        }
//        if (StringUtils.isNotBlank(endDate)) {
//            sql += "and t.create_date <= ? ";
//            params.add(endDate);
//        }
//        return sql;
        return this.commonBetween(startDate, endDate, params);
    }

    class TripReimburseRowMapper implements RowMapper<TripReimburse> {
        @Nullable
        @Override
        public TripReimburse mapRow(ResultSet rs, int rowNum) throws SQLException {
            TripReimburse form = new TripReimburse();
            //业务
            form.setId(rs.getString("id"));
            form.setRootId(rs.getString("root_id"));
            form.setDeptId(rs.getString("dept_id"));
            form.setCode(rs.getString("code_"));
            form.setDeptName(rs.getString("dept_name"));
            form.setStaff(rs.getString("staff_"));
            form.setReason(rs.getString("reason"));
            form.setTripStartDate(TimeUtil.from(rs.getDate("trip_start_date")));
            form.setTripEndDate(TimeUtil.from(rs.getDate("trip_end_date")));
            form.setTripOrigin(rs.getString("origin_"));
            form.setTripArrival(rs.getString("arrival_"));
            form.setAllowanceDuration(rs.getDouble("allowance_dur"));
            form.setAllowanceExpense(rs.getBigDecimal("allowance_exp"));
            form.setOtherExpenseDesc(rs.getString("oth_exp_desc"));
            form.setOtherExpense(rs.getBigDecimal("oth_exp"));
            form.setSum(rs.getBigDecimal("sum_"));
            form.setPayeeId(rs.getString("payee_id"));
            form.setPayeeName(rs.getString("payee_name"));
            form.setSort(rs.getInt("sort_"));
            form.setTransportation(rs.getString("transportation"));
            form.setTransExpense(rs.getBigDecimal("trans_exp"));
            form.setCompany(rs.getString("company_"));
            form.setManagers(rs.getString("managers"));
            workflowBaseAndTaskSetter(form, rs);
            return form;
        }
    }

}
