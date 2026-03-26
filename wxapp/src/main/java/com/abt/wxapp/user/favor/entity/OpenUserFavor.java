package com.abt.wxapp.user.favor.entity;

import com.abt.common.AuditInfo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 用户个人收藏
 */
@Getter
@Setter
@Entity
@Table(name = "open_user_favor")
public class OpenUserFavor extends AuditInfo {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, length = 128)
    private String id;

    /**
     * 关联的用户ID
     */
    @Column(name = "user_id", nullable = false, length = 128)
    private String userId;

    /**
     * 关联的检测项目ID
     */
    @Column(name = "cm_id", nullable = false, length = 128)
    private String checkModuleId;

}