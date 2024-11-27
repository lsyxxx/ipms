package com.abt.wf.controller;

import com.abt.common.model.R;
import com.abt.common.util.TokenUtil;
import com.abt.sys.model.dto.UserView;
import com.abt.wf.entity.FlowOperationLog;
import com.abt.wf.entity.PurchaseApplyDetail;
import com.abt.wf.entity.PurchaseApplyMain;
import com.abt.wf.model.PurchaseApplyRequestForm;
import com.abt.wf.model.UserTaskDTO;
import com.abt.wf.service.PurchaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.abt.wf.config.Constants.SERVICE_PURCHASE;

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
        setTokenUser(form);
        final PurchaseApplyMain entity = purchaseService.apply(form);
        purchaseService.skipEmptyUserTask(entity);
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
        final Page<PurchaseApplyMain> page = purchaseService.findMyTodoByQueryPageable(requestForm);
        return R.success(page.getContent(), (int) page.getTotalElements());
    }

    @GetMapping("/done")
    public R<List<PurchaseApplyMain>> doneList(@ModelAttribute PurchaseApplyRequestForm requestForm) {
        setTokenUser(requestForm);
        final Page<PurchaseApplyMain> page = purchaseService.findMyDoneByQueryPageable(requestForm);
        return R.success(page.getContent(), (int) page.getTotalElements());
    }

    @GetMapping("/myapply")
    public R<List<PurchaseApplyMain>> myApplyList(@ModelAttribute PurchaseApplyRequestForm requestForm) {
        setTokenUser(requestForm);
        final Page<PurchaseApplyMain> page = purchaseService.findMyApplyByQueryPageable(requestForm);
        return R.success(page.getContent(), (int) page.getTotalElements());
    }

    @GetMapping("/all")
    public R<List<PurchaseApplyMain>> allList(@ModelAttribute PurchaseApplyRequestForm requestForm) {
        setTokenUser(requestForm);
        final Page<PurchaseApplyMain> page = purchaseService.findAllByQueryPageable(requestForm);
        return R.success(page.getContent(), (int) page.getTotalElements());
    }


    @GetMapping("/load")
    public R<PurchaseApplyMain> load(String id) {
        final PurchaseApplyMain entity = purchaseService.load(id);
        return R.success(entity);
    }

    @PostMapping("/approve")
    public R<Object> approve(@RequestBody PurchaseApplyMain form) {
        setTokenUser(form);
        purchaseService.saveEntity(form);
        purchaseService.approve(form);
        return R.success("审批成功");
    }

    @GetMapping("/record/{id}")
    public R<List<FlowOperationLog>> processRecord(@PathVariable String id) {
        if (StringUtils.isEmpty(id)) {
            return R.warn("未传入审批编号", List.of());
        }
        final List<FlowOperationLog> records = purchaseService.processRecord(id, SERVICE_PURCHASE);
        return R.success(records);
    }

    @GetMapping("/restart")
    public R<PurchaseApplyMain> restart(String id) {
        final PurchaseApplyMain copyEntity = purchaseService.getCopyEntity(id);
        return R.success(copyEntity);
    }

    @GetMapping("/revoke")
    public R<Object> revoke(String id) {
        UserView user = TokenUtil.getUserFromAuthToken();
        purchaseService.revoke(id, user.getId(), user.getName());
        return R.success("已撤销");
    }

    /**
     * 验收
     */
    @PostMapping("/accept/all")
    public R<Object> accept(@RequestBody PurchaseApplyMain form) {
        log.info("验收");
        form.getDetails().forEach(PurchaseApplyDetail::qualified);
        purchaseService.saveEntity(form);
        return R.success("已撤销");
    }


    public void setTokenUser(PurchaseApplyRequestForm form) {
        UserView user = TokenUtil.getUserFromAuthToken();
        form.setUserid(user.getId());
        form.setUsername(user.getName());
    }

    public void setTokenUser(PurchaseApplyMain form) {
        UserView user = TokenUtil.getUserFromAuthToken();
        form.setSubmitUserid(user.getId());
        form.setSubmitUsername(user.getUsername());
    }
}
