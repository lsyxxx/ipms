package com.abt.wf.service.impl;

import com.abt.common.model.User;
import com.abt.common.model.ValidationResult;
import com.abt.common.util.JsonUtil;
import com.abt.common.util.TimeUtil;
import com.abt.finance.entity.CreditBook;
import com.abt.finance.service.CreditBookService;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.entity.FlowSetting;
import com.abt.sys.model.entity.SystemFile;
import com.abt.sys.repository.FlowSettingRepository;
import com.abt.sys.service.IFileService;
import com.abt.sys.service.UserService;
import com.abt.wf.config.WorkFlowConfig;
import com.abt.wf.entity.PurchaseApplyMain;
import com.abt.wf.entity.Reimburse;
import com.abt.wf.model.ReimburseRequestForm;
import com.abt.wf.model.UserTaskDTO;
import com.abt.wf.repository.ReimburseRepository;
import com.abt.wf.service.FlowOperationLogService;
import com.abt.wf.service.ReimburseService;
import com.abt.wf.service.SignatureService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.abt.wf.config.Constants.*;

/**
 *
 */
@Service(WorkFlowConfig.DEF_KEY_RBS)
@Slf4j
public class ReimburseServiceImpl extends AbstractWorkflowCommonServiceImpl<Reimburse, ReimburseRequestForm> implements ReimburseService {

    private final IdentityService identityService;
    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final FlowOperationLogService flowOperationLogService;
    private final UserService userService;
    private final FlowSettingRepository flowSettingRepository;

    private final ReimburseRepository reimburseRepository;
    private final BpmnModelInstance rbsBpmnModelInstance;

    private final IFileService fileService;

    private final HistoryService historyService;
    private final SignatureService signatureService;

    private final CreditAndDebitBook<Reimburse> creditAndDebitBook;
    private final CreditBookService creditBookService;

    private List<User> copyList;

    @Value("${wf.rbs.url.pre}")
    private String urlPrefix;

    public ReimburseServiceImpl(IdentityService identityService, RepositoryService repositoryService, RuntimeService runtimeService, TaskService taskService,
                                FlowOperationLogService flowOperationLogService, @Qualifier("sqlServerUserService") UserService userService, FlowSettingRepository flowSettingRepository, ReimburseRepository reimburseRepository,
                                @Qualifier("rbsBpmnModelInstance") BpmnModelInstance rbsBpmnModelInstance, IFileService fileService, HistoryService historyService, SignatureService signatureService, CreditAndDebitBook<Reimburse> creditAndDebitBook, CreditBookService creditBookService) {
        super(identityService, flowOperationLogService, taskService, userService, repositoryService, runtimeService, fileService, historyService,signatureService);
        this.identityService = identityService;
        this.repositoryService = repositoryService;
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.flowOperationLogService = flowOperationLogService;
        this.userService = userService;
        this.flowSettingRepository = flowSettingRepository;
        this.reimburseRepository = reimburseRepository;
        this.rbsBpmnModelInstance = rbsBpmnModelInstance;
        this.fileService = fileService;
        this.historyService = historyService;
        this.signatureService = signatureService;
        this.creditAndDebitBook = creditAndDebitBook;
        this.creditBookService = creditBookService;
    }


    @Override
    ValidationResult beforePreview(Reimburse form) {
        return ValidationResult.pass();
    }

    @Override
    String getDecision(Reimburse form) {
        return form.getDecision();
    }

    @Override
    void passHandler(Reimburse form, Task task) {
        this.commonPassHandler(form, task, form.getComment(), form.getId());
    }

    @Override
    void rejectHandler(Reimburse form, Task task) {
        this.commonRejectHandler(form, task, form.getComment(), form.getId());
    }

    @Override
    void afterApprove(Reimburse form) {

    }

    @Override
    void setApprovalResult(Reimburse form, Reimburse entity) {
        entity.setDecision(form.getDecision());
        entity.setComment(form.getComment());
        if (StringUtils.isNotBlank(form.getPayLevel())) {
            entity.setPayLevel(form.getPayLevel());
        }
        entity.setCheckItemJson(form.getCheckItemJson());
        creditAndDebitBook.setCreditBookProperty(form, entity, entity.getCurrentTaskName());

        entity.setSubmitUserid(form.getSubmitUserid());
        entity.setSubmitUsername(form.getSubmitUsername());
    }

    @Override
    void setFileListJson(Reimburse entity, String json) {
        entity.setOtherFileList(json);
    }

    @Override
    void clearEntityId(Reimburse entity) {
        entity.setId(null);
    }


    public List<SystemFile> getFileAttachments(Reimburse form) {
        return JsonUtil.toObject(form.getOtherFileList(), new TypeReference<List<SystemFile>>() {});
    }

    public String getAttachmentJson(Reimburse form) {
        return form.getOtherFileList();
    }


//    @Override
    void copyFile(Reimburse entity) {
        try {
            String rawFile = entity.getOtherFileList();
            if (StringUtils.isBlank(rawFile)) {
                return;
            }
            entity.setOtherFileList(null);
            List<SystemFile> list = JsonUtil.toObject(rawFile, new TypeReference<List<SystemFile>>() {});
            List<SystemFile> newList = new ArrayList<>();
            list.forEach(i -> {
                File file = new File(i.getFullPath());
                final SystemFile newFile = fileService.copyFile(file, i.getOriginalName(), SAVE_SERVICE_RBS, true, true);
                newList.add(newFile);
            });
            entity.setOtherFileList(JsonUtil.toJson(newList));
        } catch (Exception e) {
            log.error("copy file error", e);
            entity.setOtherFileList(null);
        }
    }

