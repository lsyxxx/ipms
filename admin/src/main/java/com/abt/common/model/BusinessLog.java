package com.abt.common.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 业务log 基类
 * 用来在页面展示操作记录
 */
@Data
abstract public class BusinessLog {

    /**
     * 操作用户
     */
    private String user;
    /**
     * 操作动作
     */
    private String action;
    /**
     * 操作时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTime;

    /**
     * 展示Log信息
     * @return
     */
    protected abstract String simpleLog();

    /**
     * 从LocalDateTime获取date
     * @return
     */
    public String toLocalDate() {
        return dateTime.toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }


}
