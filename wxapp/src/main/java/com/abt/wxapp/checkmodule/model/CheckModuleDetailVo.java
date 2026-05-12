package com.abt.wxapp.checkmodule.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 检测项目详情（供小程序详情页展示，字段来自后端实体）
 */
@Getter
@Setter
public class CheckModuleDetailVo {
    private String id;
    private String name;
    private String image;
    private String durationText;
    private String intro;
    private String notice;
    private String sampleRequirementText;
    private List<String> sampleRequirementImages;
    private String resultDisplayText;
    private List<String> resultDisplayImages;
    private List<Object> instruments;
    private Object testFlow;
    private List<TestParameterVo> testParameters;
    private List<Object> standards;
    private List<String> certificate;
    private boolean hasCMA;
    private boolean hasCNAS;

    @Getter
    @Setter
    public static class TestParameterVo {
        private String name;
        private String restrict;
        private List<String> standardCodes;
    }
}
