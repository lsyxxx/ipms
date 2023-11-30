package com.abt.wf.controller;

import com.abt.common.model.R;
import com.abt.common.util.TokenUtil;
import com.abt.sys.model.dto.UserView;
import com.abt.wf.model.CommentForm;
import com.abt.wf.model.ReimburseApplyForm;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
     * @param form 申请提交表单
     */
    @PostMapping("/apply")
    public R apply(@Valid @RequestBody ReimburseApplyForm form) {
        final UserView user = TokenUtil.getUserFromAuthToken();

        return R.success();
    }

    /**
     * 审批
     * @param form 审批表单
     */
    public R approve(CommentForm form) {
        final UserView user = TokenUtil.getUserFromAuthToken();

        return R.success();
    }

}
