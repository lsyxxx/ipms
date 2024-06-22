package com.abt.salary.service.impl;

import com.abt.common.util.FileUtil;
import com.abt.common.util.TokenUtil;
import com.abt.common.util.ValidateUtil;
import com.abt.salary.SalaryExcelReadListener;
import com.abt.salary.entity.*;
import com.abt.salary.model.SalaryDetail;
import com.abt.salary.model.SalaryPreview;
import com.abt.salary.model.UserSlip;
import com.abt.salary.repository.*;
import com.abt.salary.service.SalaryService;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.dto.UserView;
import com.abt.sys.model.entity.EmployeeInfo;
import com.abt.sys.model.entity.TUser;
import com.abt.sys.repository.EmployeeRepository;
import com.abt.sys.repository.TUserRepository;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.enums.CellExtraTypeEnum;
import com.alibaba.excel.support.ExcelTypeEnum;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static com.abt.salary.Constants.*;

/**
  * 
  */
@Service
@Slf4j
public class SalaryServiceImpl implements SalaryService {

    private final EmployeeRepository employeeRepository;
    private final SalaryMainRepository salaryMainRepository;
    private final SalaryCellRepository salaryCellRepository;
    private final SalarySlipRepository salarySlipRepository;
    private final SalaryEncRepository salaryEncRepository;
    private final TUserRepository tUserRepository;
    private final SalaryHeaderRepository salaryHeaderRepository;

    @Value("${com.abt.file.upload.save}")
    private String fileRoot;

    /**
     * 超时时间:分钟
     */
    @Value("${sl.my.session.timeout}")
    private Integer sessionTimeout;

    /**
     * 自动确认天数
     */
    @Value("${sl.my.autocheck}")
    private Integer defaultAutoCheck;

    public SalaryServiceImpl(EmployeeRepository employeeRepository, SalaryMainRepository salaryMainRepository,
                             SalaryCellRepository salaryCellRepository, SalarySlipRepository salarySlipRepository, SalaryEncRepository salaryEncRepository, TUserRepository tUserRepository, SalaryHeaderRepository salaryHeaderRepository) {
        this.employeeRepository = employeeRepository;
        this.salaryMainRepository = salaryMainRepository;
        this.salaryCellRepository = salaryCellRepository;
        this.salarySlipRepository = salarySlipRepository;
        this.salaryEncRepository = salaryEncRepository;
        this.tUserRepository = tUserRepository;
        this.salaryHeaderRepository = salaryHeaderRepository;
    }

    @Override
    public SalaryMain createSalaryMain(MultipartFile file, String yearMonth, String group, Integer sheetNo) {
        SalaryMain main = new SalaryMain();
        main.setFileName(file.getOriginalFilename());
        main.setYearMonth(yearMonth);
        main.setGroup(group);
        main.setSheetNo(sheetNo);
        return main;
    }

    //抽取excel数据
    @Override
    public SalaryPreview extractAndValidate(InputStream fis, SalaryMain salaryMain) {
        final SalaryExcelReadListener listener = new SalaryExcelReadListener(salaryMain.getId(), salaryMain.getYearMonth());
        EasyExcel.read(fis, listener)
                .excelType(ExcelTypeEnum.XLSX)
                .headRowNumber(SalaryExcelReadListener.DATA_START_IDX)
                //sheetNo从0开始
                .extraRead(CellExtraTypeEnum.MERGE).sheet(salaryMain.getSheetNo()).doRead();
        salaryMain.setNetPaidColumnIndex(listener.getNetPaidColumnIndex());
        salaryMain.setJobNumberColumnIndex(listener.getJobNumberColumnIndex());
        salaryMain.setName(listener.getNameColumnIndex());

        final int jobNumberColumnIndex = listener.getJobNumberColumnIndex();
        final int netPaidColumnIndex = listener.getNetPaidColumnIndex();
        log.info("=== jobNumberColumnIndex {} =======", jobNumberColumnIndex);
        log.info("=== netPaidColumnIndex {} =======", netPaidColumnIndex);

        //组装preview
        SalaryPreview preview = this.buildPreview(listener, salaryMain);
        preview.setSalaryMain(salaryMain);

        //校验
        if (!listener.includeJobNumber()) {
            preview.addFatalError("严重错误:表头中没有\"工号\"，请修改工资表后重新上传");
        }
        if (!listener.includeName()) {
            preview.addFatalError("严重错误:表头中没有\"姓名\"，请修改工资表后重新上传");
        }
        if (!listener.includeNetPaid()) {
            preview.addFatalError("严重错误:表头中没有\"本月实发工资\"，请修改工资表后重新上传");
        }
        //严重错误直接抛出，否则后续可能存在问题
        if (!preview.hasFatalError()) {
            log.info("==== FATAL ERROR! {}",  preview.getFatalError());
            return preview;
        }

        preview.setJobNumberColumnIndex(listener.getJobNumberColumnIndex());
        preview.setNetPaidColumnIndex(listener.getNetPaidColumnIndex());
        preview.setNameColumnIndex(listener.getNameColumnIndex());

        //-- 校验人员信息:目的是保证工资条正确发放
        //0. 所有人员工号是否都存在
        //1. 人员是否在employee表中
        //2. 工号和姓名是否对应（工号作为key，校验姓名）
        //3. 是否有重复人员(工号重复)
        //4. excel中是否存在离职员工
        final List<EmployeeInfo> employees = employeeRepository.findAllWithDept();
        checkJobNumberNull(preview);
        checkUsernameNull(preview);
        checkUserContains(preview, employees);
        checkUserConsistency(preview, employees);
        checkJobNumberDuplicated(preview);
        checkUserEnabled(preview, employees);

        //-- 校验“本月实发工资”列是否存在空数据
        checkNetPaidNull(preview);

        //整合数据
        preview.setSlipTable(this.buildSlipTable(preview.getRawTable()));
        log.info("===== extractAndValidate DONE =======");
        return preview;
    }

