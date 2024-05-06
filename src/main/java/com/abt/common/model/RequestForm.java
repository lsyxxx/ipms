package com.abt.common.model;

import com.abt.sys.model.dto.UserView;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * client请求参数
 */
@Data
@Accessors(chain = true)
public class RequestForm {

    public static final int PAGE_LIMIT = 999;
    public static final int DEFAULT_LIMIT = 20;

    /**
     * 分页页数
     * based = 1
     */
    private int page = 1;

    /**
     * 单页数量
     */
    private int limit = 0;

    /**
     * 按id查询
     */
    private String id;

    private String name;
    /**
     * 按类型查询时
     */
    private String type;

    /**
     * 搜索参数
     */
    private String query;

    /**
     * 状态
     */
    private String state;

    /**
     * 列表数据count
     */
    private int count;

    private int firstResult;
    private String userid;
    private String username;


    /**
     * 开始日期
     */
    private String startDate;
    /**
     * 结束日期;
     */
    private String endDate;
    private String createUserid;

    /**
     * 是否分页
     */
    public boolean isPaging() {
        return !noPaging();
    }

    /**
     * 没分页
     */
    public boolean noPaging() {
       return limit == 0;
    }


    public static RequestForm of (int page, int limit, String type, String query) {
        RequestForm form = new RequestForm();
        if (type == null) {
            type  = "";
        }
        form.setPage(page).setType(type).setLimit(limit).setQuery(query);
        return form;
    }

    public int getFirstResult() {
        this.firstResult = (this.page - 1) * limit;
        return this.firstResult;
    }

    public static RequestForm createNoPaging() {
        RequestForm form = new RequestForm();
        form.setLimit(PAGE_LIMIT);
        return form;
    }

    public RequestForm setDefaultLimit() {
        this.limit = DEFAULT_LIMIT;
        return this;
    }

    /**
     * 强制分页，如果没有传入Limit则使用默认。防止有些循环查询数据太大，或者使用分页查询limit=0无法查询
     */
    public RequestForm forcePaged() {
        if (this.noPaging()) {
            this.setDefaultLimit();
        }
        return this;
    }

    public void setNoPaging() {
        this.limit = 0;
    }

    /**
     * jpa分页用的page, base 0
     */
    public int jpaPage() {
        return this.page - 1;
    }

}
