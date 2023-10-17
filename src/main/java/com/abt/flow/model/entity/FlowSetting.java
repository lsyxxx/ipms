package com.abt.flow.model.entity;

import com.abt.common.model.AuditInfo;
import com.abt.db.CustomAuditorListener;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Comment;

/**
 * 流程设置参数
 * k-v形式, key可以重复，表示次参数有多个值
 * 比如设置默认的财务总监, key=fiManager, value=userid
 */

@Schema(description = "流程设置参数")
@Table(name = "T_flow_setting")
@Comment("流程设置参数")
@Entity
@Getter
@Setter
@ToString
public class FlowSetting extends AuditInfo {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Schema(description = "PK")
    private String id;

    @Schema(description = "参数key")
    @Column(name = "key_", columnDefinition = "VARCHAR(128)")
    private String key;

    @Schema(description = "参数value")
    @Column(name = "value_", columnDefinition = "VARCHAR(128)")
    private String value;

    @Schema(description = "参数分类")
    @Column(name = "type_", columnDefinition = "VARCHAR(128)")
    private String type;


    @Schema(description = "备注")
    @Column(columnDefinition = "VARCHAR(128)")
    private String remark;

    @Schema(description = "设置说明")
    @Column(columnDefinition = "VARCHAR(128)")
    private String description;


    public FlowSetting(String key, String value) {
        this.key = key;
        this.value = value;
    }


    public FlowSetting() {
        super();
    }
}