    private SalaryPreview buildPreview(SalaryExcelReadListener listener, SalaryMain main) {
        SalaryPreview preview = new SalaryPreview();
        preview.setYearMonth(main.getYearMonth());
        preview.setGroup(main.getGroup());
        preview.setFilePath(main.getFilePath());
        preview.setSalaryMain(main);
        if (listener == null) {
            return preview;
        }
        preview.setRawTable(listener.getTableList());
        preview.setMergedHeader(listener.getMergedHeader());
        preview.setNameColumnIndex(listener.getNameColumnIndex());
        preview.setNetPaidColumnIndex(listener.getNetPaidColumnIndex());
        preview.setJobNumberColumnIndex(listener.getJobNumberColumnIndex());
        preview.buildHeader(listener.getRawHeader(), main.getId());
        return preview;
    }

    private void checkNetPaidNull(SalaryPreview preview) {
        preview.getRawTable().forEach(row -> {
            row.forEach(cell -> {
                if (cell.getColumnIndex() == preview.getNetPaidColumnIndex()) {
                    if (StringUtils.isBlank(cell.getValue())) {
                        setRowErrorFlag(row);
                        preview.addTypedErrorRow(ERR_NETPAID_NULL, row);
                    }
                }
            });
        });
    }

    //离职/未启用工资选项
    public void checkUserEnabled(SalaryPreview preview, List<EmployeeInfo> employees) {
        preview.getRawTable().forEach(row -> {
            SalaryCell jobNumCell = row.get(preview.getJobNumberColumnIndex());
            String jobNumber = jobNumCell.getValue();
            final Optional<EmployeeInfo> emp = employees.stream().filter(i -> i.getJobNumber().equals(jobNumber) && (i.isExit() || i.salaryDisabled())).findAny();
            if (emp.isPresent()) {
                setRowErrorFlag(row);
                preview.addTypedErrorRow(ERR_USER_EXIT, row);
            }

        });
    }

    //工号重复
    public void checkJobNumberDuplicated(SalaryPreview preview) {
        Map<String, List<List<SalaryCell>>> jobNumMap = new HashMap<>();
        for (List<SalaryCell> row : preview.getRawTable()) {
            String jobNumber = row.get(preview.getJobNumberColumnIndex()).getValue();
            List<List<SalaryCell>> list = jobNumMap.getOrDefault(jobNumber, new ArrayList<>());
            list.add(row);
            jobNumMap.put(jobNumber, list);
        }
        jobNumMap.forEach((k, v) -> {
            //重复
            if (v.size() > 1) {
                v.forEach(this::setRowErrorFlag);
                preview.addTypedErrorAll(ERR_JOBNUM_DUPLICATED, v);
            }
        });
    }


    //工号不存在
    public void checkUserContains(SalaryPreview preview, List<EmployeeInfo> employees) {
        preview.getRawTable().forEach(row -> {
             String jobNumber = row.get(0).getJobNumber();
            final boolean exists = employees.stream().anyMatch(i -> i.getJobNumber().equals(jobNumber));
            if (!exists) {
                setRowErrorFlag(row);
                preview.addTypedErrorRow(ERR_JOBNUM_NOT_EXIST, row);
            }
        });
    }

