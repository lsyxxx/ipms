package com.abt.salary.service.impl;

import com.abt.common.model.R;
import com.abt.common.util.FileUtil;
import com.abt.common.util.ValidateUtil;
import com.abt.oa.entity.OrgLeader;
import com.abt.oa.service.OrgLeaderService;
import com.abt.salary.SalaryExcelReadListener;
import com.abt.salary.entity.*;
import com.abt.salary.model.*;
import com.abt.salary.repository.*;
import com.abt.salary.service.SalaryService;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.entity.CustomerInfo;
import com.abt.sys.model.entity.EmployeeInfo;
import com.abt.sys.model.entity.SystemMessage;
import com.abt.sys.model.entity.TUser;
import com.abt.sys.repository.EmployeeRepository;
import com.abt.sys.repository.TUserRepository;
import cn.idev.excel.EasyExcel;
import cn.idev.excel.enums.CellExtraTypeEnum;
import cn.idev.excel.support.ExcelTypeEnum;
import com.abt.sys.service.SystemMessageService;
import com.abt.wf.entity.UserSignature;
import com.abt.wf.repository.UserSignatureRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.abt.oa.OAConstants.*;
import static com.abt.salary.Constants.*;
import static com.abt.salary.entity.SalarySlip.LABEL_NAME;
import static com.abt.salary.entity.SalarySlip.LABEL_USER_CHECK;
import static com.abt.salary.model.CheckAuth.*;
import static java.util.stream.Collectors.toList;

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
    private final CustomerInfo abtCompany;
    private final CustomerInfo grdCompany;
    private final CustomerInfo dcCompany;
    private final OrgLeaderService orgLeaderService;
    private final UserSignatureRepository userSignatureRepository;
    private final SystemMessageService systemMessageService;

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


    /**
     * 签名地址
     */
    @Value("${abt.sig.dir}")
    private String sigDir;

    /**
     * 生成导出文件的地址
     */
    @Value("${abt.sl.check}")
    private String checkExcelDir;

    @Value("${abt.sl.slip.url}")
    private String userSlipUrl;

    @Value("${abt.sl.chk.dept.url}")
    private String deptCheckUrl;

    @Value("${abt.sl.chk.smry.url}")
    private String summaryCheckUr;



    public SalaryServiceImpl(EmployeeRepository employeeRepository, SalaryMainRepository salaryMainRepository,
                             SalaryCellRepository salaryCellRepository, SalarySlipRepository salarySlipRepository, SalaryEncRepository salaryEncRepository, TUserRepository tUserRepository, SalaryHeaderRepository salaryHeaderRepository, CustomerInfo abtCompany, CustomerInfo grdCompany, CustomerInfo dcCompany, OrgLeaderService orgLeaderService, UserSignatureRepository userSignatureRepository, SystemMessageService systemMessageService) {
        this.employeeRepository = employeeRepository;
        this.salaryMainRepository = salaryMainRepository;
        this.salaryCellRepository = salaryCellRepository;
        this.salarySlipRepository = salarySlipRepository;
        this.salaryEncRepository = salaryEncRepository;
        this.tUserRepository = tUserRepository;
        this.salaryHeaderRepository = salaryHeaderRepository;
        this.abtCompany = abtCompany;
        this.grdCompany = grdCompany;
        this.dcCompany = dcCompany;
        this.orgLeaderService = orgLeaderService;
        this.userSignatureRepository = userSignatureRepository;
        this.systemMessageService = systemMessageService;
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
        salaryMain.setNetPaidColumnIndex(listener.getNetPaidRawColumnIndex());
        salaryMain.setJobNumberColumnIndex(listener.getJobNumberRawColumnIndex());
        salaryMain.setName(listener.getNameRawColumnIndex());
        salaryMain.setEmpCostColumnIndex(listener.getEmpCostColumnIndex());

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
        if (!listener.includeEmpCost()) {
            preview.addFatalError("严重错误:表头中没有\"用工成本\"，请修改工资表后重新上传");
        }
        //严重错误直接抛出，否则后续可能存在问题
        if (!preview.hasFatalError()) {
            log.error("==== FATAL ERROR! {}",  preview.getFatalError());
            return preview;
        }

        preview.setJobNumberColumnIndex(listener.getJobNumberRawColumnIndex());
        preview.setNetPaidColumnIndex(listener.getNetPaidRawColumnIndex());
        preview.setNameColumnIndex(listener.getNameRawColumnIndex());
        preview.setEmpCostColumnIndex(listener.getEmpCostColumnIndex());
        //合计数据
        salaryMain.setSumEmp(listener.getSumEmp());
        salaryMain.setSumCost(listener.getSumCost().setScale(2, RoundingMode.FLOOR).doubleValue());
        salaryMain.setSumNetPaid(listener.getSumNetPaid().setScale(2, RoundingMode.FLOOR).doubleValue());

        preview.setSumEmp(listener.getSumEmp());
        preview.setSumCost(listener.getSumCost().setScale(2, RoundingMode.FLOOR).doubleValue());
        preview.setSumNetPaid(listener.getSumNetPaid().setScale(2, RoundingMode.FLOOR).doubleValue());

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
        preview.setNameColumnIndex(listener.getNameRawColumnIndex());
        preview.setNetPaidColumnIndex(listener.getNetPaidRawColumnIndex());
        preview.setJobNumberColumnIndex(listener.getJobNumberRawColumnIndex());
        preview.setSumEmp(listener.getSumEmp());
        preview.setSumCost(listener.getSumCost().setScale(2, RoundingMode.FLOOR).doubleValue());
        preview.setSumNetPaid(listener.getSumNetPaid().setScale(2, RoundingMode.FLOOR).doubleValue());
        preview.buildHeader(listener.getRawHeader(), main.getId());
        return preview;
    }

    private void checkNetPaidNull(SalaryPreview preview) {
        preview.getRawTable().forEach(row -> row.forEach(cell -> {
            if (cell.getColumnIndex() == preview.getNetPaidColumnIndex()) {
                if (StringUtils.isBlank(cell.getValue())) {
                    setRowErrorFlag(row);
                    cell.addError(ERR_NETPAID_NULL);
                    preview.addTypedErrorRow(ERR_NETPAID_NULL, row);
                }
            }
        }));
    }

    //离职/未启用工资选项
    public void checkUserEnabled(SalaryPreview preview, List<EmployeeInfo> employees) {
        preview.getRawTable().forEach(row -> {
            SalaryCell jobNumCell = row.get(preview.getJobNumberColumnIndex());
            String jobNumber = jobNumCell.getValue();
            final Optional<EmployeeInfo> emp = employees.stream().filter(i -> i.getJobNumber().equals(jobNumber) && (i.isExit() || i.salaryDisabled())).findAny();
            if (emp.isPresent()) {
                setRowErrorFlag(row);
                getRowInfoCell(row).addError(ERR_USER_EXIT);
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
                v.forEach(i -> {
                    setRowErrorFlag(i);
                    getRowInfoCell(i).addError(ERR_JOBNUM_DUPLICATED);
                });
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
                getRowInfoCell(row).addError(ERR_JOBNUM_NOT_EXIST);
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
                getRowInfoCell(row).addError(ERR_USER_NOT_FIT);
                preview.addTypedErrorRow(ERR_USER_NOT_FIT, row);
            }
        }
    }

    public void checkUsernameNull(SalaryPreview preview) {
        preview.getRawTable().forEach(row -> row.forEach(cell -> {
            if (cell.getColumnIndex() == preview.getNameColumnIndex()) {
                if (StringUtils.isBlank(cell.getValue())) {
                    setRowErrorFlag(row);
                    cell.addError(ERR_USERNAME_NULL);
                    preview.addTypedErrorRow(ERR_USERNAME_NULL, row);
                }
            }
        }));
    }

    /**
     * 校验工号是否存在空数据
     */
    public void checkJobNumberNull(SalaryPreview preview) {
        preview.getRawTable().forEach(row -> {
            SalaryCell cell = row.get(0);
            if (StringUtils.isBlank(cell.getJobNumber())) {
                setRowErrorFlag(row);
                cell.addError(ERR_JOBNUM_NULL);
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
     * 生成可以发工资条的table，所有上传的数据都形成工资条，但是有问题的不发送
     * @param rawTable 原始数据
     */
    private List<List<SalaryCell>> buildSlipTable(List<List<SalaryCell>> rawTable) {
        List<List<SalaryCell>> slipTable = new ArrayList<>();
        rawTable.forEach(row -> {
            List<SalaryCell> newRow = new ArrayList<>(row);
//            SalaryCell infoCell = getRowInfoCell(newRow);
//            if (!infoCell.isRowError()) {
//
//            }
            slipTable.add(newRow);
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

    /**
     * 生成各级审批用户及对应的审批部门
     */
    private List<OrgLeader> findSalaryLeaders() {
        return orgLeaderService.findAll();
    }

    /**
     * 将各级审批人填入slip中
     */
    public void setSalarySlipCheckLeaders(SalaryMain main, SalarySlip slip, List<OrgLeader> leaders) {
        if (slip == null) {
            return;
        }
        final String deptExcel = slip.getDeptExcel();
        final Map<String, OrgLeader> map = leaders.stream()
                .filter(i -> StringUtils.isBlank(i.getDeptId()) || i.getDeptName().equals(deptExcel))
                .collect(Collectors.toMap(OrgLeader::getRole, i -> i));
        if (map.get(SL_CHK_DM) != null && main.isDeptManagerCheck()) {
            slip.setDmJobNumber(map.get(SL_CHK_DM).getJobNumber());
            slip.setDmName(map.get(SL_CHK_DM).getName());
        }
        if (map.get(SL_CHK_DCEO) != null) {
            slip.setDceoJobNumber(map.get(SL_CHK_DCEO).getJobNumber());
            slip.setDceoName(map.get(SL_CHK_DCEO).getName());
        }
        if (map.get(SL_CHK_CEO) != null) {
            slip.setCeoJobNumber(map.get(SL_CHK_CEO).getJobNumber());
            slip.setCeoName(map.get(SL_CHK_CEO).getName());
        }
        if (map.get(SL_CHK_HR) != null) {
            slip.setHrJobNumber(map.get(SL_CHK_HR).getJobNumber());
            slip.setHrName(map.get(SL_CHK_HR).getName());
        }
    }

    public void findSalaryMainByYearMonth(String yearMonth) {
        //验证yearMonth

    }


    @Override
    public List<DeptSummary> getDeptSummaryTableByMainId(String mid) {
        List<DeptSummary> list = new ArrayList<>();
        final List<SalarySlip> slips = salarySlipRepository.findByMainId(mid);
        //deptExcel可能为空
        final Map<String, List<SalarySlip>> map = slips.stream().collect(Collectors.groupingBy(SalarySlip::getDeptExcel, toList()));
        for (Map.Entry<String, List<SalarySlip>> entry : map.entrySet()) {
            final String deptExcel = entry.getKey();
            final List<SalarySlip> slipList = entry.getValue();
            DeptSummary ds = new DeptSummary();
            ds.setDeptName(deptExcel);
            ds.setSumEmp(slipList.size());
            final BigDecimal sumCost = slipList.stream().map(SalarySlip::getEmpCost).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
            ds.setSumCost(sumCost);
            final BigDecimal sumNetPaid = slipList.stream().map(SalarySlip::getNetPaid).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
            ds.setSumNetPaid(sumNetPaid);
            ds.setIndex(slipList.stream().map(SalarySlip::getIndexExcel).filter(Objects::nonNull).min(Integer::compareTo).orElse(0));
            list.add(ds);
        }
        list.sort(Comparator.comparing(DeptSummary::getIndex));
        return list;
    }


    //导入数据库和发送工资条到个人
    @Transactional
    @Override
    public void salaryImport(SalaryMain main, SalaryPreview preview) {
        //保存
        LocalDateTime sendTime = LocalDateTime.now();
        final LocalDateTime autoCheckTime = getAutoCheckTime(sendTime);
        main.setAutoCheckTime(autoCheckTime);
        main = salaryMainRepository.save(main);
        salaryHeaderRepository.saveAll(preview.getHeader());
        final List<OrgLeader> orgLeaders = findSalaryLeaders();
        Set<String> chks = new HashSet<>();
        Set<String> users = new HashSet<>();
        Set<String> smryChks = new HashSet<>();
        //生成工资条
        List<SalarySlip> slips = new ArrayList<>();
        for (List<SalaryCell> row : preview.getSlipTable()) {
            final SalarySlip slip = SalarySlip.create(main, row);
            if (StringUtils.isBlank(slip.getName())) {
                continue;
            }
            setSalarySlipCheckLeaders(main, slip, orgLeaders);

            Integer idx = main.getNetPaidColumnIndex();
            String netPaid = row.get(idx).getValue();
            if (netPaid != null) {
                slip.setNetPaid(new BigDecimal(netPaid));
            }
            idx = main.getEmpCostColumnIndex();
            if (idx != null) {
                String empCost = row.get(idx).getValue();
                if (empCost != null) {
                    slip.setEmpCost(new BigDecimal(empCost));
                }
            }
            //error
            final SalaryCell rowInfoCell = getRowInfoCell(row);
            slip.setError(rowInfoCell.getError());

            //自动确认时间
            if (!slip.isError()) {
                slip.send(sendTime);
                slip.setAutoCheckTime(autoCheckTime);
            }

            final SalarySlip saved = salarySlipRepository.save(slip);
            slips.add(saved);
            //个人消息
            users.add(slip.getJobNumber());
            //审批
            if (StringUtils.isNotBlank(slip.getDceoJobNumber())) {
                chks.add(slip.getDceoJobNumber());
            }
            if (StringUtils.isNotBlank(slip.getDmJobNumber())) {
                chks.add(slip.getDmJobNumber());
            }
            if (StringUtils.isNotBlank(slip.getCeoJobNumber())) {
                smryChks.add(slip.getCeoJobNumber());
            }
            if (StringUtils.isNotBlank(slip.getHrJobNumber())) {
                smryChks.add(slip.getHrJobNumber());
            }
            for (SalaryCell cell : row) {
                cell.setSlipId(saved.getId());
            }
            //防止空单元格
            row.removeIf(cell -> StringUtils.isBlank(cell.getParentLabel()) && StringUtils.isBlank(cell.getLabel()) && StringUtils.isBlank(cell.getValue()));
            salaryCellRepository.saveAllAndFlush(row);
        }

        try {
            //发送消息，失败不影响其他的
            final List<SystemMessage> umsgs = createMsgs(users, userSlipUrl, "您的工资条已发放，请及时查看并确认");
            //部门审核
            final List<SystemMessage> cmsgs = createMsgs(chks, deptCheckUrl, "本月工资表已生成，请审核");
            //汇总审核
            final List<SystemMessage> smrys = createMsgs(smryChks, summaryCheckUr, "本月工资表已生成，请审核");
            cmsgs.addAll(umsgs);
            cmsgs.addAll(smrys);
            systemMessageService.sendAll(cmsgs);
        } catch (Exception e) {
            log.error("发送消息失败" + e.getMessage(), e);
        }

        preview.setSlips(slips);

    }

    private List<SystemMessage> createMsgs(Set<String> jnos, String href, String content) {
        final List<TUser> users = tUserRepository.findAllByEmpnumIn(jnos);
        Map<String, TUser> map = users.stream().collect(Collectors.toMap(TUser::getEmpnum, u -> u));
        List<SystemMessage> systemMessages = new ArrayList<>();
        for(String jno : jnos) {
            TUser u = map.get(jno);
            if (u != null) {
                final SystemMessage msg = systemMessageService.createSystemMessage(u.getId(), u.getName(), href, content, "salaryCheck");
                systemMessages.add(msg);
            }
        }
        return systemMessages;
    }


    private void salaryMainCount(SalaryMain main) {
        final List<SalarySlip> slips = salarySlipRepository.findByMainId(main.getId());
        main.setAllCount(slips.size());
        main.setSendCount((int) slips.stream().filter(SalarySlip::isSend).count());
        main.setCheckCount((int) slips.stream().filter(SalarySlip::isCheck).count());
        main.setReadCount((int) slips.stream().filter(SalarySlip::isRead).count());
        main.setDmAllCount((int) slips.stream().filter(SalarySlip::needDmCheck).count());
        main.setDmCheckCount((int) slips.stream().filter(SalarySlip::isDmCheck).count());
        main.setDceoAllCount((int) slips.stream().filter(SalarySlip::needDceoCheck).count());
        main.setDceoCheckCount((int) slips.stream().filter(SalarySlip::isDceoCheck).count());
        main.setHrAllCount((int) slips.stream().filter(SalarySlip::needHrCheck).count());
        main.setHrCheckCount((int) slips.stream().filter(SalarySlip::isHrCheck).count());
        main.setCeoAllCount((int) slips.stream().filter(SalarySlip::needCeoCheck).count());
        main.setCeoCheckCount((int) slips.stream().filter(SalarySlip::isCeoCheck).count());
    }

    //根据 工资年月/工资组查看导入/发送工资条记录
    @Override
    public List<SalaryMain> findImportRecordBy(String yearMonth) {
        final List<SalaryMain> list = salaryMainRepository.findByYearMonthNullable(yearMonth);
        //统计数据
        list.forEach(this::salaryMainCount);
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
        list.forEach(SalarySlip::user);
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

    /**
     * 删除不显示的项目
     * 1. 是否确认列
     */
    private void removeUndislayItems(List<SalaryCell> cells) {
        cells.removeIf(c -> LABEL_USER_CHECK.equals(c.getLabel()));
    }

    //根据slip id查询一条工资条详情
    @Override
    public List<UserSalaryDetail> getSalaryDetail(String slipId, String mainId) {
        final SalaryMain main = this.findSalaryMainById(mainId);
        List<SalaryCell> list = salaryCellRepository.findBySlipIdAndValueIsNotNullOrderByColumnIndexAsc(slipId);
        removeUndislayItems(list);
        if (!main.isShowEmptyColumn()) {
            list = formatSalaryDetails(list);
        }
        //组装成UserSalaryDetail
        List<UserSalaryDetail> userSalaryDetails = new ArrayList<>();
        final Map<String, List<SalaryCell>> map = list.stream().collect(Collectors.groupingBy(item -> item.getParentLabel() == null ? "" : item.getParentLabel(), toList()));
        map.forEach((k, v) -> {
            UserSalaryDetail us = new UserSalaryDetail();
            us.setLabel(k);
            if (v != null && !v.isEmpty()) {
                us.setChildren(new ArrayList<>());
                v.forEach(cell -> {
                    UserSalaryDetail child = new UserSalaryDetail();
                    child.setValue(cell.getValue());
                    child.setLabel(cell.getLabel());
                    child.setColumnIndex(cell.getColumnIndex());
                    child.setCellId(cell.getId());
                    //给父节点赋值，方便排序，是哪个cell col index不重要。
                    us.setColumnIndex(cell.getColumnIndex());
                    us.addChild(child);
                });
                //对child排序
                us.getChildren().sort(Comparator.comparingInt(UserSalaryDetail::getColumnIndex));
            }
            userSalaryDetails.add(us);
        });
        userSalaryDetails.sort(Comparator.comparing(UserSalaryDetail::getColumnIndex));
        return userSalaryDetails;
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
                        log.debug("不是数字不能转换 - {}", value);
                        newList.add(cell);
                    }
                }
            }

        });
        return newList;
    }

    //重置为初始状态，重置密码
    @Override
    public void resetFirst(String jobNumber) {
        final SalaryEnc enc = findAndCreateSalaryEnc(jobNumber);
        enc.resetFirst();
        salaryEncRepository.save(enc);
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

    @Override
    public void updateEncNotFirst(PwdForm form, String jobNumber) {
        final SalaryEnc enc = this.findAndCreateSalaryEnc(jobNumber);
        verifyLastPwd(form.getPwd1(), enc);
        verifyPwd(form.getOldPwd(), jobNumber);
        verifyConfirmedPwd(form.getPwd1(), form.getPwd2());
        updateAndPersistEnc(enc, form.getPwd1());
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

    public void verifyLastPwd(String newPwd, SalaryEnc enc) {
        final String encrypt = this.encrypt(newPwd);
        if (enc.getPwd().equals(encrypt)) {
            throw new BusinessException("新旧密码不能相同！");
        }
    }

    @Override
    public void verifyPwd(String pwd, String jobNumber) {
        final SalaryEnc enc = this.findAndCreateSalaryEnc(jobNumber);
        doVerifyPwd(pwd, enc);
    }

    public void doVerifyPwd(String pwd, SalaryEnc enc) {
        if (!encrypt(pwd).equals(enc.getPwd())) {
            throw new BusinessException("密码错误！");
        }
    }

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
    public void checkSalarySlip(String slipId, String checkType) {
        final SalarySlip salarySlip = findSalarySlipEntityById(slipId);
        salarySlip.check(checkType);
        salarySlipRepository.save(salarySlip);
    }

    private SalarySlip findSalarySlipEntityById(String id) {
       return salarySlipRepository.findById(id).orElseThrow(() -> new BusinessException("未查询到工资条(id=" + id + ")"));
    }

    //自动确认
    @Override
    public void slipAutoCheck(String slipId) {
        final SalarySlip slip = findSalarySlipEntityById(slipId);
        getSlipAutoCheckTime(slip);
        slip.check(SalarySlip.CHECK_TYPE_AUTO);
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

    public LocalDateTime getAutoCheckTime(LocalDateTime sendTime) {
        if (sendTime == null) {
            return null;
        }
        return sendTime.plusDays(getDefaultAutoCheck());
    }

    @Override
    public void saveAllSlip(List<SalarySlip> list) {
        salarySlipRepository.saveAllAndFlush(list);
    }


    public Integer getDefaultAutoCheck() {
        if (this.defaultAutoCheck == null) {
            this.defaultAutoCheck = 2;
        }
        return this.defaultAutoCheck;
    }

    @Override
    public SalaryPreview getSalaryCheckTable(CheckAuth checkAuth, SalaryMain main) {
        String mid = main.getId();
        SalaryPreview preview = new SalaryPreview();
        preview.setSalaryMain(main);
        //header
        final List<SalaryHeader> header = salaryHeaderRepository.findByMidOrderByStartRowAscStartColumnAsc(mid);
        //待审核的slip
        final List<SalarySlip> checkSlip = findSalarySlipsByCheckAuth(checkAuth, mid);
        //需要的sig jobNubmer
        Set<String> allJobNumbers = checkSlip.stream()
                .flatMap(slip -> Stream.of(
                        slip.getDceoJobNumber(),
                        slip.getDmJobNumber(),
                        slip.getJobNumber(),
                        slip.getCeoJobNumber(),
                        slip.getHrJobNumber()
                ))
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());
        final Map<String, String> sigMap = userSignatureRepository.findByJobNumberIn(allJobNumbers)
                .stream().collect(Collectors.toMap(UserSignature::getJobNumber, UserSignature::getBase64));
        for (SalarySlip slip : checkSlip) {
            //签名
            if (slip.isDmCheck()) {
                slip.setDmSig(sigMap.getOrDefault(slip.getDmJobNumber(), ""));
            }
            if (slip.isCheck()) {
                slip.setUserSig(sigMap.getOrDefault(slip.getJobNumber(), null));
            }
            if (slip.isDceoCheck()) {
                slip.setDceoSig(sigMap.getOrDefault(slip.getDceoJobNumber(), null));
            }
            if (slip.isCeoCheck()) {
                slip.setCeoSig(sigMap.getOrDefault(slip.getCeoJobNumber(), null));
            }
            if (slip.isHrCheck()) {
                slip.setHrSig(sigMap.getOrDefault(slip.getHrJobNumber(), null));
            }
        }

        final List<String> sids = checkSlip.stream().map(SalarySlip::getId).toList();
        final List<SalaryCell> slCells = salaryCellRepository.findBySidList(sids);
        //根据rowIndex转为行
        final Map<Integer, List<SalaryCell>> groupByRow = convertRows(slCells);
        final ArrayList<List<SalaryCell>> values = new ArrayList<>(groupByRow.values());
        preview.setHeader(this.buildMultiHeader(header));
        preview.setSlipTable(values);
        preview.setSlips(checkSlip);
        return preview;
    }

    private Map<Integer, List<SalaryCell>> convertRows(List<SalaryCell> slCells) {
        return slCells.stream().collect(
                Collectors.groupingBy(
                        SalaryCell::getRowIndex, LinkedHashMap::new, toList()));
    }

    private List<SalarySlip> findSalarySlipsByCheckAuth(CheckAuth checkAuth, String mid) {
        List<SalarySlip> slips = new ArrayList<>();
        if (SL_CHK_DCEO.equals(checkAuth.getViewAuth())) {
            slips = salarySlipRepository.findByMainIdAndDceoJobNumber(mid, checkAuth.getJobNumber(), Sort.by(Sort.Order.asc("jobNumber")));
        } else if (SL_CHK_DM.equals(checkAuth.getViewAuth())) {
            slips = salarySlipRepository.findByMainIdAndDmJobNumber(mid, checkAuth.getJobNumber(), Sort.by(Sort.Order.asc("jobNumber")));
        } else if (SL_CHK_HR.equals(checkAuth.getViewAuth()) || SL_CHK_CEO.equals(checkAuth.getRole())) {
            slips = salarySlipRepository.findByMainId(mid);
        } else if (SL_CHK_USER.equals(checkAuth.getViewAuth())) {
            slips = salarySlipRepository.findByJobNumberAndMainId(checkAuth.getJobNumber(), mid);
        } else if (SL_CHK_ALL.equals(checkAuth.getViewAuth())) {
            slips = salarySlipRepository.findByMainId(mid);
        }
        return slips;
    }


    /**
     * 组装多级表头，只有2级表头
     */
    private List<SalaryHeader> buildMultiHeader(List<SalaryHeader> headers) {
        if (headers == null || headers.isEmpty()) {
            return headers;
        }
        //二级表头
        List<SalaryHeader> header2 = headers.stream().filter(i -> i.getStartRow() == 2).toList();
        final List<SalaryHeader> header1 = headers.stream().filter(i -> i.getStartRow() == 1).toList();
        //添加parentLabel
        for (SalaryHeader h : header2) {
            int col = h.getStartColumn();
            header1.stream().filter(i -> i.getStartColumn() == col).findFirst().ifPresent(i -> h.setParentLabel(i.getName()));
        }
        return header2;
    }


    /**
     * 审批限制
     * @param checkAuth 审核权限
     * @param slids     待审核的slip id list
     */
    @Override
    public R<List<SalarySlip>> deptCheck(CheckAuth checkAuth, List<String> slids) {
        if (slids == null || slids.isEmpty()) {
            return R.warn("没有要审批的工资条", null);
        }
        final List<SalarySlip> slips = salarySlipRepository.findAllById(slids);
        //判断逐级确认
        Set<SalarySlip> errList = new HashSet<>();
        //人事审批不用限制，其他领导审批必须员工先确认
        if (!SL_CHK_HR.equals(checkAuth.getRole())) {
            for (SalarySlip s : slips) {
                if (s.isForceCheck() && !s.isCheck()) {
                    s.addError("员工未确认");
                    errList.add(s);
                }
                if (SL_CHK_DM.equals(checkAuth.getRole())) {
                    continue;
                }
                if (StringUtils.isNotBlank(s.getDmJobNumber()) && !s.isDmCheck()) {
                    s.addError("部门经理未确认");
                    errList.add(s);
                }
//            if (SL_CHK_DCEO.equals(checkAuth.getRole())) {
//                continue;
//            }
//            if (StringUtils.isNotBlank(s.getDceoJobNumber()) && !s.isDceoCheck()) {
//                s.addError("副总经理未确认");
//                errList.add(s);
//            }
            }
        }

        if (!errList.isEmpty()) {
            return R.bizFail(new ArrayList<>(errList), "审批失败");
        }

        if (SL_CHK_DM.equals(checkAuth.getRole())) {
            salarySlipRepository.updateDmChecked(checkAuth.getJobNumber(), checkAuth.getName(), LocalDateTime.now(), slids);
        } else if (SL_CHK_DCEO.equals(checkAuth.getRole())) {
            salarySlipRepository.updateDceoChecked(checkAuth.getJobNumber(), checkAuth.getName(), LocalDateTime.now(), slids);
        } else if (SL_CHK_HR.equals(checkAuth.getRole())) {
            salarySlipRepository.updateHrChecked(checkAuth.getJobNumber(), checkAuth.getName(), LocalDateTime.now(), slids);
        } else if (SL_CHK_CEO.equals(checkAuth.getRole())) {
            salarySlipRepository.updateCeoCheck(checkAuth.getJobNumber(), checkAuth.getName(), LocalDateTime.now(), slids);
        } else {
            log.warn("无权审批工资(role:{})", checkAuth.getRole());
            return R.warn("无权审批工资(role:" + checkAuth.getRole() + ")");
        }

        return R.success(null, "审批成功");
    }

    @Override
    public List<SalaryMain> findCheckList(String yearMonth, CheckAuth checkAuth) {
        List<SalaryMain> list = new ArrayList<>();
        final List<SalaryMain> slms = salaryMainRepository.findByYearMonthNullable(yearMonth);
        for (SalaryMain sm : slms) {
            //判断每个slmain是否存在要审批的
            final List<SalarySlip> slips = findSalarySlipsByCheckAuth(checkAuth, sm.getId());
            if (!slips.isEmpty()) {
                long checked = 0;
                if (SL_CHK_DM.equals(checkAuth.getRole())) {
                    checked = slips.stream().filter(SalarySlip::isDmCheck).count();
                } else if (SL_CHK_DCEO.equals(checkAuth.getRole())) {
                    checked = slips.stream().filter(SalarySlip::isDceoCheck).count();
                }
                sm.setLeaderChecked(checked);
                sm.setLeaderAllCheck(slips.size());
                sm.setLeaderCheckTime(slips.get(0).getDmTime());
                sm.setGroupName(translateCompanyName(sm.getGroup()));
                list.add(sm);
            }
        }
        return list;
    }

    @Override
    public String translateCompanyName(String code) {
        return switch (code) {
            case COMPANY_A -> abtCompany.getCustomerName();
            case COMPANY_G -> grdCompany.getCustomerName();
            case COMPANY_D -> dcCompany.getCustomerName();
            default -> "";
        };
    }

    @Override
    public List<SalaryMain> summaryCheckList(String yearMonth, CheckAuth checkAuth) {
        List<SalaryMain> list = new ArrayList<>();
        final List<SalaryMain> slms = salaryMainRepository.findByYearMonthNullable(yearMonth);
        final Map<String, List<SalaryMain>> smMap = slms.stream().collect(Collectors.groupingBy(SalaryMain::getYearMonth, toList()));
        for(Map.Entry<String, List<SalaryMain>> entry : smMap.entrySet()) {
            SalaryMain sm = new SalaryMain();
            sm.setYearMonth(entry.getKey());
            final Set<String> ids = entry.getValue().stream().map(SalaryMain::getId).collect(Collectors.toSet());
            final List<SalarySlip> slips = salarySlipRepository.findByMainIdIn(ids);
            if (!slips.isEmpty()) {
                final long ceoCheck = slips.stream().filter(SalarySlip::isCeoCheck).count();
                sm.setLeaderChecked(ceoCheck);
                sm.setLeaderAllCheck(slips.size());
                sm.setLeaderCheckTime(slips.get(0).getCeoTime());
                list.add(sm);
            }
        }
        return list;
    }

//    public List<SlipCount> salaryCountYearMonthByCheckAuth(String yearMonth, CheckAuth checkAuth) {
//        if (SL_CHK_CEO.equals(checkAuth.getViewAuth())) {
//            return salarySlipRepository.countCeoCheck(yearMonth);
//        } else if (SL_CHK_DCEO.equals(checkAuth.getViewAuth())) {
//            return salarySlipRepository.countDceoCheck(checkAuth.getJobNumber(), yearMonth);
//        } else if (SL_CHK_DM.equals(checkAuth.getViewAuth())) {
//            return salarySlipRepository.countDmCheck(checkAuth.getJobNumber(), yearMonth);
//        } else if (SL_CHK_HR.equals(checkAuth.getViewAuth())) {
//            return salarySlipRepository.countHrCheck(yearMonth);
//        } else if (SL_CHK_USER.equals(checkAuth.getViewAuth())) {
//            return salarySlipRepository.countUserCheck(checkAuth.getJobNumber(), yearMonth);
//        } else if (SL_CHK_ALL.equals(checkAuth.getViewAuth())) {
//            return salarySlipRepository.countAllCheck(yearMonth);
//        } else {
//            log.warn("未知的工资查看权限{}", checkAuth.getViewAuth());
//            return List.of();
//        }
//    }




    @Override
    public List<SalaryPreview> salarySummaryList(String yearMonth, CheckAuth checkAuth) {
        List<SalaryPreview> list = new ArrayList<>();
        final SalaryPreview abt = this.salarySummaryTable(yearMonth, COMPANY_A, checkAuth);
        final SalaryPreview grd = this.salarySummaryTable(yearMonth, COMPANY_G, checkAuth);
        final SalaryPreview dc = this.salarySummaryTable(yearMonth, COMPANY_D, checkAuth);
        list.add(abt);
        list.add(grd);
        list.add(dc);

        return list;
    }


    public SalaryPreview salarySummaryTable(@NotNull String yearMonth, String group, CheckAuth checkAuth) {
        final List<SalaryMain> smByYm = salaryMainRepository.findByYearMonthAndGroup(yearMonth, group);
        if (smByYm == null || smByYm.isEmpty()) {
            return new SalaryPreview();
        }
        if (smByYm.size() > 1) {
            final String name = this.translateCompanyName(group);
            //一般只上传一个，暂时不考虑多个的，因为不同表头合并可能产生问题
            throw new BusinessException(name + " 工资表上传多次");
        }
        return this.getSalaryCheckTable(checkAuth, smByYm.get(0));
    }

    @Override
    public void updateUserSalaryCell(String cellId, String value) {
        if (StringUtils.isBlank(cellId)) {
            throw new BusinessException("未传入cell id");
        }
        final SalaryCell cell = salaryCellRepository.findById(cellId).orElseThrow(() -> new BusinessException("未查询到工资数据(cellId=" + cellId + ")"));
        String slipId = cell.getSlipId();
        final SalarySlip slip = salarySlipRepository.findById(slipId).orElseThrow(() -> new BusinessException("未查询到工资条(slipId=" + slipId + ")"));
        if (slip.isForceCheck() && slip.isCheck()) {
            throw new BusinessException(String.format("用户[%s:%s]已确认签名，无法修改", slip.getJobNumber(), slip.getName()));
        }
        if (LABEL_NAME.equals(cell.getLabel())) {
            throw new BusinessException("无法修改姓名!");
        }
        //1. sl_cell修改
        cell.setValue(value);
        salaryCellRepository.updateValueById(value, cellId);
    }


    @Override
    public LocalDateTime sendSlipById(String slipId) {
        final LocalDateTime autoCheckTime = getAutoCheckTime(LocalDateTime.now());
        salarySlipRepository.updateSendById(slipId, autoCheckTime);
        return autoCheckTime;
    }

    /**
     * 根据序号排序行
     * @param table table
     */
    private List<List<SalaryCell>> sortRows(final List<List<SalaryCell>> table) {
        List<List<SalaryCell>> newTable = new ArrayList<>(table);
        // 按“序号”列的值排序
        newTable.sort((row1, row2) -> {
            // 获取每行的“序号”单元格的值
            String serial1 = getSerialNumber(row1);
            String serial2 = getSerialNumber(row2);

            // 将字符串转换为整数进行比较
            try {
                int num1 = Integer.parseInt(serial1);
                int num2 = Integer.parseInt(serial2);
                return Integer.compare(num1, num2);
            } catch (NumberFormatException e) {
                // 如果转换失败，返回0（或根据需求处理）
                return 0;
            }
        });

        return newTable;
    }

    private String getSerialNumber(List<SalaryCell> row) {
        for (SalaryCell cell : row) {
            if ("序号".equals(cell.getLabel())) {
                return cell.getValue();
            }
        }
        return "0"; // 默认值，假设没有找到序号
    }

    @Override
    public String createCheckExcel(String company, String yearMonth, CheckAuth checkAuth, String mid) throws IOException {
        String fullCompany = "";
        if ("ABT".equalsIgnoreCase(company)) {
            fullCompany = abtCompany.getCustomerName();
        } else if ("GRD".equalsIgnoreCase(company)) {
            fullCompany = grdCompany.getCustomerName();
        } else if ("DC".equalsIgnoreCase(company)) {
            fullCompany = dcCompany.getCustomerName();
        } else {
            throw new BusinessException("未知的工资分组: " + company);
        }
        final SalaryMain main = findSalaryMainById(mid);
        final SalaryPreview preview = getSalaryCheckTable(checkAuth, main);
        //1. 获取数据
        final List<SalaryHeader> headers = salaryHeaderRepository.findByMidOrderByStartRowAscStartColumnAsc(preview.getSalaryMain().getId());
        List<SalaryHeader> header2 = headers.stream().filter(h -> h.getStartRow() == 2).collect(Collectors.toCollection(ArrayList::new));
        List<SalaryHeader> header1 = headers.stream().filter(h -> h.getStartRow() == 1).collect(Collectors.toCollection(ArrayList::new));
        addSignatureColumn(header1);
        addSignatureColumn(header2);
        List<UserSignature> us = userSignatureRepository.findAllUserSignatures();
        Map<String, UserSignature> usMap = us.stream().collect(Collectors.toMap(UserSignature::getJobNumber, i -> i));

        //2. 创建excel
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet();

            //---- 首行标题
            Row row0 = sheet.createRow(0);
            Cell titleCell = row0.createCell(0);
            titleCell.setCellValue(fullCompany + "工资表(" + yearMonth + ")");
            CellStyle titleStyle = workbook.createCellStyle();
            // 设置字体
            Font font = workbook.createFont();
            font.setFontHeightInPoints((short) 16); // 字体大小
            font.setBold(true);                     // 加粗
            titleStyle.setFont(font);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);       // 居中
            titleStyle.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直居中
            titleStyle.setLocked(true);
            // 应用样式
            titleCell.setCellStyle(titleStyle);

            // 计算excel总列数
            int totalColumns = Math.max(header1.size(), header2.size());
            // 添加签名列的数量
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, totalColumns));

            //---- 表头
            CellStyle headerStyle = createCellStyle(workbook);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setLocked(true);
            //写入表头
            Row row1 = sheet.createRow(1);
            Row row2 = sheet.createRow(2);
            for (int c = 0; c < totalColumns; c++) {
                Cell c1 = row1.createCell(c);
                c1.setCellValue(header1.get(c).getName());
                c1.setCellStyle(headerStyle);
                Cell c2 = row2.createCell(c);
                c2.setCellValue(header2.get(c).getName());
                c2.setCellStyle(headerStyle);
            }
            //上下行合并，合并表头
            for(int i = 0; i < totalColumns; i++) {
                String n1 = row1.getCell(i).getStringCellValue();
                String n2 = row2.getCell(i).getStringCellValue();
                if (cn.idev.excel.util.StringUtils.equals(n1, n2)) {
                    //表头一样合并
                    sheet.addMergedRegion(new CellRangeAddress(1, 2, i, i));
                }
            }

            //------ 写入数据
            final List<List<SalaryCell>> cells = preview.getSlipTable();
            final List<List<SalaryCell>> table = sortRows(cells);
            final List<SalarySlip> slips = preview.getSlips();
            final Map<String, SalarySlip> slipMap = slips.stream().collect(Collectors.toMap(SalarySlip::getJobNumber, i -> i));
//            写入excel
            final UserSignature empty = new UserSignature();
            CellStyle dataStyle = createCellStyle(workbook);
            int colMax = 0;
            for(int r = 0; r < table.size(); r++) {
                Row row = sheet.createRow(r + 3);
                List<SalaryCell> data = table.get(r);
                String jobNumber = data.get(0).getJobNumber();
                for (int c = 0; c < data.size(); c++) {
                    Cell cell = row.createCell(c);
                    final SalaryCell sc = data.get(c);
                    cell.setCellValue(sc.getValue());
                    cell.setCellStyle(dataStyle);
                }
//                签名列
//                个人
                SalarySlip slip = slipMap.get(jobNumber);
                UserSignature userSig = usMap.get(jobNumber);
                int col = data.size() ;
                colMax = Math.max(colMax, col);
                if (userSig != null && slip.isCheck()) {
                    insertImage(sigDir + userSig.getFileName(), workbook, sheet, row, col,r + 3);
                }
                col = col + 1;
                //副总
                if (StringUtils.isNotBlank(slip.getDceoJobNumber()) && slip.isDceoCheck()) {
                    UserSignature dceoSig = usMap.getOrDefault(slip.getDceoJobNumber(), empty);
                    insertImage(sigDir + dceoSig.getFileName(), workbook, sheet, row, col,r + 3);
                }
                col = col + 1;
                //人事
                if (StringUtils.isNotBlank(slip.getHrJobNumber()) && slip.isHrCheck()) {
                    UserSignature hrSig = usMap.getOrDefault(slip.getHrJobNumber(), empty);
                    insertImage(sigDir + hrSig.getFileName(), workbook, sheet, row, col,r + 3);
                }

                col = col + 1;
                //总经理
                if (StringUtils.isNotBlank(slip.getCeoJobNumber()) && slip.isCeoCheck()) {
                    UserSignature ceoSig = usMap.getOrDefault(slip.getCeoJobNumber(), empty);
                    insertImage(sigDir + ceoSig.getFileName(), workbook, sheet, row, col,r + 3);
                }
            }
            //---- 设置列宽
            //合计行
            Row sumRow = sheet.createRow(table.size() + 3);
            //合计
            Cell c0 = sumRow.createCell(0);
            c0.setCellValue("合计");
            c0.setCellStyle(dataStyle);
            final List<BigDecimal> sums = sumEachColumn(table, colMax);
            for (int c = 1; c < colMax; c++) {
                Cell cell = sumRow.createCell(c);
                cell.setCellValue(sums.get(c).toString());
                cell.setCellStyle(dataStyle);
            }
            //合计合并单元格
            sheet.addMergedRegion(new CellRangeAddress(sumRow.getRowNum(), sumRow.getRowNum(), 0, 4));

            //序号，姓名，工号
            sheet.setColumnWidth(0, 4 * 256);
            sheet.setColumnWidth(1, 6 * 256);
            sheet.setColumnWidth(2, 4 * 256);
            //部门
            sheet.setColumnWidth(3, 12 * 256);
            //岗位
            sheet.setColumnWidth(4, 14 * 256);
            //其他数据(不含图片)
            //设置宽度
            for (int c = 5; c < colMax; c++) {
                sheet.setColumnWidth(c, 8 * 256);
            }

            //保存excel
            String excelName = checkExcelDir +  company + yearMonth + ".xlsx";

            // 锁定结构（防止添加/删除 sheet）
            // 设置允许的操作：允许格式化列（隐藏列需要这个权限）
            sheet.enableLocking();
            sheet.lockDeleteColumns(true);
            sheet.lockDeleteRows(true);
            sheet.lockInsertColumns(true);
            sheet.lockInsertRows(true);
            sheet.lockSort(true);
            sheet.lockAutoFilter(true);
            sheet.lockFormatCells(true);
            sheet.lockFormatRows(true);
            sheet.lockFormatColumns(false); // 允许格式化列（可以隐藏）
            sheet.protectSheet("readonly123");
            workbook.setWorkbookPassword("readonly123", null); // 设置保护密码

            // 输出文件
            try (FileOutputStream fos = new FileOutputStream(excelName)) {
                workbook.write(fos);
            }

            return excelName;
        }

    }

    //导出excel合计行
    public List<BigDecimal> sumEachColumn(List<List<SalaryCell>> table, int maxCol) {
        List<BigDecimal> columnSums = new ArrayList<>(Collections.nCopies(maxCol, BigDecimal.ZERO));
        for (List<SalaryCell> row : table) {
            for (int i = 5; i < maxCol; i++) {
                String value = row.get(i).getValue();
                try {
                    BigDecimal d2 = columnSums.get(i);
                    BigDecimal d1 = new BigDecimal(value);
                    columnSums.set(i, d2.add(d1).setScale(2, RoundingMode.FLOOR));
                } catch (Exception e) {
                    log.error("非数字，无法合计--" + e.getMessage(), e);
                }
            }
        }
        return columnSums;
    }

    private void addSignatureColumn(List<SalaryHeader> header) {
        header.add(new SalaryHeader("个人签名"));
        header.add(new SalaryHeader("副总审核"));
        header.add(new SalaryHeader("人事审核"));
        header.add(new SalaryHeader("总经理审核"));
    }

    CellStyle createCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setWrapText(true);

        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10); // 字体大小
        style.setFont(font);
        return style;
    }

    @Override
    public List<SalaryMain> findAllSalaryMainByYearLike(String year) {
        return salaryMainRepository.findByYear(year);
    }

    /**
     * 插入excel图片，固定高度，宽度
     * @param imagePath 图片路径
     * @param workbook workbook
     * @param sheet sheet
     * @param row row
     * @param colIndex 插入的单元格col
     * @param rowIndex 插入的单元各row
     */
    private void insertImage(String imagePath, XSSFWorkbook workbook, XSSFSheet sheet, Row row, int colIndex, int rowIndex) {
        try {
            FileInputStream imageStream = new FileInputStream(imagePath);
            byte[] imageBytes = IOUtils.toByteArray(imageStream);
            int pictureIdx = workbook.addPicture(imageBytes, Workbook.PICTURE_TYPE_JPEG);
            imageStream.close();
            // 创建绘图工具
            XSSFDrawing drawing = sheet.createDrawingPatriarch();

            // 设置单元格的行高和列宽（注意：列宽单位为1/256字符宽度，行高单位为磅）
            sheet.setColumnWidth(colIndex, 10 * 256); // 10个字符宽
            row.setHeightInPoints(20); // 30磅高

            // 创建锚点，将图片锚定到单元格
            XSSFClientAnchor anchor = new XSSFClientAnchor();
            anchor.setCol1(colIndex);
            anchor.setRow1(rowIndex);
            anchor.setCol2(colIndex + 1);
            anchor.setRow2(rowIndex + 1);

            // 插入图片并获取图片对象
            final XSSFPicture pic = drawing.createPicture(anchor, pictureIdx);
            //调整图片占单元格的大小，1就是100%
            pic.resize(1);
        } catch (Exception e) {
            log.error("User signature not found: " + imagePath, e);
        }
    }

    @Override
    public void ceoCheckAllByYearMonth(String yearMonth, CheckAuth checkAuth) {
        if (StringUtils.isBlank(yearMonth)) {
            throw new BusinessException("工资年月未知");
        }
        salarySlipRepository.ceoCheckByYearMonth(yearMonth, LocalDateTime.now());
    }

    @Override
    public void hrCheckAllByYearMonth(String yearMonth, CheckAuth checkAuth) {
        if (StringUtils.isBlank(yearMonth)) {
            throw new BusinessException("工资年月未知");
        }
        salarySlipRepository.hrCheckByYearMonth(yearMonth, LocalDateTime.now());
    }

    @Transactional
    @Override
    public void updateUserSlip(String slipId, SalaryMain main) {
        final SalarySlip slip = salarySlipRepository.findById(slipId).orElseThrow(() -> new BusinessException("未查询到工资条(id=" + slipId + ")"));
        final SalaryCell empCostCell = salaryCellRepository.findBySlipIdAndColumnIndex(slipId, main.getEmpCostColumnIndex());
        final SalaryCell empNetPaidCell = salaryCellRepository.findBySlipIdAndColumnIndex(slipId, main.getNetPaidColumnIndex());
        //update
        salarySlipRepository.updateUserEmpCostAndNetPaid(slipId, new BigDecimal(empCostCell.getValue()), new BigDecimal(empNetPaidCell.getValue()));
    }

    @Override
    public SalaryMain recalculateSalaryMainSumData(String mid, @NotNull SalaryMain sm) {
        //empCost
        final Integer empCostColumnIndex = sm.getEmpCostColumnIndex();

        if (empCostColumnIndex != null) {
            //获取cells计算
            final List<SalaryCell> cells = salaryCellRepository.findByColumnIndexAndMidOrderByRowIndex(empCostColumnIndex, mid);
            sm.setSumCost(sumDoubleValue(cells));
        }
        //netPaid
        final Integer netPaidColumnIndex = sm.getNetPaidColumnIndex();
        if (netPaidColumnIndex != null) {
            final List<SalaryCell> cells = salaryCellRepository.findByColumnIndexAndMidOrderByRowIndex(netPaidColumnIndex, mid);
            sm.setSumNetPaid(sumDoubleValue(cells));
        }
        sm = salaryMainRepository.save(sm);
        return sm;

    }

    public Double sumDoubleValue(List<SalaryCell> cells) {
        return cells.stream().filter(c -> StringUtils.isNotBlank(c.getValue())).map(c -> {
            try {
                return new BigDecimal(c.getValue());
            } catch (Exception e) {
                log.error(String.format("%s无法转为数字", c.getValue()), e);
                return BigDecimal.ZERO;
            }
        }).reduce(BigDecimal.ZERO, BigDecimal::add).doubleValue();
    }


    @Override
    public void adminUserCheck(String yearMonth) {
        if (StringUtils.isBlank(yearMonth)) {
            throw new BusinessException("工资年月未传入");
        }
        salarySlipRepository.updateAllUnchecked(LocalDateTime.now(), yearMonth);
    }

    @Override
    public List<SalarySlip> findUncheckUserSlipsByMainId(String mainId) {
        return salarySlipRepository.findUncheckByMainId(mainId);
    }

    @Override
    public SlipCount ceoSlipCount(String mid) {
        return salarySlipRepository.ceoSlipCount(mid);
    }

    @Override
    public SlipCount hrSlipCount(String mid) {
        return salarySlipRepository.hrSlipCount(mid);
    }

    @Override
    public SlipCount dceoSlipCount(String mid, CheckAuth checkAuth) {
        final String jobNumber = checkAuth.getJobNumber();
        return salarySlipRepository.dceoSlipCount(mid, jobNumber);

    }

}
