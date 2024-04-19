package com.abt.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 返回分页数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Page<T> {

    /**
     * 当前分页list
     */
    private List<T> content = new ArrayList<>();
    /**
     * 总条数
     */
    private int total = 0;


}