    //工号/姓名/工资组 与employee要一致
    public void checkUserConsistency(SalaryPreview preview, List<EmployeeInfo> employees) {
        for (List<SalaryCell> row : preview.getRawTable()) {
            String jobNumber = row.get(preview.getJobNumberColumnIndex()).getValue();
            String name = row.get(preview.getNameColumnIndex()).getValue();
            final boolean match = employees.stream().anyMatch(i -> i.getJobNumber().equals(jobNumber) && i.getName().equals(name) && i.getCompany().equals(preview.getGroup()));
            if (!match) {
                setRowErrorFlag(row);
                preview.addTypedErrorRow(ERR_USER_NOT_FIT, row);
            }
        }
    }

    public void checkUsernameNull(SalaryPreview preview) {
        preview.getRawTable().forEach(row -> {
            row.forEach(cell -> {
                if (cell.getColumnIndex() == preview.getNameColumnIndex()) {
                    if (StringUtils.isBlank(cell.getValue())) {
                        setRowErrorFlag(row);
                        preview.addTypedErrorRow(ERR_USERNAME_NULL, row);
                    }
                }
            });
        });
    }

    /**
     * 校验工号是否存在空数据
     */
    public void checkJobNumberNull(SalaryPreview preview) {
        preview.getRawTable().forEach(row -> {
            SalaryCell cell = row.get(0);
            if (StringUtils.isBlank(cell.getJobNumber())) {
                setRowErrorFlag(row);
                preview.addTypedErrorRow(ERR_JOBNUM_NULL, row);
            }
        });
    }

    private SalaryCell setRowErrorFlag(List<SalaryCell> row) {
        final SalaryCell rowInfoCell = getRowInfoCell(row);
        rowInfoCell.doError();
        return rowInfoCell;
    }

    private SalaryCell getRowInfoCell(List<SalaryCell> row) {
        return row.get(SL_ROW_INFO_IDX);
    }

    /**
     * 生成可以发工资条的table
     * @param rawTable 原始数据
     */
    private List<List<SalaryCell>> buildSlipTable(List<List<SalaryCell>> rawTable) {
        List<List<SalaryCell>> slipTable = new ArrayList<>();
        rawTable.forEach(row -> {
            List<SalaryCell> newRow = new ArrayList<>(row);
            SalaryCell infoCell = getRowInfoCell(newRow);
            if (!infoCell.isRowError()) {
                newRow.remove(SL_ROW_INFO_IDX);
                slipTable.add(newRow);
            }
        });
        return slipTable;
    }

    private String createFilePath(String yearMonth) {
        return fileRoot + File.separator + SERVICE + File.separator + yearMonth + File.separator;
    }


    @Override
    public void saveExcelFile(MultipartFile file, SalaryMain main) {
        String filePath = createFilePath(main.getYearMonth());
        final String newName = FileUtil.saveFile(file, filePath, true);
        String fullPath = filePath + newName;
        main.setFilePath(fullPath);
        main.setFileName(file.getOriginalFilename());
    }

    //导入数据库和发送工资条到个人
    @Transactional
    @Override
    public void salaryImport(SalaryMain main, SalaryPreview preview) {
        //保存
        salaryMainRepository.save(main);

        //生成工资条
        for (List<SalaryCell> row : preview.getSlipTable()) {
            final String jobNumber = row.get(0).getJobNumber();
            final String name = row.get(0).getName();
            SalarySlip slip = SalarySlip.create(main, jobNumber);
            slip.setName(name);
            slip.setYearMonth(main.getYearMonth());
            Integer idx = main.getNetPaidColumnIndex() - 1;
            String netPaid = row.get(idx).getValue();
            if (netPaid != null) {
                slip.setNetPaid(new BigDecimal(netPaid));
            }
            //自动确认时间
            slip.send();
            this.getSlipAutoCheckTime(slip);
            slip = salarySlipRepository.save(slip);
            for (SalaryCell cell : row) {
                cell.setSlipId(slip.getId());
            }
            salaryCellRepository.saveAll(row);

        }

    }

    //根据 工资年月/工资组查看导入/发送工资条记录
    @Override
    public List<SalaryMain> findImportRecordBy(String yearMonth) {
        final List<SalaryMain> list = salaryMainRepository.findByYearMonthNullable(yearMonth);
        //统计数据
        list.forEach(m -> {
            final int sendCount = salarySlipRepository.countByIsSendAndMainId(true, m.getId());
            m.setSendCount(sendCount);
            final int readCount = salarySlipRepository.countByIsReadAndMainId(true, m.getId());
            m.setReadCount(readCount);
            final int checkCount = salarySlipRepository.countByIsCheckAndMainId(true, m.getId());
            m.setCheckCount(checkCount);
            final int feedBackCount = salarySlipRepository.countByIsFeedBackAndMainId(true, m.getId());
            m.setFeedBackCount(feedBackCount);
        });
        return list;

    }

