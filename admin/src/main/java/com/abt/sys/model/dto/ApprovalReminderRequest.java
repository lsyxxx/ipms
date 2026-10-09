package com.abt.sys.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** 审批提醒发送请求，接收范围由服务端确定。 */
@Data
public class ApprovalReminderRequest {
    @NotBlank(message = "请输入提醒内容")
    @Size(max = 200, message = "提醒内容不能超过200字")
    private String content;
}
