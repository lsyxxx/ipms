package com.abt.wf.controller;

import com.abt.common.model.R;
import com.abt.common.util.TokenUtil;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.dto.UserView;
import com.abt.wf.model.TripReimburseForm;
import com.abt.wf.model.TripRequestForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 差旅报销
 */
@RestController
@Slf4j
@RequestMapping("/wf/trip")
public class TripController {



    /**
     * 申请
     * @param form 申请表单
     */
    @PostMapping("/apply")
    public void apply(@RequestBody TripReimburseForm form) {
        if (CollectionUtils.isEmpty(form.getItems())) {
            throw new BusinessException("请输入差旅报销明细信息!");
        }

        UserView user = TokenUtil.getUserFromAuthToken();
        form.getCommon().setCreateUserid(user.getId());
        form.getCommon().setCreateUsername(user.getUsername());

    }

    @PostMapping("/approve")
    public void approve(@RequestBody TripReimburseForm form) {

    }

    /**
     * 差旅报销记录
     * @param criteria 搜索条件
     */
    @PostMapping("/all")
    public void getAll(@ModelAttribute TripRequestForm criteria) {

    }

    @GetMapping("/load/{entityId}")
    public R<TripReimburseForm> load(@PathVariable String entityId) {

        return null;
    }





}
