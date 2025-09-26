package com.abt.common.model;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.abt.oa.OAConstants.QUERY_MODE_ALL;

/**
 * client请求参数
 */
@Data
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
    private LocalDate localStartDate;
    /**
     * 结束日期;
     */
    private String endDate;
    private LocalDate localEndDate;
    private String createUserid;

    private boolean enabled;

    private String taskDefKey;
    private String taskName;

    /**
     * 导出模式
     * QUERY_MODE_*
     * 无表示全部
     */
    private String queryMode;

    private String key;



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
        form.setPage(page);
        form.setType(type);
        form.setLimit(limit);
        form.setQuery(query);
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

    public int getSize() {
        return this.limit;
    }

    public boolean isQueryAll() {
        return StringUtils.isBlank(this.queryMode) || QUERY_MODE_ALL.equals(this.queryMode);
    }

    public Pageable createDefaultPageable() {
        return PageRequest.of(this.jpaPage(), this.getLimit(), Sort.by(Sort.Order.desc("createDate")));
    }

    /**
     * 日期的开始时间
     */
    public LocalDateTime toLocalStartTime() {
        return this.localStartDate == null ? null : this.localStartDate.atStartOfDay();
    }

    /**
     * localEndDate 后一天的开始
     */
    public LocalDateTime toLocalEndTime() {
        return this.localEndDate == null ? null : this.localEndDate.plusDays(1).atStartOfDay();
    }

    /**
     * 生成Pageable
     * @param sort 为null，则默认是createDate desc
     * @return PageRequest
     */
    public PageRequest createPageable(Sort sort) {
        if (sort == null) {
            sort = Sort.by(Sort.Order.desc("createDate"));
        }
        return PageRequest.of(this.jpaPage(), this.getLimit(), sort);
    }
}
