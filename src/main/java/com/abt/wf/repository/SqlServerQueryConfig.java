package com.abt.wf.repository;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @description Sqlserver 查询配置
 */
@Component
@Data
public class SqlServerQueryConfig {

    /**
     * 每次查询最大条数
     */
    @Value("${abt.sql.max}")
    private int maxItemsPerSql;

    public String selectTop() {
        return "select top " + maxItemsPerSql + " ";
    }

}
