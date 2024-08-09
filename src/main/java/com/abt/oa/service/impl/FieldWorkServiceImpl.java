package com.abt.oa.service.impl;

import com.abt.common.model.User;
import com.abt.common.util.TimeUtil;
import com.abt.oa.OAConstants;
import com.abt.oa.entity.FieldWork;
import com.abt.oa.entity.FieldWorkAttendanceSetting;
import com.abt.oa.entity.FieldWorkItem;
import com.abt.oa.entity.FrmLeaveReq;
import com.abt.oa.model.CalendarEvent;
import com.abt.oa.model.FieldWorkRequestForm;
import com.abt.oa.model.FieldWorkUserBoard;
import com.abt.oa.reposity.FieldAttendanceSettingRepository;
import com.abt.oa.reposity.FieldWorkItemRepository;
import com.abt.oa.reposity.FieldWorkRepository;
import com.abt.oa.service.FieldWorkService;
import com.abt.oa.service.LeaveService;
import com.abt.oa.service.PaiBanService;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.entity.EmployeeInfo;
import com.abt.sys.service.EmployeeService;
import com.abt.sys.util.WithQueryUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 */
@Slf4j
@Service
public class FieldWorkServiceImpl implements FieldWorkService {

    private final FieldAttendanceSettingRepository fieldAttendanceSettingRepository;
    private final FieldWorkRepository fieldWorkRepository;
    private final EmployeeService employeeService;
    private final FieldWorkItemRepository fieldWorkItemRepository;
    private final PaiBanService paiBanService;
    private final LeaveService leaveService;

    public FieldWorkServiceImpl(FieldAttendanceSettingRepository fieldAttendanceSettingRepository, FieldWorkRepository fieldWorkRepository, EmployeeService employeeService, FieldWorkItemRepository fieldWorkItemRepository, PaiBanService paiBanService, LeaveService leaveService) {
        this.fieldAttendanceSettingRepository = fieldAttendanceSettingRepository;
        this.fieldWorkRepository = fieldWorkRepository;
        this.employeeService = employeeService;
        this.fieldWorkItemRepository = fieldWorkItemRepository;
        this.paiBanService = paiBanService;
        this.leaveService = leaveService;
    }

    @Override
    public List<FieldWorkAttendanceSetting> findAllSettings() {
        return fieldAttendanceSettingRepository.findAll(Sort.by("sort").ascending());
    }

    @Override
    public void saveSetting(FieldWorkAttendanceSetting fieldAttendanceSetting) {
        fieldAttendanceSettingRepository.save(fieldAttendanceSetting);
    }

    @Override
    public List<FieldWorkAttendanceSetting> findAllEnabledAllowance() {
        return fieldAttendanceSettingRepository.findByEnabledOrderBySortAsc(true);
    }

