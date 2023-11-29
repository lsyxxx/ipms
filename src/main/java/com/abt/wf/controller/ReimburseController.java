package com.abt.wf.controller;

import com.abt.common.util.TokenUtil;
import com.abt.sys.model.dto.UserView;
import com.abt.wf.model.ReimburseApplyForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@RestController
@Slf4j
@RequestMapping("/rbs")
public class ReimburseController {

    /**
     * 申请流程
     */
    @PostMapping("/apply")
    public void apply(ReimburseApplyForm form) {
        final UserView user = TokenUtil.getUserFromAuthToken();


    }

}
