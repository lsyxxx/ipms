package com.abt.common.util;


import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

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

}
