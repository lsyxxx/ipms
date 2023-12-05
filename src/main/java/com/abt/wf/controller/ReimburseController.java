package com.abt.wf.controller;

import com.abt.common.model.R;
import com.abt.common.util.TokenUtil;
import com.abt.sys.model.dto.UserView;
import com.abt.wf.model.CommentForm;
import com.abt.wf.model.ReimburseApplyForm;
import com.abt.wf.service.impl.WorkFlowServiceImpl;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 *
 */
@RestController
@Slf4j
@RequestMapping("/test/rbs")
public class ReimburseController {
    private final WorkFlowServiceImpl impl;

    public ReimburseController(WorkFlowServiceImpl impl) {
        this.impl = impl;
    }


    /**
     * 申请流程
     * @param form 申请提交表单
     */
    @PostMapping("/apply")
    public R apply(@Valid @RequestBody ReimburseApplyForm form) {
//        final UserView user = TokenUtil.getUserFromAuthToken();

        return R.success();
    }

    @GetMapping("/applytest")
    public R applyTest(String key, boolean isLeader, double cost) {
        log.info("apply test...");
        Map<String, Object> vars = Map.of(
                "isLeader", isLeader,
                "cost", cost,
                "starter", "user_apply"
        );
        impl.applyTest(key, vars);
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
