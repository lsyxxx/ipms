package com.abt.flow.repository;

import com.abt.common.util.TimeUtil;
import com.abt.flow.model.entity.FlowScheme;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 *
 */
@Component
public class FlowSchemeRepositoryImpl implements FlowSchemeRepository{
    private final JdbcTemplate jdbcTemplate;

    public FlowSchemeRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public FlowScheme findById(String id) {
        return jdbcTemplate.queryForObject("SELECT * FROM FlowScheme WHERE Id = ?", new FlowSchemeRowMapper(), id);
    }

    @Override
    public List<FlowScheme> findAllEnabled() {
        return jdbcTemplate.query("SELECT * FROM FlowScheme WHERE DeleteMark = 0 AND Disabled = 0", new FlowSchemeRowMapper());
    }

    class FlowSchemeRowMapper implements RowMapper<FlowScheme> {

        @Nullable
        @Override
        public FlowScheme mapRow(ResultSet rs, int rowNum) throws SQLException {
            FlowScheme flowScheme = new FlowScheme();
            flowScheme.setId(rs.getString("Id"));
            flowScheme.setSchemeCode(rs.getString("SchemeCode"));
            flowScheme.setSchemeName(rs.getString("SchemeName"));
            flowScheme.setSchemeContent(rs.getString("SchemeContent"));
            flowScheme.setSchemeType(rs.getString("SchemeType"));
            flowScheme.setSchemeVersion(rs.getString("SchemeVersion"));
            flowScheme.setSchemeCanUser(rs.getString("SchemeCanUser"));
            flowScheme.setFrmId(rs.getString("FrmId"));
            flowScheme.setFrmType(rs.getInt("FrmType"));
            flowScheme.setAuthorizeType(rs.getInt("AuthorizeType"));
            flowScheme.setSortCode(rs.getInt("SortCode"));
            flowScheme.setDeleteMark(rs.getBoolean("DeleteMark"));
            flowScheme.setDisabled(rs.getBoolean("Disabled"));
            flowScheme.setDescription(rs.getString("Description"));
            flowScheme.setCreateDate(TimeUtil.from(rs.getTimestamp("CreateDate")));
            flowScheme.setCreateUserId(rs.getString("CreateUserId"));
            flowScheme.setCreateUserName(rs.getString("CreateUserName"));
            flowScheme.setModifyDate(TimeUtil.from(rs.getTimestamp("ModifyDate")));
            flowScheme.setModifyUserId(rs.getString("ModifyUserId"));
            flowScheme.setModifyUserName(rs.getString("ModifyUserName"));
            flowScheme.setOrgId(rs.getString("OrgId"));
            flowScheme.setSchemeName1(rs.getString("SchemeName1"));
            flowScheme.setProcessDefId(rs.getString("ProcDefId"));
            flowScheme.setService(rs.getString("Service"));
            return flowScheme;
        }
    }

}