    @Override
    public List<FieldWork> findUserRecord(FieldWork query) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("username", ExampleMatcher.GenericPropertyMatcher::contains)
                .withMatcher("attendanceDate", ExampleMatcher.GenericPropertyMatcher::exact)
                .withMatcher("createUserid", ExampleMatcher.GenericPropertyMatcher::exact);
//                .withIgnorePaths("enable", "sort");
        Example<FieldWork> example = Example.of(query, matcher);
        fieldWorkRepository.findAll(example,
                Sort.by(Sort.Order.asc("createUserid"), Sort.Order.desc("attendanceDate"), Sort.Order.asc("reviewTime")));
        return null;
    }

    //员工表中部门经理
    public static final String EMP_POS_MGR = "部门经理";
    //工程技术部，西南项目部
    public static final List<String> fieldWorkDept = List.of("c3626282-a499-4354-8330-b49fff6887b9", "e5b9f524-485b-496f-9786-8a92a1a9ad2c");

    //查询用户野外考勤审批人
    @Override
    public User findUserReviewer(String userDept) {
        if (fieldWorkDept.contains(userDept)) {
            EmployeeInfo example = new EmployeeInfo();
            example.setDept(userDept);
            example.setPosition(EMP_POS_MGR);
            //工程技术部和西南项目部选部门经理
            final List<EmployeeInfo> list = employeeService.getByExample(example);
            if (list != null && !list.isEmpty()) {
                return new User(list.get(0));
            }
        }
        return null;
    }


    @Transactional
    @Override
    public void saveFieldWork(FieldWork fw) {
        fw.setReviewResult(OAConstants.FW_WAITING);
        fw = fieldWorkRepository.save(fw);
        String id = fw.getId();
        fw.getItemIds().forEach(i -> {
            final FieldWorkAttendanceSetting fwa = findSettingById(i);
            final FieldWorkItem fi = FieldWorkItem.create(fwa, id);
            fieldWorkItemRepository.save(fi);
        });
    }

    public FieldWorkAttendanceSetting findSettingById(String id) {
        return fieldAttendanceSettingRepository.findById(id).orElseThrow(() -> new BusinessException("保存失败!。原因：未查询到野外补助配置项(id=" + id + ")"));
    }

    @Override
    public List<FieldWork> saveFieldWorkList(List<FieldWork> list, String reviewerId, String reviewerName) {
        List<FieldWorkItem> fwiBatch = new ArrayList<>();
        List<FieldWork> errList = new ArrayList<>();
        final Map<LocalDate, List<FieldWork>> groupByDate = list.stream().collect(Collectors.groupingBy(FieldWork::getAttendanceDate, Collectors.toList()));
        for (Map.Entry<LocalDate, List<FieldWork>> entry : groupByDate.entrySet()) {
            List<FieldWork> value = entry.getValue();
            if (value != null && !value.isEmpty()) {
                FieldWork fw = value.get(0);
                fw.setReviewResult(OAConstants.FW_PASS);
                fw.setReviewerId(reviewerId);
                fw.setReviewerName(reviewerName);
                fw.setReviewTime(LocalDateTime.now());
                for (FieldWork v : value) {
                    String aid = v.getSingleId();
                    try {
                        final FieldWorkAttendanceSetting setting = findSettingById(aid);
                        final FieldWorkItem fi = FieldWorkItem.create(setting, null);
                        fwiBatch.add(fi);
                        fw.addItem(fi);
                        final BigDecimal sum = BigDecimal.valueOf(fi.getSum()).add(BigDecimal.valueOf(fw.getSum()));
                        fw.setSum(sum.doubleValue());
                    } catch (BusinessException e) {
                        log.error("未查询到野外补贴配置项， 错误信息", e);
                        errList.add(v);
                    } catch (Exception e) {
                        log.error("系统错误: ", e);
                        errList.add(v);
                    }
                }

                fw = fieldWorkRepository.save(fw);
                final FieldWork finalFw = fw;
                fw.getItems().forEach(i -> i.setFid(finalFw.getId()));

            }
        }
        fieldWorkItemRepository.saveAllAndFlush(fwiBatch);

        return errList;
    }

    @Override
    public Page<FieldWork> findTodoRecords(FieldWorkRequestForm form) {
        final PageRequest pageRequest = PageRequest.of(form.jpaPage(), form.getLimit(), Sort.by(Sort.Order.asc("createDate")));
        final Page<FieldWork> page = fieldWorkRepository.findTodoFetchedByQuery(form.getQuery(), form.getUserid(), form.getState(),
                TimeUtil.toLocalDate(form.getStartDate()), TimeUtil.toLocalDate(form.getEndDate()), pageRequest);
        WithQueryUtil.build(page.getContent());
        return page;
    }

    @Override
    public Page<FieldWork> findDoneRecords(FieldWorkRequestForm form) {
        final PageRequest pageRequest = PageRequest.of(form.jpaPage(), form.getLimit(), Sort.by(Sort.Order.asc("createDate")));
        final Page<FieldWork> page = fieldWorkRepository.findDoneFetchedByQuery(form.getQuery(), form.getUserid(), form.getState(),
                TimeUtil.toLocalDate(form.getStartDate()), TimeUtil.toLocalDate(form.getEndDate()), pageRequest);
        WithQueryUtil.build(page.getContent());
        return page;
    }

    @Override
    public Page<FieldWork> findApplyRecords(FieldWorkRequestForm form) {
        final PageRequest pageRequest = PageRequest.of(form.jpaPage(), form.getLimit(), Sort.by(Sort.Order.asc("createDate")));
        final Page<FieldWork> page = fieldWorkRepository.findApplyFetchedByQuery(form.getQuery(), form.getUserid(), form.getState(),
                TimeUtil.toLocalDate(form.getStartDate()), TimeUtil.toLocalDate(form.getEndDate()), pageRequest);
        WithQueryUtil.build(page.getContent());
        return page;
    }


    @Override
    public Page<FieldWork> findAllRecords(FieldWorkRequestForm form) {
        final PageRequest pageRequest = PageRequest.of(form.jpaPage(), form.getLimit(), Sort.by(Sort.Order.asc("createDate")));
        final Page<FieldWork> page = fieldWorkRepository.findAllFetchedByQuery(form.getQuery(), form.getState(),
                TimeUtil.toLocalDate(form.getStartDate()), TimeUtil.toLocalDate(form.getEndDate()), pageRequest);
        WithQueryUtil.build(page.getContent());
        return page;
    }

    @Override
    public Page<FieldWork> findAtdRecord(FieldWorkRequestForm form) {
        final PageRequest pageRequest = PageRequest.of(form.jpaPage(), form.getLimit(), Sort.by(Sort.Order.asc("createDate")));
        final Page<FieldWork> page = fieldWorkRepository.findAtdFetchedByQuery(form.getQuery(), form.getUserid(), form.getState(),
                TimeUtil.toLocalDate(form.getStartDate()), TimeUtil.toLocalDate(form.getEndDate()), pageRequest);
        WithQueryUtil.build(page.getContent());
        return page;
    }

    @Override
    public void reject(String id, String userid, String reason) {
        final FieldWork fw = this.findFieldWorkEntity(id);
        validateReviewer(userid, fw);
        reject(fw, reason);
    }

    @Override
    public void pass(String id, String userid) {
        final FieldWork fw = this.findFieldWorkEntity(id);
        validateReviewer(userid, fw);
        pass(fw);
    }

    public void pass(FieldWork fw) {
        fw.setReviewResult(OAConstants.FW_PASS);
        fw.setReviewTime(LocalDateTime.now());
        fieldWorkRepository.save(fw);
    }

    private void reject(FieldWork fw, String reason) {
        fw.setReviewTime(LocalDateTime.now());
        fw.setReviewResult(OAConstants.FW_REJECT);
        fw.setReviewReason(reason);
        fieldWorkRepository.save(fw);
    }

    private void validateReviewer(String userid, FieldWork fw) {
        if (!userid.equals(fw.getReviewerId())) {
            log.error("用户{}不是当前考勤记录审批人", userid);
            throw new BusinessException("用户(id=" + userid + ")不是当前考勤记录审批人，无法审批！");
        }
    }

    public FieldWork findFieldWorkEntity(String id) {
        return fieldWorkRepository.findById(id).orElseThrow(() -> new BusinessException("未查询到考勤记录(id=" + id + ")"));
    }

    @Override
    public FieldWorkUserBoard userBoard(String jobNumber, String userid, String startDateStr, String endDateStr) {
        final LocalDate startDate = TimeUtil.toLocalDate(startDateStr);
        final LocalDate endDate = TimeUtil.toLocalDate(endDateStr);
        FieldWorkUserBoard board = new FieldWorkUserBoard();
        board.setPeriodEnd(endDate);
        board.setPeriodStart(startDate);
        assert startDate != null;
        board.setDayCount((int) ChronoUnit.DAYS.between(startDate, endDate) + 1);
        List<CalendarEvent> events = new ArrayList<>();
        List<FieldWorkAttendanceSetting> settings = this.findAllSettings();

        //查询所有记录
        final List<FieldWork> allRecords = fieldWorkRepository.findByJobNumberAndAttendanceDateBetween(jobNumber, startDate, endDate);
        final List<FieldWork> passRecords = allRecords.stream().filter(FieldWork::isPass).toList();
        board.setPassCount(passRecords.size());
        board.setApplyCount(allRecords.size());
        final List<FieldWork> reviewRecords = fieldWorkRepository.findByReviewerIdAndAttendanceDateBetween(userid, startDate, endDate);
        final int todoCount = (int) reviewRecords.stream().filter(FieldWork::isWaiting).count();
        board.setTodoCount(todoCount);
        board.setDoneCount(reviewRecords.size() - todoCount);

        //出勤
        Set<LocalDate> workDaySet = new HashSet<>();
        Set<LocalDate> resetDaySet = new HashSet<>();
        //出勤天数: 不包含《不计算考勤》项目
        passRecords.forEach(fw -> {
            fw.getItems().forEach(i -> {
                if (isWorkDay(settings, i)) {
                    workDaySet.add(fw.getAttendanceDate());
                } else if (isRestDay(settings, i)) {
                    resetDaySet.add(fw.getAttendanceDate());
                }
            });
        });
        board.setWorkDay(workDaySet.size());
        board.setRestDay(resetDaySet.size());
        //补贴event
        events.addAll(createFieldWorkCalendarEvents(allRecords));
        //请假event(仅通过的)
        final List<FrmLeaveReq> leaveRecords = leaveService.findByUser(userid, OAConstants.OPENAUTH_FLOW_STATE_FINISH, startDate, endDate);
        final List<CalendarEvent> leaveEvents = createLeaveCalendarEvents(leaveRecords);
        //请假天数
        events.addAll(leaveEvents);
        board.setLeaveDay(leaveEvents.size());


        board.setEvents(events);
        return board;
    }

    /**
     * 野外考勤的日历显示规则：
     * 1. 日历中仅显示审批通过的考勤的补贴项目，审批被拒绝或审批中的不显示
     * 2. 审批中以event形式展示
     * 3. 当天的所有审批都被拒绝，则显示被拒绝
     */
    private List<CalendarEvent> createFieldWorkCalendarEvents(List<FieldWork> record) {
        List<CalendarEvent> events = new ArrayList<>();
        final Map<LocalDate, Set<FieldWork>> groupByDate = record.stream().collect(Collectors.groupingBy(FieldWork::getAttendanceDate, Collectors.toSet()));
        final List<FieldWorkAttendanceSetting> all = fieldAttendanceSettingRepository.findAll();
        for (Map.Entry<LocalDate, Set<FieldWork>> entry : groupByDate.entrySet()) {
            Boolean pass = null, reject = null;
            for (FieldWork fw : entry.getValue()) {
                //有一个pass就算pass
                pass = pass == null ? fw.isPass() : (pass || fw.isPass());
                reject = reject == null ? fw.isReject() : (reject && fw.isReject());
                if (fw.isPass() || fw.isWaiting()) {
                    //审批通过的event
                    fw.getItems().forEach(i -> {
                        final CalendarEvent ae = create(i, TimeUtil.yyyy_MM_ddString(fw.getAttendanceDate()), CAL_EVENT_TYPE_ALLOWANCE);
                        findSettingById(all, i.getAllowanceId()).ifPresent(a -> {
                            ae.setBackgroundColor(a.getBackgroundColor());
                            ae.setShortName(a.getShortName());
                            ae.setType(fw.getReviewResult());
                        });
                        events.add(ae);
                    });
                }
            }
        }

        return events;
    }

    private Optional<FieldWorkAttendanceSetting> findSettingById(List<FieldWorkAttendanceSetting> list, String id) {
        return list.stream().filter(i -> i.getId().equals(id)).findFirst();
    }

    public static final String CAL_EVENT_TYPE_PASS = "pass";
    public static final String CAL_EVENT_TYPE_REJECT = "reject";
    public static final String CAL_EVENT_TYPE_WAITING = "waiting";
    public static final String CAL_EVENT_TYPE_LEAVE = "leave";
    public static final String CAL_EVENT_TYPE_ALLOWANCE = "allowance";

    public CalendarEvent create(FieldWorkItem item, String start, String type) {
        CalendarEvent event = new CalendarEvent();
        if (item == null) {
            return event;
        }

        event.setId(item.getId());
        event.setTitle(item.getAllowanceName());
        event.setOrder(item.getSort());
        event.setStart(start);
        event.setType(type);
        event.build();

        return event;
    }

    //默认绿色
    public static final String BG_PASS = "";
    public static final String BG_REJECT = "red";
    public static final String BG_WAITING = "yellow";


    private CalendarEvent createRejectEvent(LocalDate date) {
        CalendarEvent event = new CalendarEvent();
        event.setTitle("考勤已拒绝");
        event.setStart(TimeUtil.yyyy_MM_ddString(date));
        event.setOrder(0);
        event.setType(CAL_EVENT_TYPE_REJECT);
//        event.build();
        return event;
    }

    private CalendarEvent createWaitingEvent(FieldWork fw) {
        CalendarEvent event = new CalendarEvent();
        event.setTitle("考勤审批中");
        event.setStart(TimeUtil.yyyy_MM_ddString(fw.getAttendanceDate()));
        event.setOrder(0);
        event.setType(CAL_EVENT_TYPE_WAITING);
//        event.build();
        return event;
    }


    //计算请假events
    private List<CalendarEvent> createLeaveCalendarEvents(List<FrmLeaveReq> leaveRecords) {
        List<CalendarEvent> events = new ArrayList<>();
        for (FrmLeaveReq frmLeaveReq : leaveRecords) {
            CalendarEvent event = new CalendarEvent();
            event.setStart(frmLeaveReq.getStartTime());
            event.setEnd(frmLeaveReq.getEndTime());
            event.setTitle(frmLeaveReq.getRequestTypeName());
            event.setId(frmLeaveReq.getId());
            //请假放后面
            event.setOrder(99);
            events.add(event);
            event.setType(frmLeaveReq.getIsFinish());
            event.setShortName(frmLeaveReq.getRequestTypeName());
        }
        return events;

    }

    private boolean isWorkDay(List<FieldWorkAttendanceSetting> settings, FieldWorkItem item) {
        final Optional<FieldWorkAttendanceSetting> setting = settings.stream().filter(s -> item.getAllowanceId().equals(s.getId()) && s.isWork()).findFirst();
        return setting.isPresent();
    }

    private boolean isRestDay(List<FieldWorkAttendanceSetting> settings, FieldWorkItem item) {
        final Optional<FieldWorkAttendanceSetting> setting = settings.stream().filter(s -> item.getAllowanceId().equals(s.getId()) && !s.isWork()).findFirst();
        return setting.isPresent();
    }


}
