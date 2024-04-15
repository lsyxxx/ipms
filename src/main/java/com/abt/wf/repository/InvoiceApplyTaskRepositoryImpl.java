package com.abt.wf.repository;

import com.abt.wf.entity.InvoiceApply;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
 * criteria 合同名称，合同编号, 客户id， 客户name, 项目名称, 申请部门id, 申请部门name
 */
@Repository
@Slf4j
@AllArgsConstructor
public class InvoiceApplyTaskRepositoryImpl extends AbstractBaseQueryRepositoryImpl implements InvoiceApplyTaskRepository {
    private final JdbcTemplate jdbcTemplate;;
    public static final String TABLE_INV = "wf_inv";

    @Override
    public List<InvoiceApply> findDoneListPageable(int page, int limit, String userid, String username, String state, String startDate, String endDate,
                                                   String idLike, String clientId, String clientName, String contractNo, String contractName,
                                                   String project, String deptId, String deptName) {
        List<Object> params = new ArrayList<>();
        String sql = doneSql(TABLE_INV);
        sql = sql + "and t.PROC_DEF_KEY_ = 'rbsInv' and t.assignee_ = ? ";
        params.add(userid);     //? as inv_task_assignee_id
        params.add(username);   //? as inv_task_assignee_name
        params.add(userid);        //and t.assignee_ = ?
        sql = conditionSql(sql, params, startDate, endDate, state, idLike, project, contractNo, contractName, clientId, clientName, deptId, deptName);
        sql += "order by t.START_TIME_ desc ";
        if (isPaging(limit)) {
            sql += pageSqlBySqlserver(page, limit);
        }
        return jdbcTemplate.query(sql, new InvoiceApplyRowMapper() , params.toArray());
    }

    @Override
    public int countDoneList(String userid, String username, String state, String startDate, String endDate,
                             String idLike, String clientId, String clientName, String contractNo, String contractName,
                             String project, String deptId, String deptName) {
        String sql = countDoneSql(TABLE_INV);
        List<Object> params = new ArrayList<>();
        sql = sql + "and t.PROC_DEF_KEY_ = 'rbsInv' and t.assignee_ = ? ";
        params.add(userid);
        sql = conditionSql(sql, params, startDate, endDate, state, idLike, project, contractNo, contractName, clientId, clientName, deptId, deptName);
        return jdbcTemplate.queryForObject(sql, Integer.class, params.toArray());
    }

    @Override
    public List<InvoiceApply> findTodoListPageable(int page, int limit, String userid, String username, String state, String startDate, String endDate,
                                                   String idLike, String clientId, String clientName, String contractNo, String contractName,
                                                   String project, String deptId, String deptName) {
        List<Object> params = new ArrayList<>();
        String sql = todoSql(TABLE_INV);
        sql = sql + "and t.PROC_DEF_ID_ like '%rbsInv%' and t.assignee_ = ? ";
        params.add(username); //? as cur_task_assignee_name,
        params.add(userid);   //rt.assignee_ = ?
        sql = conditionSql(sql, params, startDate, endDate, state, idLike, project, contractNo, contractName, clientId, clientName, deptId, deptName);

        sql += "order by t.CREATE_TIME_ desc ";
        if (isPaging(limit)) {
            sql += pageSqlBySqlserver(page, limit);
        }
        return jdbcTemplate.query(sql, new InvoiceApplyRowMapper(), params.toArray());
    }

    @Override
    public int countTodoList(String userid, String username, String state, String startDate, String endDate,
                             String idLike, String clientId, String clientName, String contractNo, String contractName,
                             String project, String deptId, String deptName) {
        List<Object> params = new ArrayList<>();
        String sql = countTodoSql(TABLE_INV);
        sql = sql + "and t.PROC_DEF_ID_ like '%rbsInv%' and t.assignee_ = ? ";
        params.add(userid);   //rt.assignee_ = ?
        sql = conditionSql(sql, params, startDate, endDate, state, idLike, project, contractNo, contractName, clientId, clientName, deptId, deptName);

        return jdbcTemplate.queryForObject(sql, Integer.class, params.toArray());
    }

