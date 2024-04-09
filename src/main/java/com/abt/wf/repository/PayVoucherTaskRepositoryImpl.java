package com.abt.wf.repository;

import com.abt.wf.entity.Loan;
import com.abt.wf.entity.PayVoucher;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class PayVoucherTaskRepositoryImpl extends AbstractBaseQueryRepositoryImpl implements PayVoucherTaskRepository {

    private final JdbcTemplate jdbcTemplate;
    public static final String TABLE_ENTITY = "wf_pay_voucher";

    @Override
    public List<PayVoucher> findPayVoucherDoneList(int page, int limit, String assigneeId, String assigneeName, String startDate, String endDate,
                                                   String entityIdLike, String state, String project, String contractNo, String contractName) {
        List<Object> params = new ArrayList<>();

        String sql = doneSql(TABLE_ENTITY);
        sql = sql + "and t.PROC_DEF_KEY_ = 'rbsPay' and t.assignee_ = ? ";
        params.add(assigneeId);     //? as inv_task_assignee_id
        params.add(assigneeName);   //? as inv_task_assignee_name
        params.add(assigneeId);        //and t.assignee_ = ?

        sql = conditionSql(sql, params, startDate, endDate, entityIdLike, state, project, contractNo, contractName);

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
        String sql = todoSql(TABLE_ENTITY);
        sql = sql + "and t.PROC_DEF_ID_ like '%rbsPay%' and t.assignee_ = ? ";
        params.add(assigneeName); //? as cur_task_assignee_name,
        params.add(assigneeId); //rt.assignee_ = ?
        sql = conditionSql(sql, params, state, entityIdLike, project, contractNo, contractName, startDate, endDate);

        sql += "order by t.CREATE_TIME_ desc ";
        if (!isPaging(limit)) {
            sql += pageSqlBySqlserver(page, limit);
        }

        return jdbcTemplate.query(sql, new PayVoucherTaskQueryRowMapper(), params.toArray());
    }

    @Override
    public List<PayVoucher> findPayVoucherUserApplyList(int page, int limit, String applyUserid, String applyUsername, String startDate, String endDate,
                                                        String entityIdLike, String state, String project, String contractNo, String contractName){
        List<Object> params = new ArrayList<>();
        String sql = applySql(TABLE_ENTITY);
        sql = sql + "and t.PROC_DEF_ID_ like '%rbsPay%' and e.create_userid = ? ";
        params.add(applyUsername);
        params.add(applyUserid);
        sql = conditionSql(sql, params, startDate, endDate, entityIdLike, state, project, contractNo, contractName);
        sql += "order by e.create_date desc ";
        if (!isPaging(limit)) {
            sql += pageSqlBySqlserver(page, limit);
        }
        return jdbcTemplate.query(sql, new PayVoucherTaskQueryRowMapper(), params.toArray());
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
            workflowBaseAndTaskSetter(form, rs);
            return form;
        }
    }


    @Override
    String conditionSql(String sql, List<Object> array, String... params) {
        if (StringUtils.isNotBlank(params[0])) {
            sql += "and e.biz_state = ? ";
            array.add(params[0]);
        }
        if (StringUtils.isNotBlank(params[1])) {
            sql += "and e.id like ? ";
            array.add(like(params[1]));
        }
        if (StringUtils.isNotBlank(params[2])) {
            sql += "and e.project like ? ";
            array.add(like(params[2]));
        }
        if (StringUtils.isNotBlank(params[3])) {
            sql += "and e.contract_no like ? ";
            array.add(like(params[3]));
        }
        if (StringUtils.isNotBlank(params[4])) {
            sql += "and e.contract_name like ? ";
            array.add(like(params[4]));
        }

        sql += between(params[5], params[6], array);

        return sql;
    }

    @Override
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
