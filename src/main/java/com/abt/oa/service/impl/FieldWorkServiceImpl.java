package com.abt.oa.service.impl;

import com.abt.common.model.User;
import com.abt.common.util.TimeUtil;
import com.abt.oa.OAConstants;
import com.abt.oa.entity.FieldWork;
import com.abt.oa.entity.FieldWorkAttendanceSetting;
import com.abt.oa.entity.FieldWorkItem;
import com.abt.oa.model.CalendarEvent;
import com.abt.oa.model.FieldWorkRequestForm;
import com.abt.oa.model.FieldWorkUserBoard;
import com.abt.oa.reposity.FieldAttendanceSettingRepository;
import com.abt.oa.reposity.FieldWorkItemRepository;
import com.abt.oa.reposity.FieldWorkRepository;
import com.abt.oa.service.FieldWorkService;
import com.abt.oa.service.PaiBanService;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.entity.EmployeeInfo;
import com.abt.sys.service.EmployeeService;
import com.abt.sys.util.WithQueryUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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

    public FieldWorkServiceImpl(FieldAttendanceSettingRepository fieldAttendanceSettingRepository, FieldWorkRepository fieldWorkRepository, EmployeeService employeeService, FieldWorkItemRepository fieldWorkItemRepository, PaiBanService paiBanService) {
        this.fieldAttendanceSettingRepository = fieldAttendanceSettingRepository;
        this.fieldWorkRepository = fieldWorkRepository;
        this.employeeService = employeeService;
        this.fieldWorkItemRepository = fieldWorkItemRepository;
        this.paiBanService = paiBanService;
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
                .withMatcher("createUserid", ExampleMatcher.GenericPropertyMatcher::exact)
                ;
//                .withIgnorePaths("enable", "sort");
        Example<FieldWork> example = Example.of(query, matcher);
        fieldWorkRepository.findAll(example,
                Sort.by(Sort.Order.asc("createUserid"),  Sort.Order.desc("attendanceDate"), Sort.Order.asc("reviewTime")));
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
        fw = fieldWorkRepository.save(fw);
        String id = fw.getId();
        fw.getItemIds().forEach(i -> {
            final FieldWorkAttendanceSetting fwa = fieldAttendanceSettingRepository.findById(i).orElseThrow(() -> new BusinessException("保存失败!。原因：未查询到野外补助配置项(id=" + i + ")"));
            fieldWorkItemRepository.save(FieldWorkItem.create(fwa, id));
        });
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

    //用户看板数据
    public FieldWorkUserBoard userBoard(String jobNumber, String startDate, String endDate) {

        FieldWorkUserBoard board = new FieldWorkUserBoard();

        List<FieldWorkAttendanceSetting> settings = this.findAllSettings();

        //查询所有记录
        final List<FieldWork> record = fieldWorkRepository.findByJobNumberAndReviewResultAndAttendanceDateBetweenOrderByAttendanceDate(jobNumber, OAConstants.FW_PASS, TimeUtil.toLocalDate(startDate), TimeUtil.toLocalDate(endDate));
        //出勤天数: 不包含《不计算考勤》项目
        List<CalendarEvent> events = new ArrayList<>();
        //出勤
        Set<LocalDate> workDaySet = new HashSet<>();
        Set<LocalDate> resetDaySet = new HashSet<>();
        record.forEach(fw -> {
           fw.getItems().forEach(i -> {
               events.add(CalendarEvent.create(i, fw.getAttendanceDate().atStartOfDay()));
               if (isWorkDay(settings, i)) {
                   workDaySet.add(fw.getAttendanceDate());
               } else if (isRestDay(settings, i)) {
                   resetDaySet.add(fw.getAttendanceDate());
               }
           });
        });
        board.setWorkDay(workDaySet.size());
        board.setRestDay(resetDaySet.size());
        board.setEvents(events);

        return board;
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
