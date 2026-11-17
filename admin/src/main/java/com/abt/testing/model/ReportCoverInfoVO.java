package com.abt.testing.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/** 报告封面详情及列表展示对象。 */
@Getter
@Setter
public class ReportCoverInfoVO {
    private String id;
    private String coverName;
    private List<String> checkModuleIds;
    private List<ReportCoverCheckModuleVO> checkModules;
    private String itemCode;
    private String sampleSpec;
    private String sampleStatus;
    private List<String> instruments;
    private List<String> standards;
    private String category;
    private String version;
    private String ownership;
    private String remark;
    private Integer status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createDate;
    private String createUserid;
    private String createUsername;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateDate;
    private String updateUserid;
    private String updateUsername;
}
