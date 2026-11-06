package com.abt.oa.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

/** 用车接口通用审计展示，不承担实体持久化职责。 */
@Getter
@Setter
public class FieldVehicleAuditVO {
    private String createUserid;
    private String createUsername;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createDate;
    private String updateUserid;
    private String updateUsername;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateDate;
}
