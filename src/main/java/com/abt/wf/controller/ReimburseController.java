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


    @PostMapping("/apply")
    public R<Object> apply(@Validated({ValidateGroup.Apply.class}) @RequestBody Reimburse form) {
        form.setRbsDate(LocalDate.now());
        reimburseService.apply(form);
        return R.success();
    }

    @PostMapping("/approve")
    public R<Object> approve(@RequestBody Reimburse form) {
        reimburseService.approve(form);
        return R.success("审批成功");
    }

    @GetMapping("/todo")
    public R<List<Reimburse>> todoList(@ModelAttribute ReimburseRequestForm requestForm) {
        setTokenUser(requestForm);
        //criteria
        //申请人，申请时间，审批编号
        final List<Reimburse> todo = reimburseService.findMyTodoByCriteria(requestForm);
        final int total = reimburseService.countMyTodoByCriteria(requestForm);
        return R.success(todo, total);
    }

    @GetMapping("/done")
    public R<List<Reimburse>> doneList(ReimburseRequestForm requestForm) {
        setTokenUser(requestForm);
        final List<Reimburse> done = reimburseService.findMyDoneByCriteriaPageable(requestForm);
        final int total = reimburseService.countMyDoneByCriteria(requestForm);
        return R.success(done, total);
    }

    @GetMapping("/myapply")
    public R<List<Reimburse>> myApplyList(ReimburseRequestForm requestForm) {
        setTokenUser(requestForm);
        final Page<Reimburse> myApply = reimburseService.findMyApplyByCriteria(requestForm);
        return R.success(myApply.getContent(), (int) myApply.getTotalElements());
    }

    @GetMapping("/all")
    public R<List<Reimburse>> all(@ModelAttribute ReimburseRequestForm requestForm) {
        final Page<Reimburse> all = reimburseService.findAllByCriteria(requestForm);
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

    @GetMapping("/del/{id}")
    public R<Object> delete(@PathVariable String id) {
        reimburseService.delete(id);
        return R.success("删除成功");
    }


    @PostMapping("/preview")
    public R<List<UserTaskDTO>> preview(@RequestBody Reimburse form) {
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
