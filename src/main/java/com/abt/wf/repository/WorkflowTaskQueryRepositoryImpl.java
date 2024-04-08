package com.abt.wf.repository;

import com.abt.wf.entity.PayVoucher;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.abt.common.util.QueryUtil.*;

/**
 * 和task关联查询
 * 业务实体统一用e
 */
@AllArgsConstructor
@Component
public class WorkflowTaskQueryRepositoryImpl extends AbstractBaseQueryRepositoryImpl implements WorkflowTaskQueryRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<PayVoucher> findPayVoucherDoneList(int page, int limit, String assigneeId, String assigneeName, String startDate, String endDate,
                                                   String entityIdLike, String state, String project, String contractNo, String contractName) {
        List<Object> params = new ArrayList<>();
        String sql = "select t.ID_ as inv_task_id, t.TASK_DEF_KEY_ as inv_task_def_id, t.NAME_ as inv_task_name, " +
                "? as inv_task_assignee_id, ? as inv_task_assignee_name, " +
                "rt.ID_ as cur_task_id, rt.TASK_DEF_KEY_ as cur_task_def_id, rt.NAME_ as cur_task_name, rt.ASSIGNEE_ as cur_task_assignee_id, " +
                "tu.Name as cur_task_assignee_name, " +
                "e.*,  su.Name as create_username1 " +
                "from ACT_HI_TASKINST t " +
                "left join wf_pay_voucher e on e.proc_inst_id = t.PROC_INST_ID_ " +
                "left join ACT_RU_TASK rt on rt.PROC_INST_ID_ = t.PROC_INST_ID_ " +
                "left join [dbo].[User] su on e.create_userid = su.Id " +
                "left join [dbo].[User] tu on rt.ASSIGNEE_ = tu.Id " +
                "where 1=1 and e.is_del = 0  " +
                "and t.END_TIME_ is not NULL " +
                //仅查询未删除的，去掉apply节点
                "and t.TASK_DEF_KEY_ not like '%apply%' " +
                "and t.PROC_DEF_KEY_ = 'rbsPay' " +
                "and t.assignee_ = ? ";
        params.add(assigneeId);     //? as inv_task_assignee_id
        params.add(assigneeName);   //? as inv_task_assignee_name
        params.add(assigneeId);        //and t.assignee_ = ?

        sql = payVoucherSqlCondition(sql, startDate, endDate, entityIdLike, state, project, contractNo, contractName, params);

        sql += "order by t.START_TIME_ desc ";
        if (!isPaging(limit)) {
            sql += pageSqlBySqlserver(page, limit);
        }
        return jdbcTemplate.query(sql, new PayVoucherTaskQueryRowMapper(), params.toArray());
    }


    @Override
    public List<PayVoucher> findPayVoucherTodoList(int page, int limit, String assigneeId, String assigneeName, String startDate, String endDate,
                                                   String entityIdLike, String state, String project, String contractNo, String contractName) {
        List<Object> params = new ArrayList<>();
        String sql = "select null as inv_task_id, null as inv_task_def_id, null as inv_task_name, " +
                "null as inv_task_assignee_id, null as inv_task_assignee_name, " +
                "t.ID_ as cur_task_id, t.TASK_DEF_KEY_ as cur_task_def_id, t.NAME_ as cur_task_name, t.ASSIGNEE_ as cur_task_assignee_id, " +
                "? as cur_task_assignee_name, " +
                "e.*,  su.Name as create_username1 " +
                "from ACT_RU_TASK t " +
                "left join wf_pay_voucher e on e.proc_inst_id = t.PROC_INST_ID_ " +
                "left join [dbo].[User] su on e.create_userid = su.Id " +
                "where 1=1 " +
                "and e.is_del = 0 " +
                "and t.TASK_DEF_KEY_ not like '%apply%' " +
                //RU_TASK没有proc_def_key
                "and t.PROC_DEF_ID_ like '%rbsPay%' " +
                "and t.assignee_ = ? ";
        params.add(assigneeName); //? as cur_task_assignee_name,
        params.add(assigneeId); //rt.assignee_ = ?
        sql = payVoucherSqlCondition(sql, startDate, endDate, entityIdLike, state, project, contractNo, contractName, params);

        sql += "order by t.CREATE_TIME_ desc ";
        if (!isPaging(limit)) {
            sql += pageSqlBySqlserver(page, limit);
        }

        return jdbcTemplate.query(sql, new PayVoucherTaskQueryRowMapper(), params.toArray());
    }

    @Override
    public List<PayVoucher> findUserApplyList(int page, int limit, String applyUserid, String applyUsername, String startDate, String endDate,
                                              String entityIdLike, String state, String project, String contractNo, String contractName){
        List<Object> params = new ArrayList<>();
        String sql = "select null as inv_task_id, null as inv_task_def_id, null as inv_task_name, " +
                "null as inv_task_assignee_id, null as inv_task_assignee_name, " +
                "t.ID_ as cur_task_id, t.TASK_DEF_KEY_ as cur_task_def_id, t.NAME_ as cur_task_name, t.ASSIGNEE_ as cur_task_assignee_id, " +
                "su.Name as cur_task_assignee_name, " +
                "e.*, e.copy as copy_users,  ? as create_username1 " +
                "from wf_pay_voucher e " +
                "left join ACT_RU_TASK t on e.proc_inst_id = t.PROC_INST_ID_" +
                "left join [dbo].[User] su on t.ASSIGNEE_ = su.Id " +
                "where 1=1 " +
                "and t.PROC_DEF_ID_ like '%rbsPay%' " +
                "and e.is_del = 0 " +
                "and e.create_userid = ? ";
        params.add(applyUsername);
        params.add(applyUserid);
        sql = payVoucherSqlCondition(sql, startDate, endDate, entityIdLike, state, project, contractNo, contractName, params);
        sql += "order by e.create_date desc ";
        if (!isPaging(limit)) {
            sql += pageSqlBySqlserver(page, limit);
        }
        return jdbcTemplate.query(sql, new PayVoucherTaskQueryRowMapper(), params.toArray());
    }

    private String payVoucherSqlCondition(String sql, String startDate, String endDate,
                              String entityIdLike, String state, String project, String contractNo, String contractName, List<Object> params) {
        if (StringUtils.isNotBlank(state)) {
            sql += "and e.biz_state = ? ";
            params.add(state);
        }
        if (StringUtils.isNotBlank(entityIdLike)) {
            sql += "and e.id like ? ";
            params.add(like(entityIdLike));
        }
        if (StringUtils.isNotBlank(project)) {
            sql += "and e.project like ? ";
            params.add(like(project));
        }
        if (StringUtils.isNotBlank(contractNo)) {
            sql += "and e.contract_no like ? ";
            params.add(like(contractNo));
        }
        if (StringUtils.isNotBlank(contractName)) {
            sql += "and e.contract_name like ? ";
            params.add(like(contractName));
        }
        if (StringUtils.isNotBlank(project)) {
            sql += "and e.project like ? ";
            params.add(like(project));
        }

        sql += between(startDate, endDate, params);

        return sql;
    }

    class PayVoucherTaskQueryRowMapper implements RowMapper<PayVoucher> {
        @Nullable
        @Override
        public PayVoucher mapRow(ResultSet rs, int rowNum) throws SQLException {
            PayVoucher form = new PayVoucher();

            form.setId(rs.getString("id"));
            form.setProject(rs.getString("project"));
            form.setPayInvoiceNum(rs.getInt("pay_inv_num"));
            form.setContractName(rs.getString("contract_name"));
            form.setContractDesc(rs.getString("contract_desc"));
            form.setContractNo(rs.getString("contract_no"));
            form.setPayedInvoiceNum(rs.getInt("payed_inv_num"));
            form.setPayDesc(rs.getString("pay_desc"));
            form.setReceiveUser(rs.getString("rec_user"));
            form.setReceiveBank(rs.getString("rec_bank"));
            form.setReceiveAccount(rs.getString("rec_account"));
            form.setPdfFileList(rs.getString("pdf_file"));
            form.setOtherFileList(rs.getString("other_file"));
            form.setCopy(rs.getString("copy_users"));
            form.setManagers(rs.getString("managers"));
            form.setPayAmount(new BigDecimal(rs.getString("pay_amt")));
            if (rs.getString("contract_amt") == null) {
                form.setContractAmount(null);
            }
            if (rs.getString("contract_amt") == null) {
                form.setContractAmount(null);
            }
            WorkflowTaskQueryRepositoryImpl.super.workflowBaseAndTaskSetter(form, rs);
            return form;
        }
    }

    String between(String startDate, String endDate, List<Object> params) {
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
}
