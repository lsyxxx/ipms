package com.abt.wf.controller;

import com.abt.common.model.Page;
import com.abt.common.model.R;
import com.abt.common.model.User;
import com.abt.common.util.JsonUtil;
import com.abt.common.util.MessageUtil;
import com.abt.common.util.TokenUtil;
import com.abt.sys.model.dto.UserView;
import com.abt.sys.model.entity.SystemFile;
import com.abt.sys.service.IFileService;
import com.abt.wf.entity.WorkflowBase;
import com.abt.wf.model.ActivitiRequestForm;
import com.abt.wf.model.UserTodo;
import com.abt.wf.service.ActivitiService;
import com.abt.wf.service.FlowOperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static com.abt.wf.config.WorkFlowConfig.DEF_KEY_RBS;

/**
 * 通用
 */
@RestController
@Slf4j
@RequestMapping("/wf")
public class WorkflowController {
    private final ActivitiService activitiService;;

    private final IFileService fileService;

    private final FlowOperationLogService flowOperationLogService;

    @Value("${com.abt.file.upload.save}")
    private String savedRoot;


    public WorkflowController(ActivitiService activitiService, IFileService fileService, FlowOperationLogService flowOperationLogService) {
        this.activitiService = activitiService;
        this.fileService = fileService;
        this.flowOperationLogService = flowOperationLogService;
    }

    /**
     * 默认抄送人
     */
    @GetMapping("/defaultcc")
    public R<List<User>> getDefaultCopyUser() {
        final List<User> defaultCopyUsers = activitiService.findDefaultCopyUsers();
        return R.success(defaultCopyUsers, defaultCopyUsers.size());
    }

    @GetMapping("/find/todo/all")
    public R<List<WorkflowBase>> findUserTodoAll(@RequestParam(required = false) String query, @RequestParam(required = false) String type) {
        final List<WorkflowBase> all = activitiService.findUserTodoAll(TokenUtil.getUseridFromAuthToken(), query, 0, 0);
        return R.success(all, all.size());
    }

    @GetMapping("/fin/todo/count")
    public R<Integer> findUserTodoCount() {
        final List<WorkflowBase> all = activitiService.findUserTodoAll(TokenUtil.getUseridFromAuthToken(), null, 0, 0);
        return R.success(all.size(), all.size());
    }

    /**
     * 用户待办页面
     *
     * @param activeKey: 激活的流程id, 若无默认显示DEF_KEY_RBS
     */
    @GetMapping("/count/all")
    public R<UserTodo> userTodoPage(@RequestParam(required = false, defaultValue = DEF_KEY_RBS) String activeKey) {
        final UserTodo userTodo = activitiService.countTodoAll(activeKey, TokenUtil.getUseridFromAuthToken());
        return R.success(userTodo, "查询待办成功!");
    }

    @GetMapping("/find/todo/defkey")
    public R<List<Object>> findTodoByDefKeyAndTaskName(@RequestParam String defKey,
                                                       @RequestParam(required = false, defaultValue = "") String taskName,
                                                       @RequestParam(required = false, defaultValue = "") String query) {
        final List<Object> list = activitiService.findTodoByDefKey(defKey, taskName, query, TokenUtil.getUseridFromAuthToken());
        return R.success(list, list.size());
    }

    /**
     * 删除流程
     * @param procId 流程实例id
     * @param deleteReason 删除原因
     */
    @GetMapping("/processinstance/del")
    public R<Object> deleteProcessInstance(String procId, String deleteReason) {
        activitiService.deleteProcessInstance(procId, deleteReason);
        return R.success("删除流程(" + procId + ")成功");
    }

    /**
     * 所有正在运行的流程节点
     */
    @GetMapping("/task/run/all")
    public R<List<Task>> runningTasks(@ModelAttribute ActivitiRequestForm requestForm) {
        final Page<Task> taskPage = activitiService.runningTasks(requestForm);
        return R.success(taskPage.getContent(), taskPage.getTotal());
    }


    /**
     * 已完成的流程
     */
    @GetMapping("/proc/hi")
    public R<List<HistoricProcessInstance>> findProcess(@ModelAttribute ActivitiRequestForm requestForm) {
        final Page<HistoricProcessInstance> list = activitiService.finishedProcess(requestForm);
        return R.success(list.getContent(), list.getTotal());
    }


    @GetMapping("/proc/rt")
    public R<List<ProcessInstance>> runtimeProcess(@ModelAttribute ActivitiRequestForm requestForm) {
        final Page<ProcessInstance> processInstancePage = activitiService.runtimeProcess(requestForm);
        return R.success(processInstancePage.getContent(), processInstancePage.getTotal());
    }


    /**
     * 流程审批节点补充附件
     */
    @PostMapping("/optLogAtt/upload")
    public R<List<SystemFile>> optLogAttachmentUpload(@RequestParam("file") MultipartFile[] files, @RequestParam String optLogId,
                                                      @RequestParam String service) {
        UserView user = TokenUtil.getUserFromAuthToken();
        if (files == null || files.length < 1) {
            log.warn("用户没有上传文件");
            return R.noFileUpload();
        }

        String failed = null;
        String msg = null;
        List<SystemFile> saved = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }
            try {
                SystemFile systemFile = fileService.saveFile(file, savedRoot, service, true, true);
                saved.add(systemFile);
            } catch (Exception e) {
                log.error("保存文件失败", e);
                if (failed != null) {
                    failed = " ," + failed + file.getOriginalFilename();
                } else {
                    failed = file.getName();
                }
                msg = MessageUtil.format("com.abt.sys.FileController.save.error", failed);
            }
        }

        flowOperationLogService.findById(optLogId)
                .ifPresent(opt -> {
                    opt.setFileJson(JsonUtil.convertJson(saved));
                    flowOperationLogService.saveLog(opt);
                });

        return R.success(saved, saved.size(), msg);
    }

}
