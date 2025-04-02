package com.abt.sys.controller;

import com.abt.common.model.R;
import com.abt.common.util.TokenUtil;
import com.abt.sys.model.dto.SystemMessageRequestForm;
import com.abt.sys.model.entity.SystemMessage;
import com.abt.sys.service.SystemMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 系统消息
 */
@RestController
@Slf4j
@RequestMapping("/sysmsg")
public class SystemMessageController {

    private final SystemMessageService systemMessageService;

    public SystemMessageController(SystemMessageService systemMessageService) {
        this.systemMessageService = systemMessageService;
    }

    @GetMapping("/find/loginuser")
    public R<List<SystemMessage>> findByLoginUser(@ModelAttribute SystemMessageRequestForm requestForm) {
        requestForm.setToId(TokenUtil.getUseridFromAuthToken());
        final Page<SystemMessage> page = systemMessageService.findUserSystemMessagesAllPageable(requestForm);
        return R.success(page.getContent(), (int)page.getTotalElements(), "查询成功!");

    }

}
