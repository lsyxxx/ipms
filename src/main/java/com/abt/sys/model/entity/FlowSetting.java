package com.abt.sys.model.entity;

import com.abt.common.model.AuditInfo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Comment;

/**
 * 流程设置参数
 * k-v形式, key可以重复，表示次参数有多个值
 * 比如设置默认的财务总监, key=fiManager, value=userid
 */

@Table(name = "t_sys_setting")
@Comment("流程设置参数")
@Entity
@Getter
@Setter
@ToString
public class FlowSetting extends AuditInfo {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "key_", columnDefinition = "VARCHAR(128)")
    private String key;

    @Column(name = "value_", columnDefinition = "VARCHAR(128)")
    private String value;

    @Column(name = "type_", columnDefinition = "VARCHAR(128)")
    private String type;

    @Column(columnDefinition = "VARCHAR(128)")
    private String remark;

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
