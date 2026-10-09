package com.abt.sys.controller;

import com.abt.common.model.R;
import com.abt.common.util.TokenUtil;
import com.abt.sys.model.dto.ApprovalReminderRequest;
import jakarta.validation.Valid;
import com.abt.sys.model.dto.SystemMessageRequestForm;
import com.abt.sys.model.entity.SystemMessage;
import com.abt.sys.service.SystemMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统消息
 */
@RestController
@Slf4j
@RequestMapping("/sysmsg")
public class SystemMessageController {

    private final SystemMessageService systemMessageService;

    /** 向全部在职且未停用员工发送个人重要消息。 */
    @PostMapping("/approval-reminder")
    public R<Integer> sendApprovalReminder(@Valid @RequestBody ApprovalReminderRequest request) {
        int count = systemMessageService.sendApprovalReminder(request.getContent());
        return R.success(count, "已向" + count + "位员工发送审批提醒");
    }

    public SystemMessageController(SystemMessageService systemMessageService) {
        this.systemMessageService = systemMessageService;
    }

    @GetMapping("/find")
    public R<List<SystemMessage>> findByLoginUser(@ModelAttribute SystemMessageRequestForm requestForm) {
        if (requestForm.isLoginUser()) {
            requestForm.setToId(TokenUtil.getUseridFromAuthToken());
        }
        if (requestForm.isTestTask()) {
            final Page<SystemMessage> page = systemMessageService.findUserTestTaskMessages(requestForm);
            return R.success(page.getContent(), (int)page.getTotalElements(), "查询成功!");
        } else {
            final Page<SystemMessage> page = systemMessageService.findUserCommonMessages(requestForm);
            return R.success(page.getContent(), (int)page.getTotalElements(), "查询成功!");
        }
    }

    @GetMapping("/read/all")
    public R<Object> setReadAll() {
        systemMessageService.readAll(TokenUtil.getUseridFromAuthToken());
        return R.success(String.format("用户[%s]消息已全部设置已读", TokenUtil.getUseridFromAuthToken()));
    }


    @GetMapping("/read/{id}")
    public R<Object> readOne(@PathVariable   String id) {
        systemMessageService.readOne(id);
        return R.success("消息已读!");
    }

}
