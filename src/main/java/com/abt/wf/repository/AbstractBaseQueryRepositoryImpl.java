package com.abt.wf.repository;

import com.abt.wf.entity.WorkflowBase;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.abt.common.util.TimeUtil;

/**
 * 适用于RequestForm
 */
public abstract class AbstractBaseQueryRepositoryImpl {

    abstract String between(String startDate, String endDate, List<Object> params);

    /**
     * 获取查询结果的列
     */
    public Set<String> getColumnMetaData(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        Set<String> columnNames = new HashSet<>();
        for (int i = 1; i <= columnCount; i++) {
            columnNames.add(metaData.getColumnName(i).toLowerCase());
        }
        return columnNames;
    }

}