    @Override
    public SalaryMain findSalaryMainById(String id) {
        return salaryMainRepository.findById(id).orElseThrow(() -> new BusinessException("未查询到上传工资表(id=" + id + ")"));
    }

    //根据salaryMain.id查询发送情况
    @Override
    public List<SalarySlip> findSendSlips(String mid) {
        final List<SalarySlip> list = salarySlipRepository.findEntireDataByMainIdOrderByJobNumber(mid);
        list.forEach(i -> {
            i.user();
            i.erase();
        });
        return list;
    }

    @Transactional
    @Override
    public void deleteSalaryRecord(String mid) {
        try {
            salaryCellRepository.deleteAllByMid(mid);
            salarySlipRepository.deleteAllByMainId(mid);
            salaryHeaderRepository.deleteByMid(mid);
            salaryMainRepository.deleteById(mid);
        } catch (Exception e) {
            log.error("删除工资条记录失败!", e);
            throw new BusinessException("删除工资条记录失败!");
        }
    }

    @Override
    public List<String> getSalaryGroup() {
        return employeeRepository.findDistinctGroup();
    }

    @Override
    public List<UserSlip> findUserSlipListByCurrentYear(String jobNumber) {
        ValidateUtil.ensurePropertyNotnull(jobNumber, "工号(jobNumber)");
        final int year = LocalDate.now().getYear();
        final List<SalarySlip> list = salarySlipRepository.findByJobNumberAndYearMonthContaining(jobNumber, String.valueOf(year));
        return list.stream().map(UserSlip::createBy).toList();
    }


    @Override
    public void verifySessionTimeout(HttpSession session) {
        if (session == null) {
            log.info("session超时，session is null!");
            throw new BusinessException("会话超时，请刷新后重新进入");
        }
        final LocalDateTime time = (LocalDateTime) session.getAttribute(S_SL_MY);
        if (time == null) {
            log.info("session超时，last time is null!");
            throw new BusinessException("会话超时，请刷新后重新进入");
        }
        LocalDateTime now = LocalDateTime.now();
        long dur = Duration.between(time, now).toMinutes();
        if (dur > this.sessionTimeout) {
            log.info("session 超时, 上次进入时间: {}", time);
            throw new BusinessException("会话超时，请刷新后重新进入");
        }
    }


    //根据slip id查询一条工资条详情
    @Override
    public List<SalaryCell> getSalaryDetail(String slipId, String mainId) {
        final SalaryMain main = this.findSalaryMainById(mainId);
        List<SalaryCell> list = salaryCellRepository.findBySlipIdOrderByColumnIndex(slipId);
        final List<SalaryHeader> header = salaryHeaderRepository.findByMid(mainId);
        //TODO: 组装
        List<SalaryDetail> details = new ArrayList<>();


        if (!main.isShowEmptyColumn()) {
            list = formatSalaryDetails(list);
        }
        return list;
    }

    /**
     * 格式化工资详情，去掉空数据，或者值为0的
     * @param list 工资数据
     */
    private List<SalaryCell> formatSalaryDetails(List<SalaryCell> list) {
        List<SalaryCell> newList = new ArrayList<>();
        list.forEach(cell -> {
            final String value = cell.getValue();
            final String label = cell.getLabel();
            if (!"序号".equals(label)) {
                if (StringUtils.isNotBlank(value)) {
                    try {
                        BigDecimal b = new BigDecimal(value);
                        //是数字不是0
                        if (b.compareTo(BigDecimal.ZERO) != 0) {
                            newList.add(cell);
                        }
                    } catch (Exception e) {
                        log.debug("不是数字不能转换 - " + value);
                        newList.add(cell);
                    }
                }
            }

        });
        return newList;
    }

    //重置为初始状态，重置密码
    @Override
    public void resetFirst() {
        final UserView userFromAuthToken = TokenUtil.getUserFromAuthToken();
        final String jobNumber = userFromAuthToken.getEmpnum();
        final SalaryEnc enc = findAndCreateSalaryEnc(jobNumber);
        enc.reset(encrypt(DEFAULT_PWD));
    }

    @Override
    public boolean verifyFirst(String jobNumber) {
        SalaryEnc enc = findAndCreateSalaryEnc(jobNumber);
        return enc.isFirst();
    }

