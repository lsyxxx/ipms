package com.abt.wf.service.impl;

import com.abt.common.exception.MissingRequiredParameterException;
import com.abt.common.model.User;
import com.abt.common.model.ValidationResult;
import com.abt.common.util.TimeUtil;
import com.abt.common.util.TokenUtil;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.dto.UserView;
import com.abt.sys.service.UserService;
import com.abt.wf.config.Constants;
import com.abt.wf.entity.FlowOperationLog;
import com.abt.wf.entity.TripReimburse;
import com.abt.wf.model.*;
import com.abt.wf.repository.TripReimburseTaskRepository;
import com.abt.wf.repository.TripRepository;
import com.abt.wf.service.FlowOperationLogService;
import com.abt.wf.service.TripReimburseService;
import com.abt.wf.util.WorkFlowUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.task.TaskQuery;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.abt.common.util.QueryUtil.like;
import static com.abt.wf.config.Constants.*;
import static com.abt.wf.config.WorkFlowConfig.DEF_KEY_TRIP;

/**
 *
 */

@Service
@Slf4j
public class TripReimburseServiceImpl extends AbstractWorkflowCommonServiceImpl<TripReimburseForm, TripRequestForm> implements TripReimburseService {
    private final FlowOperationLogService flowOperationLogService;

    private final RuntimeService runtimeService;

    private final RepositoryService repositoryService;

    private final TaskService taskService;

    private final TripRepository tripRepository;

    private final UserService userService;

//    private final Map<String, BpmnModelInstance> bpmnModelInstanceMap;
    private final BpmnModelInstance tripModelInstance;
    private final FormService formService;
    private final HistoryService historyService;
    private final TripReimburseTaskRepository tripReimburseTaskRepository;


    public TripReimburseServiceImpl(FlowOperationLogService flowOperationLogService, IdentityService identityService, RuntimeService runtimeService, RepositoryService repositoryService, TaskService taskService, TripRepository tripRepository,
                                    @Qualifier("sqlServerUserService") UserService userService,
                                    @Qualifier("rbsTripBpmnModelInstance") BpmnModelInstance tripModelInstance, FormService formService, HistoryService historyService, TripReimburseTaskRepository tripReimburseTaskRepository) {
        super(identityService, flowOperationLogService, taskService, userService, repositoryService, runtimeService);
        this.flowOperationLogService = flowOperationLogService;
        this.runtimeService = runtimeService;
        this.repositoryService = repositoryService;
        this.taskService = taskService;
        this.tripRepository = tripRepository;
        this.userService = userService;
        this.tripModelInstance = tripModelInstance;
        this.formService = formService;
        this.historyService = historyService;
        this.tripReimburseTaskRepository = tripReimburseTaskRepository;
    }

    @Override
    public Map<String, Object> createVariableMap(TripReimburseForm form) {
        return form.variableMap();
    }

    @Override
    public String businessKey(TripReimburseForm form) {
        return Constants.SERVICE_TRIP;
    }



    @Override
    public void apply(TripReimburseForm form) {
        TripReimburse common = form.getCommon();
        form.calcSum();
        WorkFlowUtil.ensureProcessDefinitionKey(common);
        //prepare
        Map<String, Object> variableMap = this.createVariableMap(form);

        //start instance
        final ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(common.getProcessDefinitionKey()).latestVersion().active().singleResult();
        final ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(common.getProcessDefinitionKey(), businessKey(form), variableMap);
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).active().singleResult();
        task.setAssignee(common.getCreateUserid());
        taskService.setAssignee(task.getId(), common.getCreateUserid());
        taskService.complete(task.getId());

        //save entity(s)
        common.setProcessDefinitionId(processDefinition.getId());
        common.setProcessInstanceId(processInstance.getId());
        common.setProcessState(HistoricProcessInstance.STATE_ACTIVE);
        common.setBusinessState(STATE_DETAIL_ACTIVE);
        common.setServiceName(SERVICE_TRIP);

        beforePersist(form);
        saveForm((form));
        runtimeService.setVariable(processInstance.getId(), Constants.VAR_KEY_ENTITY, form.getCommon().getId());

