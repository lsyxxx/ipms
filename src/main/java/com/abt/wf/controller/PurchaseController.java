package com.abt.wf.controller;

import com.abt.common.model.R;
import com.abt.common.util.TokenUtil;
import com.abt.sys.model.dto.UserView;
import com.abt.wf.entity.PurchaseApplyMain;
import com.abt.wf.model.PurchaseApplyRequestForm;
import com.abt.wf.model.UserTaskDTO;
import com.abt.wf.service.PurchaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 采购流程
 */
@RestController
@Slf4j
@RequestMapping("/purchase")
public class PurchaseController {

    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }


    @PostMapping("/apply")
    public R<Object> apply(@RequestBody PurchaseApplyMain form) {
        purchaseService.apply(form);
        return R.success("提交采购申请成功");
    }

    @GetMapping("/preview")
    public R<List<UserTaskDTO>> preview(PurchaseApplyMain form) {
        if (form == null) {
            form = new PurchaseApplyMain();
        }
        UserView user = TokenUtil.getUserFromAuthToken();
        form.setSubmitUserid(user.getId());
        form.setSubmitUsername(user.getUsername());
        final List<UserTaskDTO> preview = purchaseService.preview(form);
        return R.success(preview);
    }

    /**
     * 暂存草稿
     */
    @PostMapping("/temp")
    public R<Object> tempSave(@RequestBody PurchaseApplyMain form) {
        UserView user = TokenUtil.getUserFromAuthToken();
        form.setSubmitUserid(user.getId());
        form.setSubmitUsername(user.getUsername());
        purchaseService.tempSave(form);
        return R.success("暂存草稿成功");
    }

    @GetMapping("/todo")
    public R<List<PurchaseApplyMain>> todoList(@ModelAttribute PurchaseApplyRequestForm requestForm) {
        setTokenUser(requestForm);
        final Page<PurchaseApplyMain> page = purchaseService.findMyApplyByQueryPageable(requestForm);
        return R.success(page.getContent(), (int) page.getTotalElements());
    }

    @GetMapping("/done")
    public R<List<PurchaseApplyMain>> doneList(@ModelAttribute PurchaseApplyRequestForm requestForm) {
        setTokenUser(requestForm);
        final Page<PurchaseApplyMain> page = purchaseService.findMyApplyByQueryPageable(requestForm);
        return R.success(page.getContent(), (int) page.getTotalElements());
    }

    @GetMapping("/myapply")
    public R<List<PurchaseApplyMain>> myApplyList(@ModelAttribute PurchaseApplyRequestForm requestForm) {
        setTokenUser(requestForm);
        final Page<PurchaseApplyMain> page = purchaseService.findMyApplyByQueryPageable(requestForm);
        return R.success(page.getContent(), (int) page.getTotalElements());
    }


    public void setTokenUser(PurchaseApplyRequestForm form) {
        UserView user = TokenUtil.getUserFromAuthToken();
        form.setUserid(user.getId());
        form.setUsername(user.getName());
    }
}
