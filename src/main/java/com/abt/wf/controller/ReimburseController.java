package com.abt.wf.controller;

import com.abt.common.config.ValidateGroup;
import com.abt.common.model.R;
import com.abt.common.util.TokenUtil;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.dto.UserView;
import com.abt.wf.config.Constants;
import com.abt.wf.entity.FlowOperationLog;
import com.abt.wf.entity.Reimburse;
import com.abt.wf.model.ReimburseRequestForm;
import com.abt.wf.model.UserTaskDTO;
import com.abt.wf.service.ReimburseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 *
 */
@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/wf/rbs")
public class ReimburseController {

    private final ReimburseService reimburseService;


    /**
     * 撤销一个流程
     * @param id: 撤销的流程的id
     */
    @GetMapping("/revoke")
    public R<Object> revoke(String id) {
        UserView user = TokenUtil.getUserFromAuthToken();
        reimburseService.revoke(id, user.getId(), user.getName());
        return R.success("撤销成功");
    }

    @GetMapping("/restart")
    public R<Reimburse> copyEntity(String id) throws Exception {
        final Reimburse copyEntity = reimburseService.getCopyEntity(id);
        return R.success(copyEntity);
    }

    @PostMapping("/apply")
    public R<Object> apply(@Validated({ValidateGroup.Apply.class}) @RequestBody Reimburse form) {
        form.setRbsDate(LocalDate.now());
        reimburseService.apply(form);
        return R.success();
    }

    @PostMapping("/approve")
    public R<Object> approve(@RequestBody Reimburse form) {
        UserView user = TokenUtil.getUserFromAuthToken();
        form.setSubmitUserid(user.getId());
        form.setSubmitUsername(user.getUsername());
        reimburseService.approve(form);
        return R.success("审批成功");
    }

    @GetMapping("/todo")
    public R<List<Reimburse>> todoList(@ModelAttribute ReimburseRequestForm requestForm) {
        setTokenUser(requestForm);
        final Page<Reimburse> page = reimburseService.findMyTodoByQueryPageable(requestForm);
        return R.success(page.getContent(), (int)page.getTotalElements());
    }

    @GetMapping("/done")
    public R<List<Reimburse>> doneList(ReimburseRequestForm requestForm) {
        setTokenUser(requestForm);
        final Page<Reimburse> page = reimburseService.findMyDoneByQueryPageable(requestForm);
        return R.success(page.getContent(), (int)page.getTotalElements());

    }

    @GetMapping("/myapply")
    public R<List<Reimburse>> myApplyList(ReimburseRequestForm requestForm) {
        setTokenUser(requestForm);
        final Page<Reimburse> page = reimburseService.findMyApplyByQueryPageable(requestForm);
        return R.success(page.getContent(), (int)page.getTotalElements());
    }

    @GetMapping("/all")
    public R<List<Reimburse>> all(@ModelAttribute ReimburseRequestForm requestForm) {
        final Page<Reimburse> all = reimburseService.findAllByQueryPageable(requestForm);
        return R.success(all.getContent(), (int)all.getTotalElements());
    }

    @GetMapping("/load/{id}")
    public R<Reimburse> load(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException("审批编号不能为空!");
        }
        Reimburse invoiceApply = reimburseService.load(id);
        return R.success(invoiceApply);
    }


    //删除权限：财务流程-删除
    @Secured("849b61e0-6f97-454c-b729-f3ebc821953e")
    @GetMapping("/del/{id}")
    public R<Object> delete(@PathVariable String id, String reason) {
        reimburseService.delete(id, reason);
        return R.success("删除成功");
    }


    @PostMapping("/preview")
    public R<List<UserTaskDTO>> preview(@RequestBody Reimburse form) {
        UserView user = TokenUtil.getUserFromAuthToken();
        form.setSubmitUserid(user.getId());
        form.setSubmitUsername(user.getUsername());
        final List<UserTaskDTO> preview = reimburseService.preview(form);
        return R.success(preview, preview.size());
    }

    @GetMapping("/record/{id}")
    public R<List<FlowOperationLog>> processRecord(@PathVariable String id) {
        final List<FlowOperationLog> processRecord = reimburseService.processRecord(id, Constants.SERVICE_PAY);
        return R.success(processRecord, processRecord.size());
    }

    public void setTokenUser(ReimburseRequestForm form) {
        UserView user = TokenUtil.getUserFromAuthToken();
        form.setUserid(user.getId());
        form.setUsername(user.getName());
    }
}
