package com.abt.wxapp.checkmodule.model;

import com.abt.instrument.model.Instrument;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 检测项目
 */
@Getter
@Setter
public class CheckModule {

    /**
     * 唯一id
     */
    private String id;

    /**
     * 名称
     */
    private String name;

    /**
     * 封面图片url
     */
    private String coverImage;

    /**
     * 一般工作时间
     */
    private String duration;

    /**
     * 项目介绍
     */
    private String introduce;

    /**
     * 结果展示
     */
    private String resultDisplayText;

    /**
     * 结果展示附图url列表
     * 可多图，可无
     */
    private List<String> resultDisplayImages;

    /**
     * 实验仪器
     */
    private List<Instrument>  instruments;


}