        //record
        FlowOperationLog optLog = FlowOperationLog.applyLog(form.getCommon().getCreateUserid(), form.getCommon().getCreateUsername(), form.getCommon(), task, form.getCommon().getId());
        optLog.setTaskDefinitionKey(task.getTaskDefinitionKey());
        optLog.setTaskResult(STATE_DETAIL_APPLY);
        flowOperationLogService.saveLog(optLog);
    }

    @Override
    public void approve(TripReimburseForm form) {
        log.info("--------- TripReimburse approve... ");
        //--validate
        WorkFlowUtil.ensureProcessId(form.getCommon());
        ensureProperty(form.getRootId(), "审批编号(rootId)");
        setAuthUser(form.getSubmitUserid());
        String procId = form.getCommon().getProcessInstanceId();
        Task task = taskService.createTaskQuery().processInstanceId(procId).active().processDefinitionKey(DEF_KEY_TRIP).singleResult();
        //验证用户是否是审批用户
        form.setCurrentTaskAssigneeId(task.getAssignee());
        isApproveUser(form);

        //--load entity
        TripReimburse common = loadCommonData(form.getRootId());
        //-- approve
        if (WorkFlowUtil.isPass(form.getDecision())) {
            //update status
            common.setBusinessState(STATE_DETAIL_ACTIVE);
            saveEntity(common);
            //pass log
            FlowOperationLog optLog = FlowOperationLog.passLog(form.getSubmitUserid(), form.getSubmitUsername(), form, task, form.getRootId());
            optLog.setTaskDefinitionKey(task.getTaskDefinitionKey());
            optLog.setComment(form.getComment());
            optLog.setTaskResult(WorkFlowUtil.decisionTranslate(form.getDecision()));
            flowOperationLogService.saveLog(optLog);
            taskService.complete(task.getId());
        } else if (WorkFlowUtil.isReject(form.getDecision())) {
//            taskService.complete(task.getId());
            //delete process
            runtimeService.deleteProcessInstance(task.getProcessInstanceId(), Constants.DELETE_REASON_REJECT + "_" + form.getSubmitUserid() + "_" + form.getSubmitUsername());
            //update status
            common.setBusinessState(STATE_DETAIL_REJECT);
            saveEntity(common);

            FlowOperationLog optLog = FlowOperationLog.rejectLog(form.getSubmitUserid(), form.getSubmitUsername(), form, task, form.getRootId());
            optLog.setTaskDefinitionKey(task.getTaskDefinitionKey());
            optLog.setComment(form.getComment());
            optLog.setTaskResult(WorkFlowUtil.decisionTranslate(this.getDecision(form)));
            flowOperationLogService.saveLog(optLog);
        } else {
            log.error("审批结果只能是pass/reject, 实际审批结果: {}", form.getDecision());
            throw new BusinessException("审批结果只能是pass/reject, 实际审批参数:" + form.getDecision());
        }

        clearAuthUser();
    }

    @Override
    public void validateApplyForm(TripReimburseForm form) {
        validateApplyFormCommonData(form);
        if (CollectionUtils.isEmpty(form.getItems())) {
            throw new BusinessException("请填写出差报销明细");
        }
        form.getItems().forEach(this::validateApplyFormItem);
    }

    public void validateApplyFormCommonData(TripReimburseForm form) {
        TripReimburse common = form.getCommon();
        if (common == null) {
            throw new BusinessException("请填写必要信息");
        }
        ensureProperty(common.getDeptId(), "申请部门(deptId)");
        ensureProperty(common.getStaff(), "出差人员(staff)");
        ensureProperty(common.getReason(), "出差事由(reason)");
        ensureProperty(common.getCompany(), "所属公司(company)");
        ensureProperty(common.getPayeeId(), "领款人(payeeId)");
    }

    public void validateApplyFormItem(TripReimburse item) {
        if (item == null) {
            return;
        }
        if (item.getTripStartDate() == null || item.getTripEndDate() == null) {
            throw new MissingRequiredParameterException("起止日期");
        }
        if (StringUtils.isBlank(item.getTripOrigin()) || StringUtils.isBlank(item.getTripArrival())) {
            throw new MissingRequiredParameterException("起讫地点");
        }
        ensureProperty(item.getTransportation(), "交通工具(transportation)");
        if (item.getTransExpense() == null) {
            throw new MissingRequiredParameterException("交通费(transExpense)");
        }
    }


    public void beforePersist(TripReimburseForm form) {
        //common: dept,staff,...
        TripReimburse common = form.getCommon();
        common.setServiceName(SERVICE_TRIP);
        //items
        for (TripReimburse trip : form.getItems()) {
            trip.copyProcessData(common);
            trip.sumItem();
            trip.setServiceName(SERVICE_TRIP);
        }
    }

    public void saveForm(TripReimburseForm form) {
        form.getCommon().setSort(0);
        final String code = TimeUtil.idGenerator();
        form.getCommon().setCode(code);
        form.setCommon(tripRepository.save(form.getCommon()));
        final String rootId = form.getCommon().getId();
        form.getItems().forEach(i -> {
            i.setRootId(rootId);
            i.setCode(code);
        });
        tripRepository.saveAllAndFlush(form.getItems());
    }

    public void revoke(String entityId) {

    }

    @Override
    public void delete(String rootId) {
        //-- 删除校验
        UserView user = TokenUtil.getUserFromAuthToken();
        final ValidationResult validationResult = deleteValidate(rootId, user.getId());
        //-- 删除
        if (validationResult.isPass()) {
            //-- update entity
            TripReimburse commonData = this.loadCommonData(rootId);
            commonData.setBusinessState(Constants.STATE_DETAIL_DELETE);
            commonData.setFinished(true);
            commonData.setDelete(true);
            tripRepository.save(commonData);

            //-- del process
            runtimeService.deleteProcessInstance(commonData.getProcessInstanceId(), Constants.DELETE_REASON_DELETE + "_" + user.getId() + "_" + user.getName());

            //-- log
            FlowOperationLog optLog = FlowOperationLog.deleteLog(user.getId(), user.getName(),
                    commonData.getProcessInstanceId(), commonData.getProcessDefinitionId(), commonData.getProcessDefinitionKey(),
                    SERVICE_TRIP, commonData.getId());
            flowOperationLogService.saveLog(optLog);

        } else {
            throw new BusinessException(validationResult.getDescription());
        }
    }


    @Override
    public List<UserTaskDTO> preview(TripReimburseForm form) {
        //-- 验证必要参数
        final ValidationResult validationResult = beforePreview(form);
        if (!validationResult.isPass()) {
            throw new BusinessException(validationResult.getDescription());
        }

        //preview
        return commonPreview(form, form.variableMap(), tripModelInstance, form.getCopyList());
    }

    public ValidationResult beforePreview(TripReimburseForm form) {
        WorkFlowUtil.ensureProcessDefinitionKey(form);
        return ValidationResult.pass();
    }


    /**
     * 校验rootId
     * @param form 表单
     */
    @Override
    public void ensureEntityId(TripReimburseForm form) {
        ensureProperty(form.getRootId(), "业务根节点id(rootId)");
    }

    @Override
    public String notifyLink(String id) {
        return "/wf/trip/detail/" + id;
    }

    @Override
    String getDecision(TripReimburseForm form) {
        return form.getDecision();
    }

    @Override
    public void passHandler(TripReimburseForm form, Task task) {
//        taskService.complete(task.getId());
//        //update status
//        common.setBusinessState(STATE_DETAIL_ACTIVE);
//        //pass log
//        FlowOperationLog optLog = FlowOperationLog.passLog(form.getSubmitUserid(), form.getSubmitUsername(), form, task, form.getRootId());
//        optLog.setTaskDefinitionKey(task.getTaskDefinitionKey());
//        optLog.setComment(form.getComment());
//        optLog.setTaskResult(WorkFlowUtil.decisionTranslate(form.getDecision()));
//        flowOperationLogService.saveLog(optLog);
    }

    @Override
    public void rejectHandler(TripReimburseForm form, Task task) {

    }

    @Override
    void afterApprove(TripReimburseForm form) {

    }

    @Override
    public TripReimburseForm load(String rootId) {
        final TripReimburse common = tripRepository.findById(rootId).orElseThrow(() -> new BusinessException("未查询到差旅报销业务(rootId=" + rootId + ")"));
        final List<TripReimburse> trips = tripRepository.findByRootIdOrderBySortAsc(rootId);
        TripReimburseForm form = new TripReimburseForm();
        final Task task = taskService.createTaskQuery().processInstanceId(common.getProcessInstanceId()).active().singleResult();
        if (task != null) {
            form.setCurrentTaskAssigneeId(task.getAssignee());
        }
        common.setRootId(common.getId());
        form.setCommon(common);
        form.setItems(trips);
        return form;
    }

    @Override
    public String getEntityId(TripReimburseForm entity) {
        return "";
    }

    @Override
    public String getServiceName() {
        return SERVICE_TRIP;
    }

    @Override
    public TripReimburse loadCommonData(String rootId) {
        return tripRepository.findById(rootId).orElseThrow(() -> new BusinessException("未查询到差旅报销业务数据(rootId=" + rootId + ")"));
    }

    @Override
    public void saveEntity(TripReimburse entity) {
        tripRepository.save(entity);
    }


    @Override
    public List<TripReimburseForm> findAllByCriteriaPageable(TripRequestForm requestForm) {
        requestForm.forcePaged();
        long t1 = System.currentTimeMillis();
        //criteria: 申请人(user),申请日期(startDate-endDate),出差人包含(staff), 审批编号(id), 状态(state), 分页
        //查询entity
        Specification<TripReimburse> spec = Specification.where(TripRbsSpecifications.idOrRootIdLike(requestForm))
                .and(TripRbsSpecifications.afterStartDate(requestForm))
                .and(TripRbsSpecifications.beforeEndDate(requestForm))
                .and(TripRbsSpecifications.createUsernameLike(requestForm))
                .and(TripRbsSpecifications.staffLike(requestForm))
                .and(TripRbsSpecifications.stateEqual(requestForm))
                .and(TripRbsSpecifications.isMainData());
        Pageable pageable = PageRequest.of(requestForm.getFirstResult(), requestForm.getLimit(),
                Sort.by(Sort.Order.desc("createDate")));
        final Page<TripReimburse> page = tripRepository.findAll(spec, pageable);
        List<TripReimburse> main = page.getContent();
        int total = (int) page.getTotalElements();
        //查询相关子数据
        List<String> rootIds = main.stream().map(TripReimburse::getId).toList();
        final List<TripReimburse> details = tripRepository.findAll(Specification.where(TripRbsSpecifications.idIn(rootIds))
                , Sort.by(Sort.Order.desc("createDate"), Sort.Order.asc("sort")));
        //build
        List<TripReimburseForm> records = build(main, details);
        long t2 = System.currentTimeMillis();
        log.debug("findAllByCriteriaPageable查询所有记录花费时间: {} ms", (t2-t1));
        return records;
    }

    @Override
    public int countAllByCriteria(TripRequestForm requestForm) {
        //criteria: 申请人(user),申请日期(startDate-endDate),出差人包含(staff), 审批编号(id), 状态(state), 分页
        //查询entity
        Specification<TripReimburse> spec = Specification.where(TripRbsSpecifications.idLike(requestForm))
                .and(TripRbsSpecifications.afterStartDate(requestForm))
                .and(TripRbsSpecifications.beforeEndDate(requestForm))
                .and(TripRbsSpecifications.createUsernameLike(requestForm))
                .and(TripRbsSpecifications.staffLike(requestForm))
                .and(TripRbsSpecifications.stateEqual(requestForm))
                ;
        final long count = tripRepository.count(spec);
        return (int) count;
    }

    public List<TripReimburseForm> findActiveTaskBySpecificationPageable(List<TripReimburseForm> records, String assignee) {
        //process
        records.forEach(i -> {
            final TaskQuery taskQuery = taskService.createTaskQuery().active().processInstanceId(i.getProcessInstanceId());
            if (assignee != null) {
                taskQuery.taskAssignee(assignee);
            }
            final Task activeTask = taskQuery.singleResult();
            if (activeTask != null && !activeTask.getTaskDefinitionKey().contains("apply")) {
                i.setCurrentTaskAssigneeId(activeTask.getAssignee());
                final User user = userService.getSimpleUserInfo(activeTask.getAssignee());
                i.setCurrentTaskAssigneeName(user.getUsername());
                i.setCurrentTaskId(activeTask.getId());
                i.setCurrentTaskDefId(activeTask.getTaskDefinitionKey());
                i.setCurrentTaskName(activeTask.getName());
                i.setCurrentTaskStartTime(TimeUtil.from(activeTask.getCreateTime()));
            }
        });
        return records;
    }

    @Override
    public com.abt.common.model.Page<TripReimburseForm> findMyApplyByCriteriaPaged(TripRequestForm requestForm) {
        requestForm.forcePaged();
        long t1 = System.currentTimeMillis();
        //查询主数据
        Specification<TripReimburse> spec = Specification.where(TripRbsSpecifications.createUseridEqual(requestForm))
                .and(TripRbsSpecifications.idLike(requestForm))
                .and(TripRbsSpecifications.afterStartDate(requestForm))
                .and(TripRbsSpecifications.beforeEndDate(requestForm))
                .and(TripRbsSpecifications.staffLike(requestForm))
                .and(TripRbsSpecifications.stateEqual(requestForm))
                .and(TripRbsSpecifications.isNotDelete(requestForm))
                .and(TripRbsSpecifications.isMainData())
                ;
        Pageable pageable = PageRequest.of(requestForm.getFirstResult(), requestForm.getLimit(),
                Sort.by(Sort.Order.desc("createDate")));
        final Page<TripReimburse> page = tripRepository.findAll(spec, pageable);
        List<TripReimburse> main = page.getContent();
        int total = (int) page.getTotalElements();
        //查询相关子数据
        List<String> rootIds = main.stream().map(TripReimburse::getId).toList();
        final List<TripReimburse> details = tripRepository.findAll(Specification.where(TripRbsSpecifications.idIn(rootIds))
                , Sort.by(Sort.Order.desc("createDate"), Sort.Order.asc("sort")));
        //build
        List<TripReimburseForm> records = build(main, details);
        records = findActiveTaskBySpecificationPageable(records, null);
        com.abt.common.model.Page<TripReimburseForm> returnPage = new com.abt.common.model.Page<>(records, total);
        long t2 = System.currentTimeMillis();
        log.debug("findMyApplyByCriteriaPageable2查询所有记录花费时间: {} ms", (t2-t1));
        return returnPage;
    }

    @Override
    public com.abt.common.model.Page<TripReimburseForm> findAllPaged(TripRequestForm requestForm) {
        requestForm.forcePaged();
        long t1 = System.currentTimeMillis();
        //查询主数据
        Specification<TripReimburse> spec = Specification
                .where(TripRbsSpecifications.idLike(requestForm))
                .and(TripRbsSpecifications.afterStartDate(requestForm))
                .and(TripRbsSpecifications.beforeEndDate(requestForm))
                .and(TripRbsSpecifications.staffLike(requestForm))
                .and(TripRbsSpecifications.stateEqual(requestForm))
                .and(TripRbsSpecifications.isNotDelete(requestForm))
                .and(TripRbsSpecifications.isMainData())
                ;
        Pageable pageable = PageRequest.of(requestForm.getFirstResult(), requestForm.getLimit(),
                Sort.by(Sort.Order.desc("createDate")));
        final Page<TripReimburse> page = tripRepository.findAll(spec, pageable);
        List<TripReimburse> main = page.getContent();
        int total = (int) page.getTotalElements();
        //查询相关子数据
        List<String> rootIds = main.stream().map(TripReimburse::getId).toList();
        final List<TripReimburse> details = tripRepository.findAll(Specification.where(TripRbsSpecifications.idIn(rootIds))
                , Sort.by(Sort.Order.desc("createDate"), Sort.Order.asc("sort")));
        //build
        List<TripReimburseForm> records = build(main, details);
        records = findActiveTaskBySpecificationPageable(records, null);
        com.abt.common.model.Page<TripReimburseForm> returnPage = new com.abt.common.model.Page<>(records, total);
        long t2 = System.currentTimeMillis();
        log.debug("findMyApplyByCriteriaPageable2查询所有记录花费时间: {} ms", (t2-t1));
        return returnPage;
    }

    //不能排序
    @Deprecated
    @Override
    public List<TripReimburseForm> findMyApplyByCriteriaPageable(TripRequestForm requestForm) {
        requestForm.forcePaged();
        //criteria: 申请人(user=),申请日期(startDate-endDate),出差人包含(staff), 审批编号(id), 状态(state), 分页
        long t1 = System.currentTimeMillis();
        //查询entity
        Specification<TripReimburse> spec = Specification.where(TripRbsSpecifications.createUseridEqual(requestForm))
                .and(TripRbsSpecifications.idOrRootIdLike(requestForm))
                .and(TripRbsSpecifications.afterStartDate(requestForm))
                .and(TripRbsSpecifications.beforeEndDate(requestForm))
                .and(TripRbsSpecifications.staffLike(requestForm))
                .and(TripRbsSpecifications.stateEqual(requestForm))
                .and(TripRbsSpecifications.isNotDelete(requestForm))
                ;
        Pageable pageable = PageRequest.of(requestForm.getFirstResult(), requestForm.getLimit(),
                Sort.by(Sort.Order.by("processInstanceId"), Sort.Order.desc("createDate"), Sort.Order.asc("sort")));
        final List<TripReimburse> all = tripRepository.findAll(spec, pageable).getContent();
        List<TripReimburseForm> records = build(all);
        records = findActiveTaskBySpecificationPageable(records, null);
        long t2 = System.currentTimeMillis();
        log.debug("findMyApplyByCriteriaPageable查询所有记录花费时间: {} ms", (t2-t1));
        return records;
    }

    @Override
    public int countMyApplyByCriteria(TripRequestForm requestForm) {
        Specification<TripReimburse> spec = Specification.where(TripRbsSpecifications.createUseridEqual(requestForm))
                .and(TripRbsSpecifications.idLike(requestForm))
                .and(TripRbsSpecifications.afterStartDate(requestForm))
                .and(TripRbsSpecifications.beforeEndDate(requestForm))
                .and(TripRbsSpecifications.staffLike(requestForm))
                .and(TripRbsSpecifications.stateEqual(requestForm))
                .and(TripRbsSpecifications.isNotDelete(requestForm))
                .and(TripRbsSpecifications.isMainData())
                ;
        final long count = tripRepository.count(spec);
        return (int) count;
    }


    @Override
    public com.abt.common.model.Page<TripReimburseForm>  findMyDoneByCriteriaPaged(TripRequestForm requestForm) {
        requestForm.forcePaged();
        //criteria: 申请人(),申请日期(startDate-endDate),出差人包含(staff), 审批编号(id), 状态(state), 分页
        long t1 = System.currentTimeMillis();
        final List<TripReimburse> doneMainList = tripReimburseTaskRepository.findDoneMainList(requestForm.getPage(), requestForm.getLimit(), requestForm.getId(), requestForm.getState(),
                requestForm.getUserid(), requestForm.getUsername(), requestForm.getStartDate(), requestForm.getEndDate());
        com.abt.common.model.Page<TripReimburseForm> page = new com.abt.common.model.Page<>();
        if (CollectionUtils.isEmpty(doneMainList)) {
            return page;
        }
        List<TripReimburseForm> records = new ArrayList<>();
        //查询明细数据
        for (TripReimburse main: doneMainList) {
            TripReimburseForm form = new TripReimburseForm();
            final List<TripReimburse> items = tripRepository.findByRootIdOrderBySortAsc(main.getId());
            form.setCommon(main);
            form.setItems(items);
            records.add(form);
        }
        final int total = tripReimburseTaskRepository.countDoneList(requestForm.getPage(), requestForm.getLimit(), requestForm.getId(), requestForm.getState(),
                requestForm.getUserid(), requestForm.getStartDate(), requestForm.getEndDate());
        page.setTotal(total);
        page.setContent(records);
        long t2 = System.currentTimeMillis();
        log.debug("findMyDoneByCriteriaPaged查询所有记录花费时间: {} ms", (t2-t1));
        return page;
    }

    @Deprecated
    @Override
    public List<TripReimburseForm> findMyDoneByCriteriaPageable(TripRequestForm requestForm) {
        requestForm.forcePaged();
        //criteria: 申请人(),申请日期(startDate-endDate),出差人包含(staff), 审批编号(id), 状态(state), 分页
        long t1 = System.currentTimeMillis();
        //查询entity
        Specification<TripReimburse> spec = Specification.where(TripRbsSpecifications.createUseridEqual(requestForm))
                .and(TripRbsSpecifications.idOrRootIdLike(requestForm))
                .and(TripRbsSpecifications.afterStartDate(requestForm))
                .and(TripRbsSpecifications.beforeEndDate(requestForm))
                .and(TripRbsSpecifications.staffLike(requestForm))
                .and(TripRbsSpecifications.stateEqual(requestForm))
                .and(TripRbsSpecifications.isNotDelete(requestForm))
                ;
        Pageable pageable = PageRequest.of(requestForm.getFirstResult(), requestForm.getLimit(),
                Sort.by(Sort.Order.by("processInstanceId"), Sort.Order.desc("createDate"), Sort.Order.asc("sort")));

        final List<TripReimburse> trips = tripRepository.findAll(spec, pageable).getContent();
        List<TripReimburseForm> records = build(trips);
        List<TripReimburseForm> done = new ArrayList<>();
        final List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processDefinitionKey(DEF_KEY_TRIP)
                .taskAssignee(requestForm.getAssigneeId()).finished()
                .orderByHistoricTaskInstanceEndTime().desc().list();
        list.forEach(h -> {
            if (!h.getTaskDefinitionKey().contains("apply"))  {
                String processInstanceId = h.getProcessInstanceId();
                records.stream().filter(i -> processInstanceId.equals(i.getCommon().getProcessInstanceId())).findFirst().ifPresent(i -> {
                    i.setInvokedTaskAssigneeId(requestForm.getAssigneeId());
                    i.setInvokedTaskAssigneeName(requestForm.getAssigneeName());
                    i.setInvokedTaskDefId(h.getTaskDefinitionKey());
                    i.setInvokedTaskId(h.getId());
                    i.setInvokedTaskName(h.getName());
                    done.add(i);
                });
            }

        });
        long t2 = System.currentTimeMillis();
        log.debug("findMyDoneByCriteriaPageable查询消耗时间: {} ms", (t2-t1));
        return done;
    }

    @Override
    public int countMyDoneByCriteria(TripRequestForm requestForm) {
        Specification<TripReimburse> spec = Specification.where(TripRbsSpecifications.createUseridEqual(requestForm))
                .and(TripRbsSpecifications.idLike(requestForm))
                .and(TripRbsSpecifications.afterStartDate(requestForm))
                .and(TripRbsSpecifications.beforeEndDate(requestForm))
                .and(TripRbsSpecifications.staffLike(requestForm))
                .and(TripRbsSpecifications.stateEqual(requestForm))
                .and(TripRbsSpecifications.isNotDelete(requestForm))
                ;
        final long count = tripRepository.count(spec);
        return (int) count;
    }

    @Override
    public List<TripReimburseForm> findMyTodoByCriteria(TripRequestForm requestForm) {
        return List.of();
    }

    @Override
    public com.abt.common.model.Page<TripReimburseForm> findMyTodoByCriteriaPaged(TripRequestForm requestForm) {
        requestForm.forcePaged();
        //criteria: 申请人(),申请日期(startDate-endDate),出差人包含(staff), 审批编号(id), 状态(state), 分页
        long t1 = System.currentTimeMillis();
        final List<TripReimburse> todoMainList = tripReimburseTaskRepository.findTodoMainList(requestForm.getPage(), requestForm.getLimit(), requestForm.getId(), requestForm.getState(),
                requestForm.getUserid(), requestForm.getUsername(), requestForm.getStartDate(), requestForm.getEndDate());
        com.abt.common.model.Page<TripReimburseForm> page = new com.abt.common.model.Page<>();
        if (CollectionUtils.isEmpty(todoMainList)) {
            return page;
        }
        List<TripReimburseForm> records = new ArrayList<>();
        //查询明细数据
        for (TripReimburse main: todoMainList) {
            TripReimburseForm form = new TripReimburseForm();
            final List<TripReimburse> items = tripRepository.findByRootIdOrderBySortAsc(main.getId());
            form.setCommon(main);
            form.setItems(items);
            records.add(form);
        }
        final int total = tripReimburseTaskRepository.countTodoList(requestForm.getPage(), requestForm.getLimit(), requestForm.getId(), requestForm.getState(),
                requestForm.getUserid(), requestForm.getStartDate(), requestForm.getEndDate());
        page.setTotal(total);
        page.setContent(records);
        long t2 = System.currentTimeMillis();
        log.debug("findMyTodoByCriteriaPaged查询所有记录花费时间: {} ms", (t2-t1));
        return page;
    }

    @Override
    public int countMyTodoByCriteria(TripRequestForm requestForm) {
        return tripReimburseTaskRepository.countTodoList(requestForm.getPage(), requestForm.getLimit(), requestForm.getId(), requestForm.getState(),
                requestForm.getUserid(), requestForm.getStartDate(), requestForm.getEndDate());
    }

    @Override
    public TripReimburseForm saveEntity(TripReimburseForm entity) {
        return null;
    }

    public List<TripReimburseForm> build(List<TripReimburse> common, List<TripReimburse> detail) {
        List<TripReimburseForm> forms = new ArrayList<>();
        common.forEach(v -> {
            TripReimburseForm form = new TripReimburseForm();
            String rootId = v.getId();
            form.setCommon(v);
            final List<TripReimburse> childList = detail.stream().filter(i -> rootId.equals(i.getRootId())).toList();
            form.setItems(childList);
            form.setProcessInstanceId(v.getProcessInstanceId());
            form.setProcessDefinitionKey(v.getProcessDefinitionKey());
            form.setProcessDefinitionId(v.getProcessDefinitionId());
            forms.add(form);
        });
        return forms;
    }


    public List<TripReimburseForm> build(List<TripReimburse> list) {
        List<TripReimburseForm> forms = new ArrayList<>();
        final Map<String, TripReimburse> roots = list.stream().filter(i -> StringUtils.isBlank(i.getRootId())).collect(Collectors.toMap(TripReimburse::getId, Function.identity()));
        roots.forEach((k, v) -> {
            TripReimburseForm form = new TripReimburseForm();
            String rootId = v.getId();
            form.setCommon(v);
            final List<TripReimburse> childList = list.stream().filter(i -> rootId.equals(i.getRootId())).toList();
            form.setItems(childList);
            form.setProcessInstanceId(v.getProcessInstanceId());
            form.setProcessDefinitionKey(v.getProcessDefinitionKey());
            form.setProcessDefinitionId(v.getProcessDefinitionId());
            forms.add(form);
        });

        return forms;
    }


    static class TripRbsSpecifications {
        public static Specification<TripReimburse> idOrRootIdLike(TripRequestForm form) {
            return (root, query, builder) -> {
                if (StringUtils.isNotBlank(form.getId())) {
                    return builder.or(builder.like(root.get("id"), like(form.getId())), builder.like(root.get("rootId"), like(form.getId())));
                }
                return null;
            };
        }

        public static Specification<TripReimburse> idIn(List<String> ids) {
            return (root, query, builder) -> {
                if (ids != null && !ids.isEmpty()) {
                    return builder.in(root.get("rootId")).value(ids);
                }
                return null;
            };
        }

        public static Specification<TripReimburse> isMainData() {
            return (root, query, builder) -> builder.isNull(root.get("rootId"));
        }

        public static Specification<TripReimburse> idLike(TripRequestForm form) {
            return (root, query, builder) -> {
                if (StringUtils.isNotBlank(form.getId())) {
                    return builder.like(root.get("id"), like(form.getId()));
                }
                return null;
            };
        }

        public static Specification<TripReimburse> afterStartDate(TripRequestForm form) {
            return (root, query, builder) -> {
                if (form.getStartDate() != null) {
                    return builder.greaterThanOrEqualTo(root.get("createDate"), LocalDate.parse(form.getStartDate()));
                }
                return null;
            };
        }

        public static Specification<TripReimburse> beforeEndDate(TripRequestForm form) {
            return (root, query, builder) -> {
                if (form.getEndDate() != null) {
                    return builder.lessThanOrEqualTo(root.get("createDate"), LocalDate.parse(form.getEndDate()));
                }
                return null;
            };
        }

        public static Specification<TripReimburse> createUsernameLike(TripRequestForm form) {
            return (root, query, builder) -> {
                if (StringUtils.isNotBlank(form.getUsername())) {
                    return builder.like(root.get("createUsername"), like(form.getUsername()));
                }
                return null;
            };
        }
        public static Specification<TripReimburse> createUseridEqual(TripRequestForm form) {
            return (root, query, builder) -> {
                if (StringUtils.isNotBlank(form.getUsername())) {
                    return builder.equal(root.get("createUserid"), form.getUserid());
                }
                return null;
            };
        }


        public static Specification<TripReimburse> createUsernameEqual(TripRequestForm form) {
            return (root, query, builder) -> {
                if (StringUtils.isNotBlank(form.getUsername())) {
                    return builder.equal(root.get("createUsername"), form.getUsername());
                }
                return null;
            };
        }

        public static Specification<TripReimburse> staffLike(TripRequestForm form) {
            return (root, query, builder) -> {
                if (StringUtils.isNotBlank(form.getStaff())) {
                    return builder.equal(root.get("staff"), like(form.getStaff()));
                }
                return null;
            };
        }

        public static Specification<TripReimburse> stateEqual(TripRequestForm form) {
            return (root, query, builder) -> {
                if (StringUtils.isNotBlank(form.getState())) {
                    return builder.equal(root.get("businessState"), form.getState());
                }
                return null;
            };
        }

        public static Specification<TripReimburse> isNotDelete(TripRequestForm form) {
            return (root, query, builder) -> builder.isFalse(root.get("isDelete"));
        }
    }
}
