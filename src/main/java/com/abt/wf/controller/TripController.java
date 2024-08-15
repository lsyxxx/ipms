package com.abt.wf.controller;

import com.abt.common.config.ValidateGroup;
import com.abt.common.model.R;
import com.abt.common.util.TokenUtil;
import com.abt.sys.model.dto.UserView;
import com.abt.wf.config.Constants;
import com.abt.wf.entity.FlowOperationLog;
import com.abt.wf.entity.Reimburse;
import com.abt.wf.entity.TripMain;
import com.abt.wf.model.TripRequestForm;
import com.abt.wf.model.UserTaskDTO;
import com.abt.wf.service.TripService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *
 */
@RestController
@Slf4j
@RequestMapping("/wf/trip")
public class TripController {

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }


    /**
     * 撤销一个流程
     * @param id: 撤销的流程的id
     */
    @GetMapping("/revoke")
    public R<Object> revoke(String id) {
        UserView user = TokenUtil.getUserFromAuthToken();
        tripService.revoke(id, user.getId(), user.getName());
        return R.success("撤销成功");
    }

    @GetMapping("/restart")
    public R<TripMain> copyEntity(String id) {
        final TripMain copyEntity = tripService.getCopyEntity(id);
        if (copyEntity.getDetails() != null) {
            copyEntity.getDetails().forEach(d -> {
                d.setId(null);
                d.setMid(null);
                if (d.getItems() != null) {
                    d.getItems().forEach(i -> {
                        i.setId(null);
                        i.setDid(null);
                    });
                }
            });
        }
        return R.success(copyEntity);
    }


    @PostMapping("/apply")
    public R<Object> apply(@Validated({ValidateGroup.Apply.class}) @RequestBody TripMain form) {
        setSubmitUser(form);
        tripService.apply(form);
        return R.success("申请成功");
    }

    @PostMapping("/approve")
    public R<Object> approve(@RequestBody TripMain form) {
        setSubmitUser(form);
        tripService.approve(form);
        return R.success("审批成功");
    }

    @GetMapping("/todo")
    public R<List<TripMain>> todoList(@ModelAttribute TripRequestForm requestForm) {
        setTokenUser(requestForm);
        final Page<TripMain> page = tripService.findMyTodoByQueryPageable(requestForm);
        return R.success(page.getContent(), (int)page.getTotalElements());
    }

    @GetMapping("/done")
    public R<List<TripMain>> doneList(TripRequestForm requestForm) {
        setTokenUser(requestForm);
        final Page<TripMain> page = tripService.findMyDoneByQueryPageable(requestForm);
        return R.success(page.getContent(), (int)page.getTotalElements());
    }

    @GetMapping("/myapply")
    public R<List<TripMain>> myApplyList(TripRequestForm requestForm) {
        setTokenUser(requestForm);
        final Page<TripMain> page = tripService.findMyApplyByQueryPageable(requestForm);
        return R.success(page.getContent(), (int)page.getTotalElements());
    }

    @GetMapping("/all")
    public R<List<TripMain>> all(@ModelAttribute TripRequestForm requestForm) {
        final Page<TripMain> page = tripService.findAllByQueryPageable(requestForm);
        return R.success(page.getContent(), (int)page.getTotalElements());
    }

    @GetMapping("/load/{id}")
    public R<TripMain> load(@PathVariable String id) {
        TripMain TripMain = tripService.getEntityWithCurrentTask(id);
        return R.success(TripMain);
    }

    @GetMapping("/del/{id}")
    public R<Object> delete(@PathVariable String id, @RequestParam(required = false) String reason) {
        tripService.delete(id, reason);
        return R.success("删除成功");
    }


    @PostMapping("/preview")
    public R<List<UserTaskDTO>> preview(@RequestBody TripMain form) {
        setSubmitUser(form);
        final List<UserTaskDTO> preview = tripService.preview(form);
        return R.success(preview, preview.size());
    }

    @GetMapping("/record/{id}")
    public R<List<FlowOperationLog>> processRecord(@PathVariable String id) {
        final List<FlowOperationLog> processRecord = tripService.processRecord(id, Constants.SERVICE_PAY);
        return R.success(processRecord, processRecord.size());
    }

    public void setTokenUser(TripRequestForm form) {
        UserView user = TokenUtil.getUserFromAuthToken();
        form.setUserid(user.getId());
        form.setUsername(user.getName());
    }

    public void setSubmitUser(TripMain main) {
        UserView user = TokenUtil.getUserFromAuthToken();
        main.setSubmitUserid(user.getId());
        main.setSubmitUsername(user.getUsername());
    }


}