    /**
     * 创建工资密码
     */
    private SalaryEnc createSalaryEnc(String jobNumber) {
        ensureUserExists(jobNumber);
        return salaryEncRepository.save(SalaryEnc.create(jobNumber));
    }

    private void ensureUserExists(String jobNumber) {
        final EmployeeInfo emp = employeeRepository.findByJobNumber(jobNumber);
        if (emp == null) {
            throw new BusinessException("《员工信息》中不存在该用户(工号: " + jobNumber + ")，请添加!");
        }
        final TUser user = tUserRepository.findByEmpnum(jobNumber);
        if (user == null) {
            throw new BusinessException("员工不存在(工号:" + jobNumber + ")!");
        }
    }

    /**
     * 查找enc，没有查到就创建
     * @param jobNumber 工号
     */
    private SalaryEnc findAndCreateSalaryEnc(String jobNumber) {
        ValidateUtil.ensurePropertyNotnull(jobNumber, "工号(jobNumber)");
        SalaryEnc enc = salaryEncRepository.findByJobNumber(jobNumber);
        if (enc == null) {
            enc = createSalaryEnc(jobNumber);
        }
        return enc;
    }

    @Override
    public void updateEnc(String pwd, String jobNumber) {
        final SalaryEnc enc = findAndCreateSalaryEnc(jobNumber);
        updateAndPersistEnc(enc, pwd);
    }

    private void updateAndPersistEnc(SalaryEnc enc, String newPwd) {
        encRule(newPwd);
        enc.setPwd(encrypt(newPwd));
        enc.setFirst(false);
        salaryEncRepository.save(enc);
    }


    @Override
    public void verifyPwd(String pwd, String jobNumber) {
        final SalaryEnc enc = findAndCreateSalaryEnc(jobNumber);
        if (!encrypt(pwd).equals(enc.getPwd())) {
            throw new BusinessException("密码错误！");
        }
    }

    @Override
    public void verifyConfirmedPwd(String pwd1, String pwd2) {
        if (!pwd1.equals(pwd2)) {
            throw new BusinessException("两次输入密码不一致");
        }
    }


    private String encrypt(String pwd) {
       return DigestUtils.md5Hex(pwd);
    }

    private void encRule(String pwd) {
        //1. 密码位数
        if (StringUtils.isNotBlank(pwd)) {
            if (pwd.length() != PWD_LEN) {
                throw new BusinessException("密码长度为8位");
            }
        } else {
            throw new BusinessException("请输入密码");
        }
    }

    public Integer getSessionTimeout() {
        if (this.sessionTimeout == null) {
            return 5;
        }
        return this.sessionTimeout;
    }

    //根据年月查询
    @Override
    public List<UserSlip> findUserSalarySlipByYearMonth(String jobNumber, String yearMonth) {
        final List<SalarySlip> list = salarySlipRepository.findByJobNumberAndYearMonth(jobNumber, yearMonth);
        return list.stream().map(UserSlip::createBy).toList();
    }


    @Override
    public void readSalarySlip(String slipId) {
        final SalarySlip salarySlip = findSalarySlipEntityById(slipId);
        //只记录第一次的
        if (!salarySlip.isRead()) {
            salarySlip.read();
            salarySlipRepository.save(salarySlip);
        }
    }

    //工资条确认
    @Override
    public void checkSalarySlip(String slipId) {
        final SalarySlip salarySlip = findSalarySlipEntityById(slipId);
        salarySlip.check();
        salarySlipRepository.save(salarySlip);
    }

    private SalarySlip findSalarySlipEntityById(String id) {
       return salarySlipRepository.findById(id).orElseThrow(() -> new BusinessException("未查询到工资条(id=" + id + ")"));
    }

    //自动确认
    @Override
    public void slipAutoCheck(String slipId) {
        final SalarySlip slip = findSalarySlipEntityById(slipId);
//        getSlipAutoCheckTime(slip);
        salarySlipRepository.save(slip);
    }

    @Override
    public List<SalarySlip> findSalarySlipUnchecked() {
       return salarySlipRepository.findAllUnchecked();
    }

    @Override
    public void getSlipAutoCheckTime(SalarySlip slip) {
        final LocalDateTime sendTime = slip.getSendTime();
        final LocalDateTime autoCheckTime = sendTime.plusDays(getDefaultAutoCheck());
        slip.setAutoCheckTime(autoCheckTime);
    }

    @Override
    public void saveAllSlip(List<SalarySlip> list) {
        salarySlipRepository.saveAllAndFlush(list);
    }


    public Integer getDefaultAutoCheck() {
        if (this.defaultAutoCheck == null) {
            this.defaultAutoCheck = 3;
        }
        return this.defaultAutoCheck;
    }

}
