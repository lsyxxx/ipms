package com.abt.sys.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Nationalized;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@Getter
@Setter
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "SysMessage")
public class SystemMessage {
    @Id
    @Column(name = "Id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Size(max = 20)
    @Nationalized
    @Column(name = "TypeName", length = 20)
    private String typeName;

    @Column(name = "TypeId")
    private String typeId;

    @Column(name = "FromId")
    private String fromId;

    @NotNull
    @Column(name = "ToId", nullable = false)
    private Object toId;

    @Size(max = 50)
    @Nationalized
    @Column(name = "FromName", length = 50)
    private String fromName;

    @Size(max = 50)
    @Nationalized
    @Column(name = "ToName", length = 50)
    private String toName;

    /**
     * -1:已删除；0:默认
     */
    @NotNull
    @ColumnDefault("0")
    @Column(name = "FromStatus", nullable = false)
    private Integer fromStatus;
    public static final Integer FROM_STATUS_DEL = -1;
    public static final Integer FROM_STATUS_DEFAULT = 0;

    /**
     * -1:已删除；0:默认未读；1：已读
     */
    @NotNull
    @ColumnDefault("0")
    @Column(name = "ToStatus", nullable = false)
    private Integer toStatus;

    public static final Integer TO_STATUS_DEL = -1;
    public static final Integer TO_STATUS_UNREAD = 0;
    public static final Integer TO_STATUS_READ = 1;

    @Size(max = 200)
    @Column(name = "Href", length = 200)
    private String href;

    @Size(max = 200)
    @Nationalized
    @Column(name = "Title", length = 200)
    private String title;

    @Size(max = 1000)
    @Nationalized
    @Column(name = "Content", length = 1000)
    private String content;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ssd")
    @ColumnDefault("getdate()")
    @Column(name = "CreateTime", nullable = false)
    private Instant createTime;

    @CreatedBy
    @Column(name = "CreateId")
    private String createId;

    /**
     * 接收用户已删除
     */
    public void doDelete() {
        this.toStatus = TO_STATUS_DEL;
    }

    /**
     * 接收用户已读
     */
    public void doRead() {
        this.toStatus = TO_STATUS_READ;
    }

}