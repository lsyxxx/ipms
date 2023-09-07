package com.abt.common.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * client请求参数
 */
@Data
@Accessors(chain = true)
public class RequestForm {

    private int page = 0;
    private int size = 0;

    /**
     * 按id查询
     */
    private String id;
    /**
     * 按类型查询时
     */
    private String type;

    /**
     * 搜索参数
     */
    private String query;

    /**
     * 列表数据count
     */
    private int count;

    /**
     * 是否分页
     * @return
     */
    public boolean isPaging() {
        return !noPaging();
    }

    /**
     * 没分页
     * @return
     */
    public boolean noPaging() {
       return (page == 0) && (size == 0);
    }

}
