package com.abt.salary.controller;

import com.abt.common.model.R;
import com.abt.common.util.ValidateUtil;
import com.abt.salary.entity.SalaryMain;
import com.abt.salary.model.SalaryMainDTO;
import com.abt.salary.service.SalaryService;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.entity.SystemFile;
import com.alibaba.excel.EasyExcel;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 *
 */
@RestController
@Slf4j
@RequestMapping("/sl")
public class SalaryController {
    private final SalaryService salaryService;

    public SalaryController(SalaryService salaryService) {
        this.salaryService = salaryService;
    }


    /**
     * 获取http session
     * @param session 等于HttpServletRequest.getSession();
     */
    @GetMapping("/create/sid")
    public R<String> applyHttpSessionId(HttpSession session) {
        final UUID uuid = UUID.randomUUID();
        String sid = session.getId();
        R<String> r = R.success(uuid.toString(), "申请sid成功");
        r.setSid(sid);
        return r;
    }

    @GetMapping("/validate/sid")
    public void validateSessionId(String sid, HttpSession session) {
        final String id = session.getId();
        System.out.println("id:" + id);
        System.out.println("sid: " + sid);
    }

    /**
     * 上传工资excel
     * 1. 上传文件
     * 2. 解析文件
     */
    @PostMapping("/upload")
    @ResponseBody
    public R<Object> upload(MultipartFile file, String yearMonth, String group, Integer sheetNo) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请上传工资表!");
        }
        if (sheetNo == null) {
            sheetNo = 1;
        }
        ValidateUtil.ensurePropertyNotnull(yearMonth, "必须选择发放工资年月(yearMonth)");
        ValidateUtil.ensurePropertyNotnull(group, "必须选择工资组(group)");
        final SystemFile sysfile = salaryService.saveSalaryExcel(file, yearMonth);
        System.out.println("上传文件: " + sysfile.toString());
        final SalaryMain slm = salaryService.createSalaryMain(yearMonth, group, "", sysfile.getFullPath(), sysfile.getName());
        salaryService.previewSalaryDetail(file.getInputStream(), 1, slm);
        return R.success("上传成功");
    }

    /**
     * 根据选择的年月查询上传的工资
     * @param yearMonth 年月: yyyy-MM
     */
    @GetMapping("/load")
    public R<List<SalaryMainDTO>> loadSalaryBill(String yearMonth, String group) {
        //查询上传工资表
        //查询上传工资表对应的发送工资条情况(已发送/已查看/已确认）
        final List<SalaryMainDTO> list = salaryService.findSalaryMainByYearMonthAndGroup(yearMonth, group);
        return R.success(list);
    }

    /**
     * 删除已上传的工资条
     * @param id 工资表id(sl_main:id)
     */
    @GetMapping("/del/all")
    public R<Object> deleteSalaryMain(String id) {
        salaryService.deleteSalaryAll(id);
        return R.success("删除成功");
    }
}
