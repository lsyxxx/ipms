package com.abt.chkmodule.entity;

import com.abt.chkmodule.model.ChannelEnum;
import com.abt.common.AuditInfo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 检测分类
 */
@Getter
@Setter
@Entity
@Table(name = "check_unit")
public class CheckUnit extends AuditInfo implements UseChannel{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_", nullable = false, length = 128)
    private String id;

    @Column(name="code_",  nullable = false, length = 32)
    private String code;

    @Column(name="name_", nullable = false, length = 128)
    private String name;

    /**
     * 使用渠道, USE_CHANNEL_WXAPP/WEB
     */
    @Enumerated(EnumType.STRING)
    @Column(name="use_chn",  nullable = false, length = 16)
    private ChannelEnum useChannel;

    /**
     * 是否启用
     */
    @Column(name="enabled", columnDefinition = "BIT")
    private boolean enabled = true;

    @Override
    public ChannelEnum getChannel() {
        return this.useChannel;
    }
}
