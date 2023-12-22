package com.abt.common.model;

import com.abt.sys.model.dto.UserView;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * client请求参数
 */
@Data
@Accessors(chain = true)
public class RequestForm {

    public static final int PAGE_LIMIT = 999;

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
     * 列表数据count
     */
    private int count;

    private int firstResult;

    private User user;

    /**
     * 开始日期
     */
    private String startDate;
    /**
     * 结束日期;
     */
    private String endDate;

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

    public void setUser(UserView userView) {
        this.user = new User(userView);
    }


    public String getUserid() {
        if (this.user == null) {
            return "";
        }
        return this.user.getId();
    }

    public String getUsername() {
        if (this.user == null) {
            return "";
        }
        return this.user.getUsername();
    }

    public String getUserCode() {
        if (this.user == null) {
            return "";
        }
        return this.user.getCode();
    }

    public static RequestForm createNoPaging() {
        RequestForm form = new RequestForm();
        form.setLimit(PAGE_LIMIT);
        return form;
    }

}
