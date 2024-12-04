package com.abt.oa.service.impl;

import com.abt.common.entity.Company;
import com.abt.common.model.Cell;
import com.abt.common.model.Row;
import com.abt.common.model.Table;
import com.abt.common.model.User;
import com.abt.common.util.TimeUtil;
import com.abt.oa.OAConstants;
import com.abt.oa.entity.*;
import com.abt.oa.model.CalendarEvent;
import com.abt.oa.model.FieldConfirmResult;
import com.abt.oa.model.FieldWorkBoard;
import com.abt.oa.model.FieldWorkRequestForm;
import com.abt.oa.reposity.FieldAttendanceSettingRepository;
import com.abt.oa.reposity.FieldWorkItemRepository;
import com.abt.oa.reposity.FieldWorkRepository;
import com.abt.oa.service.FieldWorkService;
import com.abt.oa.service.LeaveService;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.entity.EmployeeInfo;
import com.abt.sys.service.EmployeeService;
import com.abt.sys.util.WithQueryUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.abt.oa.OAConstants.FW_WAITING;
import static com.abt.oa.OAConstants.FW_WITHDRAW;
import static com.abt.sys.config.Constant.SYSTEM_ID;

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
    private final LeaveService leaveService;
    private final Company ABT;
    private final Company GRD;
    private final Company DC;
    private final Configuration freemarkerConfig;


    @Setter
    @Getter
    @Value("${abt.fw.excel.output}")
    private String fieldWorkExcelOutput;


    public static final String excelTemplate = "FW2003.ftl";

    public static final String COL_ATD = "出勤天数";


    public FieldWorkServiceImpl(FieldAttendanceSettingRepository fieldAttendanceSettingRepository, FieldWorkRepository fieldWorkRepository, EmployeeService employeeService, FieldWorkItemRepository fieldWorkItemRepository, LeaveService leaveService, Company ABT, Company GRD, Company DC, Configuration freemarkerConfig) {
        this.fieldAttendanceSettingRepository = fieldAttendanceSettingRepository;
        this.fieldWorkRepository = fieldWorkRepository;
        this.employeeService = employeeService;
        this.fieldWorkItemRepository = fieldWorkItemRepository;
        this.leaveService = leaveService;
        this.ABT = ABT;
        this.GRD = GRD;
        this.DC = DC;
        this.freemarkerConfig = freemarkerConfig;
    }

    @Override
    public List<FieldWorkAttendanceSetting> findAllSettings() {
        return fieldAttendanceSettingRepository.findAll(Sort.by("sort").ascending());
    }

    @Override
    public List<FieldWorkAttendanceSetting> findLatestSettings() {
        final List<FieldWorkAttendanceSetting> all = findAllEnabledAllowance();
        final Map<String, FieldWorkAttendanceSetting> map = all.stream().collect(
                Collectors.toMap(
                        FieldWorkAttendanceSetting::getVid,
                        setting -> setting,
                        (existing, replacement) -> existing.getVersion() > replacement.getVersion() ? existing : replacement
                )
        );
        //版本数量
        final Map<String, Long> countMap = all.stream().collect(Collectors.groupingBy(FieldWorkAttendanceSetting::getVid, Collectors.counting()));
        map.values().forEach(i -> i.setVersionCount(countMap.get(i.getVid())));
        final List<FieldWorkAttendanceSetting> list = map.values().stream().sorted(Comparator.comparingInt(FieldWorkAttendanceSetting::getSort)).toList();
        return new ArrayList<>(list);
    }

    private void validateFieldSetting(FieldWorkAttendanceSetting fieldAttendanceSetting) {
        //校验重名
        if (StringUtils.isBlank(fieldAttendanceSetting.getId())) {
            //insert
            final List<FieldWorkAttendanceSetting> list = fieldAttendanceSettingRepository.findByName(fieldAttendanceSetting.getName());
            if (list != null && !list.isEmpty()) {
                throw new BusinessException("补贴名称:" + fieldAttendanceSetting.getName() + " 已存在。补贴名称不能重复");
            }
        }
    }

    @Override
    public void saveSetting(FieldWorkAttendanceSetting fieldAttendanceSetting) {
        validateFieldSetting(fieldAttendanceSetting);
        if (StringUtils.isEmpty(fieldAttendanceSetting.getVid())) {
            fieldAttendanceSetting.setVid(UUID.randomUUID().toString());
        }
        fieldAttendanceSetting = fieldAttendanceSetting.newVersion(fieldAttendanceSetting);
        fieldAttendanceSettingRepository.save(fieldAttendanceSetting);
    }

    @Override
    public List<FieldWorkAttendanceSetting> findHistorySettings(String vid) {
        return fieldAttendanceSettingRepository.findByVidOrderByVersionDesc(vid);
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

    @Override
    public List<FieldWorkAttendanceSetting> findAllEnabledAllowance() {
        return fieldAttendanceSettingRepository.findByEnabledOrderBySortAsc(true);
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
        fw.setReviewResult(FW_WAITING);
        fw = fieldWorkRepository.save(fw);
        String id = fw.getId();
        fw.getItemIds().forEach(i -> {
            final FieldWorkAttendanceSetting fwa = findSettingById(i);
            final FieldWorkItem fi = FieldWorkItem.create(fwa, id);
            fieldWorkItemRepository.save(fi);
        });
    }

    public FieldWorkAttendanceSetting findSettingById(String id) {
        return fieldAttendanceSettingRepository.findById(id).orElseThrow(() -> new BusinessException("失败!。原因：未查询到野外补助配置项(id=" + id + ")"));
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
        final Page<FieldWork> page = fieldWorkRepository.findTodoFetchedByQuery(form.getQuery(), form.getUserid(), FW_WAITING,
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
        final Page<FieldWork> page = fieldWorkRepository.findAllFetchedByQuery(form.getUsername(), form.getQuery(), form.getState(),
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
    public FieldWorkBoard userBoard(String jobNumber, String userid, String startDateStr, String endDateStr) {
        final LocalDate startDate = TimeUtil.toLocalDate(startDateStr);
        final LocalDate endDate = TimeUtil.toLocalDate(endDateStr);
        FieldWorkBoard board = new FieldWorkBoard();
        board.setPeriodEnd(endDate);
        board.setPeriodStart(startDate);
        assert startDate != null;
        board.setDayCount((int) ChronoUnit.DAYS.between(startDate, endDate) + 1);
        List<CalendarEvent> events = new ArrayList<>();
        List<FieldWorkAttendanceSetting> settings = this.findLatestSettings();

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
        final List<CalendarEvent> leaveEvents = splitLeaveCalendarEventsByDay(leaveRecords);
        events.addAll(leaveEvents);
        //请假天数
        final double leaveDay = leaveEvents.stream().mapToDouble(CalendarEvent::getDuration).sum();
        board.setLeaveDay(leaveDay);

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
                        findSettingById(all, i.getAllowanceId())
                                .ifPresent(a -> {
                                    ae.setBackgroundColor(a.getBackgroundColor());
                                    ae.setShortName(a.getShortName());
                                    ae.setType(fw.getReviewResult());
                                    ae.setDay(fw.getAttendanceDate().getDayOfMonth());
                                    ae.setSid(a.getId());
                                    ae.setDuration(1);
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
            events.add(doCreateLeaveEvent(frmLeaveReq));
        }
        return events;
    }

    private CalendarEvent doCreateLeaveEvent(FrmLeaveReq frmLeaveReq) {
        CalendarEvent event = new CalendarEvent();
        event.setStart(TimeUtil.yyyy_MM_ddString(frmLeaveReq.getStartDate()));
        event.setEnd(TimeUtil.yyyy_MM_ddString(frmLeaveReq.getEndDate()));
        event.setTitle(frmLeaveReq.getRequestTypeName());
        event.setId(frmLeaveReq.getId());
        //请假放后面
        event.setOrder(99);
        event.setType(frmLeaveReq.getIsFinish());
        event.setShortName(frmLeaveReq.getRequestTypeName());
        event.setDuration(frmLeaveReq.getDayTime().doubleValue());
        event.setSid(frmLeaveReq.getRequestTypeName());
        if (frmLeaveReq.getRequestType().equals("2")) {
            //调休
            event.setDurationUnit(CalendarEvent.DUR_UNIT_HOUR);
        } else {
            event.setDurationUnit(CalendarEvent.DUR_UNIT_DAY);
        }

        return event;
    }


    private boolean isWorkDay(List<FieldWorkAttendanceSetting> settings, FieldWorkItem item) {
        final Optional<FieldWorkAttendanceSetting> setting = settings.stream().filter(s -> item.getAllowanceId().equals(s.getId()) && s.isWork()).findFirst();
        return setting.isPresent();
    }

    private boolean isRestDay(List<FieldWorkAttendanceSetting> settings, FieldWorkItem item) {
        final Optional<FieldWorkAttendanceSetting> setting = settings.stream().filter(s -> item.getAllowanceId().equals(s.getId()) && !s.isWork()).findFirst();
        return setting.isPresent();
    }


    private boolean isWorkDay(List<FieldWorkAttendanceSetting> settings, CalendarEvent event) {
        final Optional<FieldWorkAttendanceSetting> setting = settings.stream().filter(s -> event.getSid().equals(s.getId()) && s.isWork()).findFirst();
        return setting.isPresent();
    }

    private boolean isRestDay(List<FieldWorkAttendanceSetting> settings, CalendarEvent item) {
        final Optional<FieldWorkAttendanceSetting> setting = settings.stream().filter(s -> item.getSid().equals(s.getId()) && !s.isWork()).findFirst();
        return setting.isPresent();
    }

    @Transactional
    @Override
    public void deleteFieldWork(String id, String userid) {
        final FieldWork entity = this.findFieldWorkEntity(id);
        if (!entity.getCreateUserid().equals(userid) && !SYSTEM_ID.equals(userid)) {
            throw new BusinessException("无法删除!原因:只有记录的创建人(" + entity.getCreateUsername() + ")或管理员可以删除");
        }
        fieldWorkRepository.softDeleteById(entity.getId());
    }

    @Override
    public void withdraw(String id, String userid) {
        final FieldWork entity = this.findFieldWorkEntity(id);
        if (!entity.getCreateUserid().equals(userid) && !SYSTEM_ID.equals(userid)) {
            throw new BusinessException("无法撤销!原因:只有记录的创建人(" + entity.getCreateUsername() + ")或管理员可以撤销");
        }
        entity.setReviewResult(FW_WITHDRAW);
        fieldWorkRepository.save(entity);
    }

    /**
     * 考勤统计表
     */
    @Override
    public Table createStatData(String yearMonth, LocalDate start, LocalDate end, List<FieldWork> all) {
        long t1 = System.currentTimeMillis();
        //查询记录(已确认的)
        final List<FieldWork> records = all.stream().filter(i -> i.isConfirm() && i.isPass() && !i.isDeleted()).toList();
        final List<FieldWorkItem> items = records.stream().flatMap(fieldWork -> fieldWork.getItems().stream()).toList();
        List<CalendarEvent> allEvents = new ArrayList<>();
//        final List<FieldWorkAttendanceSetting> settings = findLatestSettings();
        final List<FieldWorkAttendanceSetting> allSettings = findAllSettings();
        //生成表
        //根据用户
        final Map<String, List<FieldWork>> groupByUser = records.stream().collect(Collectors.groupingBy(FieldWork::getJobNumber, Collectors.toList()));
        Table table = new Table();
        for (Map.Entry<String, List<FieldWork>> entry : groupByUser.entrySet()) {
            //用户信息
            String userid, username, jobNumber;
            List<FieldWork> userFws = entry.getValue();
            if (CollectionUtils.isEmpty(userFws)) {
                continue;
            }
            userid = userFws.get(0).getUserid();
            username = userFws.get(0).getUsername();
            jobNumber = userFws.get(0).getJobNumber();
            String company = employeeService.getUserCompanyByJobNumber(jobNumber);

            Row row = Row.create(username, jobNumber, company);
            User user = new User(userid, username, jobNumber);
            user.setCompany(company);
            //0. 基础信息
            row.addCell(new Cell(user.getUsername(), "姓名"));

            //生成每个人的考勤
            List<CalendarEvent> userEvents = createFieldWorkCalendarEvents(userFws);
            userEvents.forEach(e -> {
                items.stream().filter(i -> e.getId().equals(i.getId())).findAny().ifPresent(i -> e.setMoneySum(i.getSum()));
            });

            //处理数据,生成表格
            //补贴数据, 生成row，根据日期(start)
            userEvents.forEach(i -> {
                Cell cell = new Cell();
                cell.setColumnName(i.getStart());
                cell.setValue(i);
                cell.setValueStr(i.getShortName());
                cell.setDateColumn(true);
                row.addCell(cell);
            });

            //---- 统计数据 -------

            //1. 出勤
            Set<String> workDaySet = new HashSet<>();
            for (CalendarEvent event : userEvents) {
                if (isWorkDay(allSettings, event)) {
                    workDaySet.add(event.getStart());
                }
            }
            row.addCell(new Cell(String.valueOf(workDaySet.size()), COL_ATD));

            //2. 作业项目统计(包含基地调休/家调休)
            final Map<String, Double> sumByAllowance = userEvents.stream().collect(Collectors.groupingBy(CalendarEvent::getSid, Collectors.summingDouble(CalendarEvent::getDuration)));
            sumByAllowance.forEach((k, v) -> {
                final FieldWorkAttendanceSetting setting = findSettingById(k);
                Cell cell = new Cell(v, setting.getName());
                cell.setSummaryColumn(true);
                CalendarEvent event = new CalendarEvent();
                event.setOrder(1000 + setting.getSort());
                event.setTitle(setting.getName());
                cell.setValue(event);
                row.addCell(cell);
            });

            //请假
//            final List<FrmLeaveReq> leaveRecords = leaveService.findByUser(userid, OAConstants.OPENAUTH_FLOW_STATE_FINISH, start, end);
            //请假数据
//            final List<CalendarEvent> leaveEvents = splitLeaveCalendarEventsByDay(leaveRecords);
//            final Map<String, Double> sumByLeave = leaveEvents.stream().collect(Collectors.groupingBy(CalendarEvent::getSid, Collectors.summingDouble(CalendarEvent::getDuration)));
//            sumByLeave.forEach((k, v) -> {
//                Cell cell = new Cell(String.valueOf(v), k);
//                CalendarEvent event = new CalendarEvent();
//                final CalendarEvent e = leaveEvents.stream().filter(i -> i.getSid().equals(k)).findFirst().get();
//                event.setOrder(2000);
//                event.setTitle(e.getTitle());
//                cell.setValue(event);
//                cell.setValueStr(String.valueOf(v));
//                row.addCell(cell);
//            });
//            allEvents.addAll(leaveEvents);

            //--- 请假end

            //3. 公休，计算每个人的公休天数


            allEvents.addAll(userEvents);

            table.addRow(row);
        }  //end for

        //公休
//        CalendarEvent restEvent = new CalendarEvent();
//        restEvent.setTitle("公休");
//        restEvent.setDurationUnit("天");
//        restEvent.setOrder(5000);
//        allEvents.add(restEvent);


        long t2 = System.currentTimeMillis();
        List<String> header = this.createStatHeader(start, end, allEvents);
        table.setHeaders(header);
        log.info("createStatData耗时:{} (ms), 统计{}人数据\n", (t2-t1), groupByUser.size());
        table.setYearMonth(yearMonth);
        table.setEvents(allEvents);
        table.setStart(start);
        table.setEnd(end);
        return table;
    }

    /**
     * 生成请假事件，多天的请假，每天生成一个事件， start=end
     */
    private List<CalendarEvent> splitLeaveCalendarEventsByDay(List<FrmLeaveReq> records) {
        List<CalendarEvent> list = new ArrayList<>();
        for (FrmLeaveReq frmLeaveReq : records) {
            if (frmLeaveReq.getStartDate().equals(frmLeaveReq.getEndDate())) {
                //一天
                final CalendarEvent event = doCreateLeaveEvent(frmLeaveReq);
                list.add(event);
            } else {
                //多天
                final List<CalendarEvent> events = doSplitLeaveReq(frmLeaveReq);
                list.addAll(events);
            }
        }

        return list;
    }

    private List<CalendarEvent> doSplitLeaveReq(FrmLeaveReq req) {
        List<CalendarEvent> events = new ArrayList<>();
        LocalDate startDate = req.getStartDate();
        LocalDate endDate = req.getEndDate();
        Stream.iterate(startDate, date -> date.plusDays(1))
                .limit(startDate.until(endDate).getDays() + 1)
                .forEach(date -> {
                    CalendarEvent event = doCreateLeaveEvent(req);
                    event.setId(UUID.randomUUID().toString());
                    event.setStart(TimeUtil.yyyy_MM_ddString(date));
                    event.setEnd(TimeUtil.yyyy_MM_ddString(date));
                    if (date.isEqual(startDate)) {
                        //时间，从上午开始算1天
                        if (isMorning(TimeUtil.toLocalTime(req.getStartTime()))) {
                            event.setDuration(1);
                        } else {
                            event.setDuration(0.5);
                        }
                    } else if (date.isEqual(endDate)) {
                        if (isMorning(TimeUtil.toLocalTime(req.getEndTime()))) {
                            event.setDuration(0.5);
                        } else {
                            event.setDuration(1);
                        }
                    } else {
                        event.setDuration(1);
                    }
                    events.add(event);
                });
        return events;
    }

    /**
     * 是否是上午
     * @param time 时间
     */
    private boolean isMorning(LocalTime time) {
        return !time.isAfter(LocalTime.of(12, 0, 0));
    }

    private boolean isAfternoon(LocalTime time) {
        return time.isAfter(LocalTime.of(12, 0, 0));
    }


    /**
     * 生成表头中的日期
     */
    private List<LocalDate> createDateHeader(LocalDate startDate, LocalDate endDate) {
        int dayCount = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
        List<LocalDate> header = new ArrayList<>();
        for (int i = 0; i < dayCount ; i++) {
            header.add(startDate.plusDays(i));
        }
        return header;
    }


    private List<CalendarEvent> createHeader(List<CalendarEvent> events) {
        return events.stream()
                .collect(Collectors.toMap(
                        CalendarEvent::getTitle,
                        event -> event,
                        (existing, replacement) -> existing
                ))
                .values().stream()
                .sorted(Comparator.comparingInt(CalendarEvent::getOrder))
                .toList();
    }


    /**
     * 根据event生成表头
     * 生成表头: 序号,姓名，工号，日期，统计列
     */
    public List<String> createStatHeader(LocalDate startDate, LocalDate endDate, List<CalendarEvent> events) {
        final List<LocalDate> dateHeader = this.createDateHeader(startDate, endDate);
        List<String> header = new ArrayList<>(dateHeader.stream().map(LocalDate::toString).toList());
        header.add(COL_ATD);
        //统计列
        //其他统计-补贴项目(仅显示已有的)
        createHeader(events).stream().map(CalendarEvent::getTitle).forEach(header::add);
        return header;
    }

    @Override
    public List<FieldWork> findAtdByUserInfo(String jobNumber, String dept, String company, LocalDate start, LocalDate end) {
        return fieldWorkRepository.findRecordsByUserInfo(jobNumber, dept, company, start, end);
    }


    public Map<String, Object> createExcelData(Table table, String shortCompany) {
        Assert.notNull(shortCompany, "shortCompany must not be null");
        log.info("create excel data for {}", shortCompany);
        String code = convertCompany(shortCompany);
        //freemarker 所需数据结构
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("yearMonth", table.getYearMonth());
        List<CalendarEvent> headers = this.createHeader(table.getEvents());
        //表头-补贴名称
        List<String> summaryHeader = new ArrayList<>(headers.stream().map(CalendarEvent::getTitle).toList());
        summaryHeader.add(0, COL_ATD);
        dataModel.put(code + "SummaryHeader", summaryHeader);
        //表头1-日期+简称
        final List<LocalDate> dateHeader = this.createDateHeader(table.getStart(), table.getEnd());
        List<String> shortHeader = new ArrayList<>();
        shortHeader.add("");
        shortHeader.add("");
        List<String> dateStrs = dateHeader.stream().map(TimeUtil::yyyy_MM_ddString).toList();
        final List<String> shortStr = dateStrs.stream().map(s -> s.substring(s.length() - 2)).toList();
        shortHeader.addAll(shortStr);
        shortHeader.add(COL_ATD);
        shortHeader.addAll(headers.stream().map(CalendarEvent::getShortName).toList());
        dataModel.put(code + "SummaryHeaderShort", shortHeader);
        //表头2-星期+补贴金额
        //星期
        List<String> shortHeader2 = new ArrayList<>();
        final List<String> weekDays = dateHeader.stream().map(LocalDate::getDayOfWeek).map(TimeUtil::chinaDayOfWeek).toList();
        shortHeader2.add("序号");
        shortHeader2.add("姓名");
        shortHeader2.addAll(weekDays);
        //出勤天数对应空
        shortHeader2.add("出勤天数");
        //补贴金额
        shortHeader2.addAll(headers.stream().map(CalendarEvent::getMoneySum).map(String::valueOf).toList());
        dataModel.put(code + "SummaryHeaderShort2", shortHeader2);

        //筛选数据
        List<String> header = new ArrayList<>();
        header.add("序号");
        header.add("姓名");
        header.addAll(dateStrs);
        header.addAll(summaryHeader);
        //data
        final List<Row> abtEvents = table.getRows().stream().filter(r -> shortCompany.equals(r.getCompany())).toList();
        List<List<String>> data = new ArrayList<>();
        //获取每个人的数据，顺序必须和header一样
        for (Row row : abtEvents) {
            List<String> eRow = new ArrayList<>();
            for (String h : header) {
                final List<String> values = row.getCells().stream().filter(c -> h.equals(c.getColumnName())).map(Cell::getValueStr).toList();
                String join = String.join("\n", values);
                if (join.endsWith("\n")) {
                    join = join.replace("\n", "");
                }
                eRow.add(join);
            }
            data.add(eRow);
        }
        dataModel.put(code + "List", data);
        return dataModel;
    }



    @Override
    public File writeExcel(Table table) throws IOException, TemplateException {
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.putAll(createExcelData(table, "A"));
        dataModel.putAll(createExcelData(table, "G"));
        dataModel.putAll(createExcelData(table, "D"));

        Template template = freemarkerConfig.getTemplate(excelTemplate);
        String fileName = System.currentTimeMillis() + ".xls";
        String path = fieldWorkExcelOutput + fileName;
        try(FileWriter fw = new FileWriter(path)) {
            template.process(dataModel, fw);
            return new File(path);
        }
    }

    private String convertCompany(String shortCompany) {
        if (StringUtils.isBlank(shortCompany)) {
            return shortCompany;
        }
        if (shortCompany.equals(ABT.getShortCode())) {
            return ABT.getCode();
        } else if (shortCompany.equals(GRD.getShortCode())) {
            return GRD.getCode();
        } else if (shortCompany.equals(DC.getShortCode())) {
            return DC.getCode();
        }
        return shortCompany;
    }

    /**
     * 转为excel需要的map数据
     */
    private Map<String, String> convertExcelData(Cell cell) {
        return Map.of(cell.getColumnName(), cell.getValueStr());
    }


    @Override
    public FieldWork detail(String id) {
        return WithQueryUtil.build(fieldWorkRepository.findById(id).orElseThrow(() -> new BusinessException("未查询到考勤记录(id=" + id + ")")));
    }


    @Override
    public int confirm(String userid, List<String> ids) {
        //TODO: 校验userid与考勤人是否一致
        final List<FieldWork> list = fieldWorkRepository.findAllById(ids);
        list.forEach(i -> i.confirm(userid));
        fieldWorkRepository.saveAllAndFlush(list);
        return list.size();
    }

    @Override
    public FieldConfirmResult getConfirmStat(FieldWorkRequestForm form) {
        FieldConfirmResult result = new FieldConfirmResult();
        final Page<FieldWork> atdRecord = findAtdRecord(form);
        List<FieldWork> list = new ArrayList<>(atdRecord.getContent());
        final Map<LocalDate, List<FieldWork>> map = list.stream().collect(Collectors.groupingBy(FieldWork::getAttendanceDate, Collectors.toList()));
        LocalDate startDate = TimeUtil.toLocalDate(form.getStartDate());
        LocalDate endDate = TimeUtil.toLocalDate(form.getEndDate());
        assert startDate != null;
        final List<LocalDate> dateList = Stream.iterate(startDate, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(startDate, endDate) + 1)
                .toList();
        dateList.forEach(d -> {
            final List<FieldWork> fws = map.get(d);
            if (fws == null || fws.isEmpty()) {
                //新增一个
                FieldWork add = new FieldWork();
                add.setAttendanceDate(d);
                list.add(add);
            }
        });
        //重新排序
        list.sort(Comparator.comparing(FieldWork::getAttendanceDate));
        result.setRecords(list);

        //-- 统计
        //合并list
//        final List<FieldWorkItem> itemList = list.stream().flatMap(fw -> fw.getItems().stream()).toList();

        return result;
    }


    @Override
    public Boolean isDuplicatedDate(LocalDate date, String userid) {
        //查询用户的考勤记录
        final List<FieldWork> list = fieldWorkRepository.findByAttendanceDateAndUserid(date, userid);
        if (list == null || list.isEmpty()) {
            return false;
        }
        final List<FieldWork> filtered = list.stream().filter(i -> (i.isPass() || i.isWaiting()) && (!i.isDeleted())).toList();
        return !filtered.isEmpty();
    }

}
