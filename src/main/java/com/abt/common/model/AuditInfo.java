package com.abt.common.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 审计信息
 */
@Data
public class AuditInfo {
    @Schema(description = "最后更新时间")
//    @Column(name = "update_date", columnDefinition = "DATETIME")
    @LastModifiedDate
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime updateDate;
    private String updateUserid;
    private String updateUsername;
    private String createUserid;
    private String createUsername;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createDate;


    /**
     * 创建
     * @param userid 用户id
     * @param createUsername 用户名
     */
    public void create(String userid, String createUsername) {
        this.createUserid = userid;
        this.createUsername = createUsername;
        this.createDate = LocalDateTime.now();
        this.updateDate = this.createDate;
        this.updateUserid = userid;
        this.updateUsername = createUsername;
    }

    public void update(String userid, String username) {
        this.updateUsername = username;
        this.updateUserid = userid;
        this.updateDate = LocalDateTime.now();
    }
}
