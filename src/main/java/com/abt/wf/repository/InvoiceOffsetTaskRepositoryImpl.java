package com.abt.wf.repository;

import com.abt.wf.entity.InvoiceOffset;
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
import static com.abt.wf.config.WorkFlowConfig.DEF_KEY_INVOFFSET;

/**
 *
 */
@Repository
@AllArgsConstructor
public class InvoiceOffsetTaskRepositoryImpl extends AbstractBaseQueryRepositoryImpl implements InvoiceOffsetTaskRepository {

    private final JdbcTemplate jdbcTemplate;

    public static final String TABLE_INVOFFSET = "wf_inv_offset";

    @Override
    public List<InvoiceOffset> findDoneList(int page, int limit, String userid, String username, String startDate, String endDate, String state,
                                            String idLike,  String contractName) {
        List<Object> params = new ArrayList<>();
        String sql = doneSql(TABLE_INVOFFSET);
        sql = sql + "and t.PROC_DEF_KEY_ = '" + DEF_KEY_INVOFFSET + "' and t.assignee_ = ? ";
        params.add(userid);     //? as inv_task_assignee_id
        params.add(username);   //? as inv_task_assignee_name
        params.add(userid);        //and t.assignee_ = ?
        sql = conditionSql(sql, params, startDate, endDate, state, idLike, contractName);
        sql += "order by t.START_TIME_ desc ";
        if (isPaging(limit)) {
            sql += pageSqlBySqlserver(page, limit);
        }
        return jdbcTemplate.query(sql, new InvoiceOffsetTaskRepositoryImpl.InvoiceOffsetRowMapper() , params.toArray());
    }

    @Override
    public int countDoneList(String userid, String username, String startDate, String endDate, String state, String idLike,  String contractName) {
        String sql = countDoneSql(TABLE_INVOFFSET);
        List<Object> params = new ArrayList<>();
        sql = sql + "and t.PROC_DEF_KEY_ = '" + DEF_KEY_INVOFFSET + "' and t.assignee_ = ? ";
        params.add(userid);
        sql = conditionSql(sql, params, startDate, endDate, state, idLike, contractName);
        return jdbcTemplate.queryForObject(sql, Integer.class, params.toArray());
    }

    @Override
    public List<InvoiceOffset> findTodoList(int page, int limit, String userid, String username, String startDate, String endDate, String state, String idLike,  String contractName) {
        List<Object> params = new ArrayList<>();
        String sql = todoSql(TABLE_INVOFFSET);
        //候选人
        sql += " or (t.ASSIGNEE_ is null and i.USER_ID_ = ? and i.TYPE_ = 'candidate') ";
        sql = sql + "and t.PROC_DEF_ID_ like '%" + DEF_KEY_INVOFFSET + "%' and t.assignee_ = ? ";
        params.add(username); //? as cur_task_assignee_name,
        params.add(userid);     //i.USER_ID_ = ?
        params.add(userid);   //rt.assignee_ = ?
        sql = conditionSql(sql, params, startDate, endDate, state, idLike, contractName);

        sql += "order by t.CREATE_TIME_ desc ";
        if (isPaging(limit)) {
            sql += pageSqlBySqlserver(page, limit);
        }
        return jdbcTemplate.query(sql, new InvoiceOffsetTaskRepositoryImpl.InvoiceOffsetRowMapper(), params.toArray());
    }

    @Override
    public int countTodoList(String userid, String username, String startDate, String endDate, String state, String idLike,  String contractName) {
        List<Object> params = new ArrayList<>();
        String sql = countTodoSql(TABLE_INVOFFSET);
        sql += " or (t.ASSIGNEE_ is null and i.USER_ID_ = ? and i.TYPE_ = 'candidate') ";
        sql = sql + "and t.PROC_DEF_ID_ like '%" + DEF_KEY_INVOFFSET + "%' and t.assignee_ = ? ";
        params.add(userid);     //i.USER_ID_ = ?
        params.add(userid);   //rt.assignee_ = ?
        sql = conditionSql(sql, params, startDate, endDate, state, idLike, contractName);

        return jdbcTemplate.queryForObject(sql, Integer.class, params.toArray());
    }

    @Override
    public List<InvoiceOffset> findUserApplyList(int page, int limit, String userid, String username, String startDate, String endDate, String state, String idLike,  String contractName) {
        List<Object> params = new ArrayList<>();
        String sql = applySql(TABLE_INVOFFSET);
        sql = sql + "and e.create_userid = ? ";
        params.add(username);
        params.add(userid);     //e.create_userid = ?
        sql = conditionSql(sql, params, startDate, endDate, state, idLike, contractName);
        sql += "order by e.create_date desc ";
        if (isPaging(limit)) {
            sql += pageSqlBySqlserver(page, limit);
        }
        return jdbcTemplate.query(sql, new InvoiceOffsetTaskRepositoryImpl.InvoiceOffsetRowMapper(), params.toArray());
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
            sql += "and e.contract_name like ? ";
            array.add(like(params[4]));
        }
        return sql;
    }

    @Override
    String between(String startDate, String endDate, List<Object> params) {
        return commonBetween(startDate, endDate, params);
    }

    class InvoiceOffsetRowMapper implements RowMapper<InvoiceOffset> {

        @Nullable
        @Override
        public InvoiceOffset mapRow(ResultSet rs, int rowNum) throws SQLException {
            InvoiceOffset form = new InvoiceOffset();
            form.setId(rs.getString("id"));
            form.setCompany(rs.getString("company_"));
            form.setProject(rs.getString("project_"));
            form.setProjectId(rs.getString("project_id"));
            form.setProjectType(rs.getString("project_type"));
            form.setAccumulatedInvoice(rs.getDouble("acc_inv"));
            form.setSupplierId(rs.getString("supplier_id"));
            form.setSupplierName(rs.getString("supplier_name"));
            form.setContractName(rs.getString("contract_name"));
            form.setContractCode(rs.getString("contract_code"));
            form.setContractAmount(rs.getDouble("contract_amt"));
            form.setInvoiceAmount(rs.getDouble("inv_amt"));
            form.setInvoiceCode(rs.getString("inv_code"));
            form.setInvoiceType(rs.getString("inv_type"));
            form.setRemark(rs.getString("remark_"));
            form.setFileList(rs.getString("file_list"));
            form.setManagers(rs.getString("managers"));
            form.setTitle(rs.getString("title_"));
            workflowBaseAndTaskSetter(form, rs);
            return form;
        }
    }
}
