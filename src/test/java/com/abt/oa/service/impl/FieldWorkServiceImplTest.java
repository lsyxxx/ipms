package com.abt.oa.service.impl;

import com.abt.oa.entity.FieldWorkAttendanceSetting;
import com.abt.oa.reposity.FieldAttendanceSettingRepository;
import com.abt.oa.reposity.FieldWorkItemRepository;
import com.abt.oa.reposity.FieldWorkRepository;
import com.abt.oa.service.FieldWorkService;
import com.abt.sys.service.EmployeeService;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.enums.WriteDirectionEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FieldWorkServiceImplTest {
    @Autowired
    private FieldWorkService fieldWorkService;

    @Test
    void writeExcel() {

        String yearMonth = "2024-08";
        String fileName = String.format("%s考勤表.xlsx", yearMonth);
        String template = "E:\\fw_atd\\fieldwork_atd_model.xlsx";
        String createFile = "E:\\fw_atd\\" + fileName;
        Map<String, String> fillMap = new HashMap<>();
        fillMap.put("yearMonth", yearMonth);

        //head
        List<Map<String, String>> summaryHeader = List.of(
                Map.of("summaryHeader", "流体2人作业负责人"),
                Map.of("summaryHeader", "流体2人作业组员"),
                Map.of("summaryHeader", "兼职司机人员接送或物资现场配合"),
                Map.of("summaryHeader", "其他野外工作"),
                Map.of("summaryHeader", "请假"),
                Map.of("summaryHeader", "办理业务或室内工作"),
                Map.of("summaryHeader", "解吸2人作业负责人")
        );

        final List<FieldWorkAttendanceSetting> settings = fieldWorkService.findLatestSettings();
        FieldWorkExcelHeaderStyleHandler fwHandler = new FieldWorkExcelHeaderStyleHandler(settings);

        try (ExcelWriter excelWriter = EasyExcel.write(createFile).withTemplate(template).registerWriteHandler(fwHandler).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet(0).build();
            FillConfig fillConfig = FillConfig.builder().direction(WriteDirectionEnum.HORIZONTAL).build();
            excelWriter.fill(summaryHeader, fillConfig, writeSheet);
            excelWriter.fill(fillMap, writeSheet);
        }


        System.out.println("----- 写入excel完成");

    }
}