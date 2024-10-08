package com.abt.common.model;

import com.abt.common.config.CommonJpaAuditListener;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 审计信息
 */
@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
@EntityListeners({AuditingEntityListener.class})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuditInfo {
    @Schema(description = "最后更新时间")
    @LastModifiedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name="update_date")
    private LocalDateTime updateDate;

    @LastModifiedBy
    @Column(name="update_userid")
    private String updateUserid;
    @Column(name="update_username")
    private String updateUsername;

    @CreatedBy
    @Column(name="create_userid")
    private String createUserid;
    @Column(name="create_username")
    private String createUsername;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name="create_date")
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
