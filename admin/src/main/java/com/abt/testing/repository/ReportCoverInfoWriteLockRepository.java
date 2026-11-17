package com.abt.testing.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Types;

/** 使用 SQL Server 2014 已支持的事务级应用锁串行化封面写操作。 */
@Repository
@RequiredArgsConstructor
public class ReportCoverInfoWriteLockRepository {
    private final JdbcTemplate jdbcTemplate;

    /** 必须在 service 的写事务内调用，提交或回滚时由数据库自动释放。 */
    public Integer acquireWriteLock() {
        return jdbcTemplate.execute(connection -> {
            var statement = connection.prepareCall("{? = CALL sys.sp_getapplock(?, ?, ?, ?)}");
            statement.registerOutParameter(1, Types.INTEGER);
            statement.setNString(2, "ipms.report_cover_info.write");
            statement.setString(3, "Exclusive");
            statement.setString(4, "Transaction");
            statement.setInt(5, 10000);
            return statement;
        }, (CallableStatementCallback<Integer>) statement -> {
            statement.execute();
            return statement.getInt(1);
        });
    }
}
