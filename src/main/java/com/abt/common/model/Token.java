package com.abt.common.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 表单请求token
 */
@Getter
@Setter
public class Token implements IToken {
    private String token;
    private String service;
}
