package com.abt.oa.service.impl;

import com.abt.oa.entity.PaiBan;
import com.abt.oa.service.PaiBanService;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Check;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PaiBanServiceImplTest {

    @Autowired
    private PaiBanService paiBanService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findBetween() {
        final List<PaiBan> list = paiBanService.findBetween(LocalDate.of(2024, 6, 26), LocalDate.of(2024, 7, 25));
        assertNotNull(list);
        list.forEach(i ->  {
            System.out.printf("日期: %s, 类型: %s", i.getPaibandate().toString(), i.getPaibanType().toString());
        });
    }

    @Getter
    @Setter
    class CheckInLog {
        /**
         * 打卡机的员工编号
         */
        private String enNo;

        /**
         * 员工姓名
         */
        private String name;

        private String mode;

        private LocalDateTime checkInTime;

    }

    public static void main(String[] args) {
        PaiBanServiceImplTest test = new PaiBanServiceImplTest();

        String filePath = "F:\\00-平台资料汇总\\考勤\\agl001.txt"; // 替换为实际文件路径
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            // 跳过标题行（如果有）
             reader.readLine();     // 取消注释以跳过第一行（如果有标题）
             reader.readLine();      //第二行标题行

            while ((line = reader.readLine()) != null) {
                // 按制表符分隔
                System.out.println("------------------");
                String[] fields = line.split("\t");
                for (String field : fields) {
                    System.out.println(field);
                }

//                if (fields.length >= 7) { // 确保最少有7个字段
//                    try {
//                        String id = fields[0].trim();
//                        String status = fields[1].trim();
//                        String constant = fields[2].trim();
//                        String description = fields[3].trim();
//                        String code = fields[4].trim();
//                        String index = fields[5].trim();
//                        String dateTimeStr = fields[6].trim();
//
//                        // 解析日期时间
//                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
//                        LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, formatter);
//
//                        // 可选字段（如果存在）
//                        String optionalField = (fields.length > 7) ? fields[7].trim() : null;
//
//                        // 输出结果
//                        System.out.println("ID: " + id);
//                        System.out.println("Status: " + status);
//                        System.out.println("Constant: " + constant);
//                        System.out.println("Description: " + description);
//                        System.out.println("Code: " + code);
//                        System.out.println("Index: " + index);
//                        System.out.println("DateTime: " + dateTime);
//                        if (optionalField != null) {
//                            System.out.println("OptionalField: " + optionalField);
//                        }
//                        System.out.println("-------------------");
//                    } catch (Exception e) {
//                        System.err.println("解析行失败: " + line + " - 错误: " + e.getMessage());
//                    }
//                } else {
//                    System.err.println("行格式错误: " + line);
//                }
            }
        } catch (IOException e) {
            System.err.println("读取文件时发生错误: " + e.getMessage());
        }

    }
}