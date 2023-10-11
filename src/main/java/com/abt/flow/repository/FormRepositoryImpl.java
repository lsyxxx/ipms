package com.abt.flow.repository;

import com.abt.common.util.TimeUtil;
import com.abt.flow.model.entity.Form;
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
public class FormRepositoryImpl implements FormRepository{

    private final JdbcTemplate jdbcTemplate;

    public FormRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Form findById(String id) {
        return jdbcTemplate.queryForObject("select * from Form where Id = ?", new FormRowMapper(), id);
    }


    class FormRowMapper implements RowMapper<Form> {

        @Nullable
        @Override
        public Form mapRow(ResultSet rs, int rowNum) throws SQLException {
            Form form = new Form();
            form.setId(rs.getString("Id"));
            form.setName(rs.getString("Name"));
            form.setFrmType(rs.getInt("FrmType"));
            form.setWebId(rs.getString("WebId"));
            form.setFields(rs.getInt("Fields"));
            form.setContentData(rs.getString("ContentData"));
            form.setContentParse(rs.getString("ContentParse"));
            form.setContent(rs.getString("Content"));
            form.setSortCode(rs.getInt("SortCode"));
            form.setDeleteMark(rs.getBoolean("DeleteMark"));
            form.setDbName(rs.getString("DbName"));
            form.setDisabled(rs.getBoolean("Disabled"));
            form.setDescription(rs.getString("Description"));
            form.setCreateDate(TimeUtil.from(rs.getTimestamp("CreateDate")));
            form.setCreateUserId(rs.getString("CreateUserId"));
            form.setCreateUserName(rs.getString("CreateUserName"));
            form.setModifyDate(TimeUtil.from(rs.getTimestamp("ModifyDate")));
            form.setModifyUserId(rs.getString("ModifyUserId"));
            form.setModifyUserName(rs.getString("ModifyUserName"));
            form.setOrgId(rs.getString("OrgId"));


            return form;
        }
    }
}
