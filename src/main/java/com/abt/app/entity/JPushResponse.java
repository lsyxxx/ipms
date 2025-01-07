package com.abt.app.entity;

import lombok.*;

/**
 * jpush rest api返回的成功信息
 */
@Getter
@Setter
@NoArgsConstructor
public class JPushResponse {
    private String sendno;
    private String msg_id;
}


