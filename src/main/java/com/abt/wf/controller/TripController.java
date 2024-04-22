package com.abt.wf.controller;

import com.abt.common.model.Page;
import com.abt.common.model.R;
import com.abt.common.util.TokenUtil;
import com.abt.sys.model.dto.UserView;
import com.abt.wf.config.Constants;
import com.abt.wf.entity.FlowOperationLog;
import com.abt.wf.model.TripReimburseForm;
import com.abt.wf.model.TripRequestForm;
import com.abt.wf.model.UserTaskDTO;
import com.abt.wf.service.TripReimburseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 差旅报销
 */
@RestController
@Slf4j
@RequestMapping("/wf/trip")
public class TripController {

    private final TripReimburseService tripReimburseService;

    public TripController(TripReimburseService tripReimburseService) {
        this.tripReimburseService = tripReimburseService;
    }


    /**
     * 申请
     * @param form 申请表单
     */
    @PostMapping("/apply")
    public R<Object> apply(@RequestBody TripReimburseForm form) {
        tripReimburseService.validateApplyForm(form);
        UserView user = TokenUtil.getUserFromAuthToken();
        form.getCommon().setCreateUserid(user.getId());
        form.getCommon().setCreateUsername(user.getUsername());
        form.setSubmitUserid(user.getId());
        form.setSubmitUsername(user.getUsername());

        tripReimburseService.apply(form);
        return R.success("申请成功");
    }

    @PostMapping("/approve")
    public R<Object> approve(@RequestBody TripReimburseForm form) {
        UserView user = TokenUtil.getUserFromAuthToken();
        form.setSubmitUserid(user.getId());
        form.setSubmitUsername(user.getUsername());
        tripReimburseService.approve(form);
        return R.success("审批成功");
    }

    @GetMapping("/todo")
    public R<List<TripReimburseForm>> todoList(@ModelAttribute TripRequestForm form) {
        UserView user = TokenUtil.getUserFromAuthToken();
        form.setUserid(user.getId());
        form.setUsername(user.getUsername());
        final Page<TripReimburseForm> todo = tripReimburseService.findMyTodoByCriteriaPaged(form);
        return R.success(todo.getContent(), todo.getTotal());
    }

    @GetMapping("/done")
    public R<List<TripReimburseForm>> doneList(@ModelAttribute TripRequestForm form) {
        UserView user = TokenUtil.getUserFromAuthToken();
        form.setUserid(user.getId());
        form.setUsername(user.getUsername());
        final Page<TripReimburseForm> done = tripReimburseService.findMyDoneByCriteriaPaged(form);
        return R.success(done.getContent(), done.getTotal());
    }

    @GetMapping("/myapply")
    public R<List<TripReimburseForm>> myApplyList(@ModelAttribute TripRequestForm form) {
        UserView user = TokenUtil.getUserFromAuthToken();
        form.setUserid(user.getId());
        form.setUsername(user.getUsername());
        final Page<TripReimburseForm> page = tripReimburseService.findMyApplyByCriteriaPaged(form);
        return R.success(page.getContent(), page.getTotal());
    }

    /**
     * 所有差旅报销记录
     * @param form 搜索条件
     */
    @GetMapping("/all")
    public R<List<TripReimburseForm>> getAll(@ModelAttribute TripRequestForm form) {
        final Page<TripReimburseForm> all = tripReimburseService.findAllPaged(form);
        return R.success(all.getContent(), all.getTotal());
    }

    @GetMapping("/load/{rootId}")
    public R<TripReimburseForm> load(@PathVariable String rootId) {
        final TripReimburseForm form = tripReimburseService.load(rootId);
        return R.success(form);
    }

    @GetMapping("/del/{rootId}")
    public R<Object> delete(@PathVariable String rootId) {
        tripReimburseService.delete(rootId);
        return R.success("删除成功");
    }

    @PostMapping("/preview")
    public R<List<UserTaskDTO>> preview(@RequestBody TripReimburseForm form) {
        final List<UserTaskDTO> preview = tripReimburseService.preview(form);
        return R.success(preview);
    }


    @GetMapping("/record/{entityId}")
    public R<List<FlowOperationLog>> processRecord(@PathVariable String entityId) {
        final List<FlowOperationLog> flowOperationLogs = tripReimburseService.processRecord(entityId, Constants.SERVICE_TRIP);
        return R.success(flowOperationLogs);
    }



}