    @Override
    public Page<Reimburse> findAllByQueryPageable(ReimburseRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Order.desc("createDate")));
        final Page<Reimburse> page = reimburseRepository.findAllByQueryPaged(requestForm.getQuery(), requestForm.getState(),
                TimeUtil.toLocalDateTime(requestForm.getStartDate()), TimeUtil.toLocalDateTime(requestForm.getEndDate()), pageable);
        page.getContent().forEach(this::buildActiveTask);
        return page;
    }

    @Override
    public Page<Reimburse> findMyApplyByQueryPageable(ReimburseRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Order.desc("createDate")));
        final Page<Reimburse> myApplyPaged = reimburseRepository.findMyApplyPaged(requestForm.getUserid(), requestForm.getQuery(), requestForm.getState(),
                TimeUtil.toLocalDateTime(requestForm.getStartDate()), TimeUtil.toLocalDateTime(requestForm.getEndDate()), pageable);
        myApplyPaged.getContent().forEach(this::buildActiveTask);
        return myApplyPaged;
    }

    @Override
    public Page<Reimburse> findMyTodoByQueryPageable(ReimburseRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Order.desc("createDate")));
        final Page<Reimburse> myTodoPaged = reimburseRepository.findMyTodoPaged(requestForm.getUserid(), requestForm.getQuery(), requestForm.getState(),
                TimeUtil.toLocalDateTime(requestForm.getStartDate()), TimeUtil.toLocalDateTime(requestForm.getEndDate()), pageable);
        myTodoPaged.getContent().forEach(this::buildActiveTask);
        return myTodoPaged;
    }

    @Override
    public Page<Reimburse> findMyDoneByQueryPageable(ReimburseRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Order.desc("createDate")));
        final Page<Reimburse> myDonePaged = reimburseRepository.findMyDonePaged(requestForm.getUserid(), requestForm.getQuery(), requestForm.getState(),
                TimeUtil.toLocalDateTime(requestForm.getStartDate()), TimeUtil.toLocalDateTime(requestForm.getEndDate()), pageable);
        myDonePaged.getContent().forEach(this::buildActiveTask);
        return myDonePaged;
    }

    @Override
    public Reimburse saveEntity(Reimburse entity) {
        return reimburseRepository.save(entity);
    }

    @Override
    public Reimburse load(String entityId) {
        Reimburse reimburse = reimburseRepository.findById(entityId).orElseThrow(() -> new BusinessException("未查询到费用报销单(审批编号=" + entityId + ")"));
        setActiveTask(reimburse);
        return reimburse;
    }

    @Override
    public String getEntityId(Reimburse entity) {
        return entity.getId();
    }

    @Override
    public String getServiceName() {
        return SERVICE_RBS;
    }

    @Override
    public Map<String, Object> createVariableMap(Reimburse form) {
        return form.createVariableMap();
    }

    @Override
    public String businessKey(Reimburse form) {
        return SERVICE_RBS;
    }

    @Override
    public List<UserTaskDTO> preview(Reimburse form) {
        final List<FlowSetting> list = flowSettingRepository.findByTypeOrderByKeyAsc(SETTING_TYPE_RBS_COPY);
        List<String> strList = list.stream().map(FlowSetting::getValue).toList();
        return this.commonPreview(form, createVariableMap(form), rbsBpmnModelInstance, strList);
    }

    @Override
    public void ensureEntityId(Reimburse form) {
        ensureProperty(form.getId(), "审批编号(id)");
    }

    @Override
    public boolean isApproveUser(Reimburse form) {
        return this.doIsApproveUser(form);
    }

    @Override
    public String notifyLink(String id) {
        return this.urlPrefix + id;
    }

    /**
     * 卡片简述
     */
    @Override
    public List<String> createBriefDesc(Reimburse entity) {
        //报销金额
        List<String> list = new ArrayList<>();
        if (entity == null) {
            return list;
        }
        list.add("报销金额:" + entity.getCost() + "(元)");
        list.add("报销类别:" + entity.getRbsType());
        list.add("报销事由:" + entity.getReason());
        return list;
    }

    @Override
    public List<CreditBook> loadCreditBook() {
        return List.of();
    }

    @Override
    public void writeCreditBook(Reimburse biz) {
        log.info("写入资金流出记录 -- 费用报销：entityId: {}", biz.getId());
        CreditBook creditBook = CreditBook.create(biz);
        creditBook.setServiceName(SERVICE_RBS);
        creditBookService.saveCreditBook(creditBook);
    }

    @Override
    public Reimburse loadBusiness(String businessId) {
        return reimburseRepository.findById(businessId).orElse(null);
    }


    @Override
    public Reimburse clearBizProcessData(Reimburse form) {
        form.clearData();
        return form;
    }

}

