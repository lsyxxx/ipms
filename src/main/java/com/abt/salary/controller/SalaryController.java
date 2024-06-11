package com.abt.salary.controller;

import com.abt.common.model.R;
import com.abt.salary.entity.SalaryMain;
import com.abt.salary.model.SalaryMainDTO;
import com.abt.salary.service.SalaryService;
import com.alibaba.excel.EasyExcel;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 *
 */
@RestController
@Slf4j
//@RequestMapping("/sl")
public class SalaryController {
    private final SalaryService salaryService;

    public SalaryController(SalaryService salaryService) {
        this.salaryService = salaryService;
    }


    /**
     * 上传工资excel
     */
    @PostMapping("/test/sl/upload")
    @ResponseBody
    public String upload(MultipartFile file, String yearMonth, String group, String netPaidColumnName) throws IOException {
        final SalaryMain slm = salaryService.createSalaryMain(yearMonth, group, netPaidColumnName);
        //删除已有数据
        salaryService.previewSalaryDetail(file.getInputStream(), "2024年3月工资", slm);
        return "success";
    }

    /**
     * 根据选择的年月查询上传的工资
     * @param yearMonth 年月: yyyy-MM
     */
    @GetMapping("/test/sl/load")
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
    @GetMapping("/test/sl/del/all")
    public R<Object> deleteSalaryMain(String id) {
        salaryService.deleteSalaryAll(id);
        return R.success("删除成功");
    }
}
