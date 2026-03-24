package com.abt.chkmodule.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static com.abt.chkmodule.Constant.USE_CHANNEL_WEB;
import static com.abt.chkmodule.Constant.USE_CHANNEL_WX;

/**
 * 检测分类
 */
@Getter
@Setter
@Entity
@Table(name = "check_unit")
public class CheckUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, length = 128)
    private String id;

    @Column(name="code",  nullable = false, length = 32)
    private String code;

    @Column(name="name", nullable = false, length = 128)
    private String name;

    /**
     * 使用渠道, USE_CHANNEL_WXAPP/WEB
     */
    @Column(name="use_chn",  nullable = false, length = 16)
    private String useChannel;

    /**
     * 依据标准号
     */
    @Column(name="stds")
    private List<CheckStandard> standards;

    public boolean isWxChannel() {
        return USE_CHANNEL_WX.equals(useChannel);
    }
    public boolean isWebChannel() {
        return USE_CHANNEL_WEB.equals(useChannel);
    }
}
