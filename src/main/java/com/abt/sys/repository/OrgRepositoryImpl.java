package com.abt.sys.repository;

import com.abt.common.util.TimeUtil;
import com.abt.sys.model.dto.UserRole;
import com.abt.sys.model.entity.Org;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 *
 */
@Repository
@AllArgsConstructor
public class OrgRepositoryImpl implements OrgRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Org> findAllByParentIdOrderByCascadeId(String parentId) {
        String sql = "select * from [dbo].[Org] where ParentId = ? order by CascadeId asc";
        return jdbcTemplate.query(sql, new OrgRowMapper(), parentId);
    }

    class OrgRowMapper implements RowMapper<Org> {

        @Nullable
        @Override
        public Org mapRow(ResultSet rs, int rowNum) throws SQLException {
            Org form = new Org();
            form.setId(rs.getString("Id"));
            form.setCascadeId(rs.getString("CascadeId"));
            form.setName(rs.getString("Name"));
            form.setHotKey(rs.getString("HotKey"));
            form.setParentName(rs.getString("ParentName"));
            form.setIsLeaf(rs.getBoolean("IsLeaf"));
            form.setIsAutoExpand(rs.getBoolean("IsAutoExpand"));
            form.setIconName(rs.getString("IconName"));
            form.setStatus(rs.getInt("Status"));
            form.setBizCode(rs.getString("BizCode"));
            form.setCustomCode(rs.getString("CustomCode"));
            form.setCreateTime(TimeUtil.from(rs.getTimestamp("CreateTime")));
            form.setCreateId(rs.getInt("CreateId"));
            form.setSortNo(rs.getInt("SortNo"));
            form.setParentId(rs.getString("ParentId"));
            form.setTypeName(rs.getString("TypeName"));
            form.setTypeId(rs.getString("TypeId"));
            return form;
        }
    }
}
