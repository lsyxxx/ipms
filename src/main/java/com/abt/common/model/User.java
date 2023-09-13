package com.abt.common.model;

import lombok.Data;

/**
 *
 */
@Data
public class User<ID> {

    /**
     * 用户唯一ID
     */
    private ID userid;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 编号，比如工号
     */
    private String code;
}
