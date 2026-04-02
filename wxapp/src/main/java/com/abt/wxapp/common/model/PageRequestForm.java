package com.abt.wxapp.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * 请求参数，带分页
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageRequestForm {
    private String id;

    private String name;

    /**
     * 通用关键字查询
     */
    private String query;

    private LocalDate startDate;
    private LocalDate endDate;


    /**
     * 页码, 0based
     */
    private int page = 0;

    /**
     * 单页数量，默认20
     */
    private int limit = 20;

}
