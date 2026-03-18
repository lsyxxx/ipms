package com.abt.wf.controller;

import com.abt.common.config.ValidateGroup;
import com.abt.common.model.R;
import com.abt.common.util.TokenUtil;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.dto.UserView;
import com.abt.wf.config.Constants;
import com.abt.wf.entity.FlowOperationLog;
import com.abt.wf.entity.SubcontractTestingSettlementDetail;
import com.abt.wf.entity.SubcontractTestingSettlementMain;
import com.abt.wf.model.SbctSummaryData;
import com.abt.wf.model.SubcontractTestingSettlementRequestForm;
import com.abt.wf.model.UserTaskDTO;
import com.abt.wf.service.SubcontractTestingSettlementService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 外送结算流程
 */
@RestController
@Slf4j
@RequestMapping("/wf/sbctstl")
public class SubcontractTestingSettlementController {


    private final SubcontractTestingSettlementService subcontractTestingSettlementService;

    public SubcontractTestingSettlementController(SubcontractTestingSettlementService subcontractTestingSettlementService) {
        this.subcontractTestingSettlementService = subcontractTestingSettlementService;
    }

    /**
     * 撤销一个流程
     * @param id: 撤销的流程的id
     */
    @GetMapping("/revoke")
    public R<Object> revoke(String id) {
        UserView user = TokenUtil.getUserFromAuthToken();
        subcontractTestingSettlementService.revoke(id, user.getId(), user.getName());
        return R.success("撤销成功");
    }

    @GetMapping("/restart")
    public R<SubcontractTestingSettlementMain> copyEntity(String id) {
        final SubcontractTestingSettlementMain copyEntity = subcontractTestingSettlementService.getCopyEntity(id);
        return R.success(copyEntity);
    }

    @PostMapping("/apply")
    public R<Object> apply(@Validated({ValidateGroup.Apply.class}) @RequestBody SubcontractTestingSettlementMain form) {
        setSubmitUser(form);
        final SubcontractTestingSettlementMain entity = subcontractTestingSettlementService.apply(form);
        subcontractTestingSettlementService.skipEmptyUserTask(entity);
        return R.success(entity, "提交成功");
    }

    @PostMapping("/approve")
    public R<Object> approve(@RequestBody SubcontractTestingSettlementMain form) {
        UserView user = TokenUtil.getUserFromAuthToken();
        form.setSubmitUserid(user.getId());
        form.setSubmitUsername(user.getUsername());
        subcontractTestingSettlementService.approve(form);
        subcontractTestingSettlementService.skipEmptyUserTask(form);
        return R.success("审批成功");
    }


    @GetMapping("/todo")
    public R<List<SubcontractTestingSettlementMain>> todoList(@ModelAttribute SubcontractTestingSettlementRequestForm requestForm) {
        setTokenUser(requestForm);
        List<SubcontractTestingSettlementMain> todoList = subcontractTestingSettlementService.findMyTodoList(requestForm);
        return R.success(todoList, todoList.size());
    }
    
    @GetMapping("/done")
    public R<List<SubcontractTestingSettlementMain>> doneList(@ModelAttribute SubcontractTestingSettlementRequestForm requestForm) {
        setTokenUser(requestForm);
        Page<SubcontractTestingSettlementMain> page = subcontractTestingSettlementService.findMyDoneByQueryPageable(requestForm);
        return R.success(page.getContent(), (int) page.getTotalElements());
    }

    @GetMapping("/myapply")
    public R<List<SubcontractTestingSettlementMain>> myApplyList(@ModelAttribute SubcontractTestingSettlementRequestForm requestForm) {
        setTokenUser(requestForm);
        Page<SubcontractTestingSettlementMain> page = subcontractTestingSettlementService.findMyApplyByQueryPageable(requestForm);
        return R.success(page.getContent(), (int) page.getTotalElements());
    }

    @GetMapping("/all")
    public R<List<SubcontractTestingSettlementMain>> all(@ModelAttribute SubcontractTestingSettlementRequestForm requestForm) {
        Page<SubcontractTestingSettlementMain> all = subcontractTestingSettlementService.findAllByQueryPageable(requestForm);
        return R.success(all.getContent(), (int) all.getTotalElements());
    }
    
    @GetMapping("/load/{id}")
    public R<SubcontractTestingSettlementMain> load(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException("审批编号不能为空!");
        }
        SubcontractTestingSettlementMain form = subcontractTestingSettlementService.loadEntityOnly(id);
        final List<SbctSummaryData> summaryData = subcontractTestingSettlementService.getSummaryData(id);
        final boolean exists = subcontractTestingSettlementService.duplicatedSamplesExists(id);
        form.setSummaryData(summaryData);
        form.setDuplicatedSettledExists(exists);
        setSubmitUser(form);
        return R.success(form);
    }

    //删除权限：财务流程-删除
    @GetMapping("/del/{id}")
    public R<Object> delete(@PathVariable String id, @RequestParam(required = false) String reason) {
        subcontractTestingSettlementService.delete(id, reason);
        return R.success("删除成功");
    }

    @PostMapping("/preview")
    public R<List<UserTaskDTO>> preview(@RequestBody SubcontractTestingSettlementMain form) {
        UserView user = TokenUtil.getUserFromAuthToken();
        form.setSubmitUserid(user.getId());
        form.setSubmitUsername(user.getUsername());
        final List<UserTaskDTO> preview = subcontractTestingSettlementService.preview(form);
        return R.success(preview, preview.size());
    }

    @GetMapping("/record/{id}")
    public R<List<FlowOperationLog>> processRecord(@PathVariable String id) {
        final List<FlowOperationLog> processRecord = subcontractTestingSettlementService.processRecord(id, Constants.SERVICE_SBCT_STL);
        return R.success(processRecord, processRecord.size());
    }

    @GetMapping("/todo/count")
    public R<Integer> countTodo(@ModelAttribute SubcontractTestingSettlementRequestForm requestForm) {
        setTokenUser(requestForm);
        final int count = subcontractTestingSettlementService.countMyTodo(requestForm);
        return R.success(count, "查询成功");
    }

    /**
     * 获取结算单的样品列表
     */
    @GetMapping("/sample/page")
    public R<Page<SubcontractTestingSettlementDetail>> getSampleList(@ModelAttribute SubcontractTestingSettlementRequestForm requestForm) {
        final PageRequest pageRequest = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Direction.ASC, "sampleNo", "checkModuleId"));
        final Page<SubcontractTestingSettlementDetail> page = subcontractTestingSettlementService.getSamplesPage(requestForm.getId(), pageRequest);
        return R.success(page, "查询成功");
    }

    /**
     * 查询重复结算的样品
     */
    @PostMapping("/find/duplicaed")
    public void findDuplicatedSamples(@RequestBody SubcontractTestingSettlementRequestForm requestForm) {

    }
    
    /**
     * 设置当前用户信息
     */
    private void setTokenUser(SubcontractTestingSettlementRequestForm requestForm) {
        UserView user = TokenUtil.getUserFromAuthToken();
        requestForm.setUserid(user.getId());
        requestForm.setUsername(user.getUsername());
    }
    
    /**
     * 设置提交用户信息
     */
    private void setSubmitUser(SubcontractTestingSettlementMain form) {
        UserView user = TokenUtil.getUserFromAuthToken();
        form.setSubmitUserid(user.getId());
        form.setSubmitUsername(user.getUsername());
    }
}
