package com.abt.flow.model.entity;

import com.abt.common.model.AuditInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 流程设置参数
 * k-v形式, key可以重复，表示次参数有多个值
 * 比如设置默认的财务总监, key=fiManager, value=userid
 */

@Schema(description = "流程设置参数")
@Table(name = "T_flow_setting")
@Comment("流程设置参数")
@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
public class FlowSetting extends AuditInfo {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Schema(description = "PK")
    private String id;

    @Schema(description = "参数key")
    @Column(columnDefinition = "VARCHAR(128)")
    private String key;

    @Schema(description = "参数value")
    @Column(columnDefinition = "VARCHAR(128)")
    private String value;

    @Schema(description = "参数分类")
    @Column(columnDefinition = "VARCHAR(128)")
    private String type;

    public FlowSetting() {
        super();
    }

    public FlowSetting(String key, String value) {
        this.key = key;
        this.value = value;
    }


}
