package com.abt.common.util;


import com.abt.common.exception.MissingRequiredParameterException;
import org.apache.commons.lang3.StringUtils;

/**
 * 查询工具
 */
public class QueryUtil {

    public static final int NO_PAGING = 0;

    /**
     * 是否分页
     * @param limit 单页数量
     * @return: true: 分页
     *          false: 不分页
     */
    public static boolean isPaging(int limit) {
        return limit > NO_PAGING;
    }

    /**
     * sqlserver 分页sql
     * @param page 页数, 从1开始
     * @param size 单页数量
     */
    public static String pageSqlBySqlserver(int page, int size) {
        int skip = (page - 1) * size;
        return " offset " + skip + " rows fetch next " + size + " rows only ";
    }

    public static String like(String param) {
        return "%" + param + "%";
    }

    public static String sql(String sql) {
        return " " + sql + " ";
    }

    public static void ensureProperty(String prop, String propName) {
        if (StringUtils.isBlank(prop)) {
            throw new MissingRequiredParameterException("缺少必要参数(" + propName + ")");
        }
    }

}