    @Override
    public List<InvoiceApply> findApplyListPageable(int page, int limit, String userid, String username, String state, String startDate, String endDate,
                                                    String idLike, String clientId, String clientName, String contractNo, String contractName,
                                                    String project, String deptId, String deptName) {
        List<Object> params = new ArrayList<>();
        String sql = applySql(TABLE_INV);
        sql = sql + "and e.create_userid = ? ";
        params.add(username);
        params.add(userid);     //e.create_userid = ?
        sql = conditionSql(sql, params, startDate, endDate, state, idLike, project, contractNo, contractName, clientId, clientName, deptId, deptName);
        sql += "order by e.create_date desc ";
        if (isPaging(limit)) {
            sql += pageSqlBySqlserver(page, limit);
        }
        return jdbcTemplate.query(sql, new InvoiceApplyRowMapper(), params.toArray());
    }

    @Override
    public int countApplyList(String userid, String username, String state, String startDate, String endDate,
                              String idLike, String clientId, String clientName, String contractNo, String contractName,
                              String project, String deptId, String deptName) {
        List<Object> params = new ArrayList<>();
        String sql = countApplySql(TABLE_INV);
        sql = sql + "and e.create_userid = ? ";
        params.add(userid);     //e.create_userid = ?
        sql = conditionSql(sql, params, startDate, endDate, state, idLike, project, contractNo, contractName, clientId, clientName, deptId, deptName);
        return jdbcTemplate.queryForObject(sql, Integer.class, params.toArray());
    }

    class InvoiceApplyRowMapper implements RowMapper<InvoiceApply> {

        @Nullable
        @Override
        public InvoiceApply mapRow(ResultSet rs, int rowNum) throws SQLException {
            InvoiceApply form = new InvoiceApply();
            form.setId(rs.getString("id"));
            form.setClientId(rs.getString("client_id"));
            form.setClientName(rs.getString("client_name"));
            form.setInvoiceType(rs.getString("inv_type"));
            form.setContractAmount(rs.getDouble("contract_amt"));
            form.setContractNo(rs.getString("contract_no"));
            form.setContractName(rs.getString("contract_name"));
            form.setProject(rs.getString("project"));
            form.setInvoiceAmount(rs.getDouble("inv_amt"));
            form.setAddress(rs.getString("address"));
            form.setTaxNo(rs.getString("tax_no"));
            form.setTelephoneNo(rs.getString("tel_no"));
            form.setBank(rs.getString("bank"));
            form.setAccount(rs.getString("account"));
            form.setManagers(rs.getString("managers"));
            form.setRemark(rs.getString("remark"));


            workflowBaseAndTaskSetter(form, rs);
            return form;
        }
    }

    //criteria 申请日期（起止日期） 流程状态 审批编号 项目名称 合同名称，合同编号, 客户id， 客户name，申请部门id, 申请部门name
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
            sql += "and e.project = ? ";
            array.add(like(params[4]));
        }
        if (StringUtils.isNotBlank(params[5])) {
            sql += "and e.contract_no like ? ";
            array.add(like(params[5]));
        }
        if (StringUtils.isNotBlank(params[6])) {
            sql += "and e.contract_name like ? ";
            array.add(like(params[6]));
        }
        if (StringUtils.isNotBlank(params[7])) {
            sql += "and e.client_id = ? ";
            array.add(params[7]);
        }
        if (StringUtils.isNotBlank(params[8])) {
            sql += "and e.client_name like ? ";
            array.add(like(params[8]));
        }
        if (StringUtils.isNotBlank(params[9])) {
            sql += "and e.dept_id = ? ";
            array.add(params[9]);
        }
        if (StringUtils.isNotBlank(params[10])) {
            sql += "and e.dept_name = ? ";
            array.add(like(params[10]));
        }
        return sql;
    }

    @Override
    String between(String startDate, String endDate, List<Object> params) {
        return this.commonBetween(startDate, endDate, params);
    }
}
