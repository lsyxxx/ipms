package com.abt.sys.model.entity;

import com.abt.common.model.AuditInfo;
import com.abt.common.model.RequestFile;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 文件系统
 */


@Schema(description = "文件")
@Table(name = "T_sys_file")
@Comment("系统文件 ")
@Entity
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class SystemFile extends  AuditInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Schema(description = "PK")
    private String id;

    @Schema(description = "上传文件名")
    @Column(columnDefinition = "VARCHAR(128)")
    private String name;

    @Schema(description = "原始文件名")
    @Column(name = "org_name", columnDefinition = "VARCHAR(128)")
    private String originalName;

    @Schema(description = "文件描述")
    @Column(columnDefinition = "VARCHAR(128)")
    private String description;

    @Schema(description = "完整的保存路径")
    @Column(columnDefinition = "VARCHAR(128)")
    private String url;

    @Schema(description = "文件类型/后缀. eg: png, pdf")
    @Column(columnDefinition = "VARCHAR(64)")
    private String type;

    @Schema(description = "所属服务/模块id/路径, eg: finance财务,flow流程")
    @Column(columnDefinition = "VARCHAR(64)")
    private String service;

    @Schema(description = "业务类型")
    @Column(columnDefinition = "VARCHAR(128)")
    private String bizType;

    @Schema(description = "关联ID1")
    @Column(name = "rel_id1", columnDefinition = "VARCHAR(256)")
    private String relationId1;

    @Schema(description = "关联ID2")
    @Column(name = "rel_id2", columnDefinition = "VARCHAR(256)")
    private String relationId2;


    @Schema(description = "是否删除")
    @Column(name = "is_del", columnDefinition = "BIT")
    private boolean isDeleted = false;


    public SystemFile(RequestFile requestFile) {
        this.bizType = requestFile.getBizType();
        this.name = requestFile.getFileName();
        this.service = requestFile.getService();
        this.relationId1 = requestFile.getRelationId1();
        this.relationId2 = requestFile.getRelationId2();
    }
}
