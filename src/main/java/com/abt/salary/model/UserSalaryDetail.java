package com.abt.salary.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户工资条详情
 */
@Data
public class UserSalaryDetail {

    /**
     * 标题
     */
    private String label = "";

    /**
     * 对应值
     */
    private String value = "";

    /**
     * 子节点
     */
    private List<UserSalaryDetail> children;

    private int columnIndex;


    public void addChild(UserSalaryDetail child) {
        if (this.children == null) {
            this.children = new ArrayList<UserSalaryDetail>();
        }
        this.children.add(child);
    }

}
