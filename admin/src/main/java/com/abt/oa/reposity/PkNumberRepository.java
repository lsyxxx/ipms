package com.abt.oa.reposity;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** 既有 T_PK_CONFIG、T_PK 的参数化访问，不使用吞掉异常的旧取号接口。 */
@Repository
public class PkNumberRepository {
    private final JdbcTemplate jdbc;

    public PkNumberRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public record Rule(String prefix, String usePrefix, String useDate, String useServer,
                       String useGuid, BigDecimal length, String monthDay) { }
    public record Counter(String id, BigDecimal number, String rowId) { }

    public List<Rule> findRules(String name) {
        return jdbc.query("SELECT Prefix, Isprefix, Isdate, Isserverid, Isguid, Flen, isMonthDay " +
                        "FROM T_PK_CONFIG WHERE Tablename = ?",
                (rs, row) -> new Rule(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                        rs.getString(5), rs.getBigDecimal(6), rs.getString(7)), name);
    }

    public List<String> findCurrentServers() {
        return jdbc.query("SELECT Serverid FROM T_SRV_LIST WHERE Iscurrent = '1'", (rs, row) -> rs.getString(1));
    }

    public List<Counter> findCounters(String name, String prefix, String date, String server) {
        StringBuilder sql = new StringBuilder("SELECT FID, SN, MYROWID FROM T_PK WHERE Tablename = ?");
        List<Object> args = new ArrayList<>(); args.add(name);
        if (prefix != null) { sql.append(" AND Prefix = ?"); args.add(prefix); }
        if (date != null) { sql.append(" AND Curdate = ?"); args.add(date); }
        if (server != null) { sql.append(" AND Serverid = ?"); args.add(server); }
        return jdbc.query(sql.toString(), (rs, row) -> new Counter(rs.getString(1), rs.getBigDecimal(2), rs.getString(3)), args.toArray());
    }

    public int insert(String id, String name, String prefix, String date, String server,
                      BigDecimal next, String rowId, LocalDateTime now) {
        return jdbc.update("INSERT INTO T_PK (FID, TABLENAME, PREFIX, CURDATE, SERVERID, SN, MYROWID, OPERATEDATE, UPFLAG) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, '0')", id, name, prefix, date, server, next, rowId, now);
    }

    public int update(Counter previous, BigDecimal next, String rowId, LocalDateTime now) {
        return jdbc.update("UPDATE T_PK SET SN = ?, MYROWID = ?, OPERATEDATE = ?, UPFLAG = '1' WHERE FID = ? AND SN = ?",
                next, rowId, now, previous.id(), previous.number());
    }

    public boolean matches(String id, BigDecimal next, String rowId) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM T_PK WHERE FID = ? AND SN = ? AND MYROWID = ?",
                Integer.class, id, next, rowId);
        return Integer.valueOf(1).equals(count);
    }
}
