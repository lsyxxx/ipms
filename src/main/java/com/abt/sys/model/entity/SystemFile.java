package com.abt.sys.model.entity;

import com.abt.common.model.AuditInfo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 文件系统
 */


@Schema(description = "文件")
@Table(name = "T_sys_file")
@Comment("流文件")
@Entity
@NoArgsConstructor
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class SystemFile extends  AuditInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Schema(description = "PK")
    private String id;



    @Schema(description = "文件名")
    @Column(columnDefinition = "VARCHAR(128)")
    private String name;

    @Schema(description = "完整的保存路径")
    @Column(columnDefinition = "VARCHAR(128)")
    private String url;

    @Schema(description = "文件类型/后缀. eg: png, pdf")
    @Column(columnDefinition = "VARCHAR(64)")
    private String type;

    @Schema(description = "所属服务id, eg: finance财务,flow流程")
    @Column(columnDefinition = "VARCHAR(64)")
    private String service;

    @Schema(description = "业务类型")
    @Column(columnDefinition = "VARCHAR(128)")
    private String bizType;

}
