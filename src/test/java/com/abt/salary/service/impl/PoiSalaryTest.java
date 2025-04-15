package com.abt.salary.service.impl;

import cn.idev.excel.util.StringUtils;
import com.abt.common.model.User;
import com.abt.salary.entity.SalaryCell;
import com.abt.salary.entity.SalaryHeader;
import com.abt.salary.entity.SalaryMain;
import com.abt.salary.entity.SalarySlip;
import com.abt.salary.model.CheckAuth;
import com.abt.salary.model.SalaryPreview;
import com.abt.salary.repository.SalaryHeaderRepository;
import com.abt.salary.service.SalaryService;
import com.abt.wf.entity.UserSignature;
import com.abt.wf.repository.UserSignatureRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.apache.poi.util.IOUtils;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 */
@SpringBootTest
public class PoiSalaryTest {

    @Autowired
    private SalaryService salaryService;
    @Autowired
    private UserSignatureRepository userSignatureRepository;

    @Autowired
    private SalaryHeaderRepository salaryHeaderRepository;



    void insertImage(String imagePath, XSSFWorkbook workbook, XSSFSheet sheet, Row row, int colIndex, int rowIndex) throws IOException {
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
//
//            // 获取图片实际大小
//            BufferedImage image = javax.imageio.ImageIO.read(new File(imagePath));
//            int imgWidthPx = image.getWidth();
//            int imgHeightPx = image.getHeight();
//
//            // 单元格行高 20 磅 ≈ 26.67 px
//            float cellHeightPx = 26.67f;

            // 根据高度计算缩放比例，保持宽高比
//            double scale = cellHeightPx / imgHeightPx;

            // 插入图片并获取图片对象
            final XSSFPicture pic = drawing.createPicture(anchor, pictureIdx);
            //调整图片占单元格的大小，1就是100%
            pic.resize(1);
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + imagePath);
        }


    }

    public static void main(String[] args) throws IOException {
        // 创建工作簿和工作表
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("ImageSheet");
// 要插入的图片路径
        String imagePath = "F:\\sig\\001弓虎军-1.png";
        FileInputStream imageStream = new FileInputStream(imagePath);
        byte[] imageBytes = IOUtils.toByteArray(imageStream);
        int pictureIdx = workbook.addPicture(imageBytes, Workbook.PICTURE_TYPE_JPEG);
        imageStream.close();

        // 创建绘图工具
        XSSFDrawing drawing = sheet.createDrawingPatriarch();

        // 指定单元格位置（比如 B2，即 row=1, col=1）
        int row = 1;
        int col = 1;

        // 设置单元格的行高和列宽（注意：列宽单位为1/256字符宽度，行高单位为磅）
        sheet.setColumnWidth(col, 10 * 256); // 20个字符宽
        sheet.createRow(row).setHeightInPoints(30); // 100磅高

        // 创建锚点，将图片锚定到单元格
        XSSFClientAnchor anchor = new XSSFClientAnchor();
        anchor.setCol1(col);
        anchor.setRow1(row);
        anchor.setCol2(col + 1);
        anchor.setRow2(row + 1);

        // 插入图片并获取图片对象
        final XSSFPicture picture = drawing.createPicture(anchor, pictureIdx);
        picture.resize(1);

        //*** 自适应
        for (int i = 0; i < 3; i++) {
            sheet.autoSizeColumn(i);
        }

        // 保存文件
        try (FileOutputStream out = new FileOutputStream("C:\\Users\\Administrator\\Desktop\\pic_test.xlsx")) {
            workbook.write(out);
        }

    }

    void addSignatureColumn(List<SalaryHeader> header) {
        header.add(new SalaryHeader("个人签名"));
        header.add(new SalaryHeader("副总审核"));
        header.add(new SalaryHeader("人事审核"));
        header.add(new SalaryHeader("总经理审核"));
    }

    @SneakyThrows
    @Test
    void creatExcel() throws IOException {
        final SalaryPreview preview = getData();
        final List<SalaryHeader> headers = salaryHeaderRepository.findByMidOrderByStartRowAscStartColumnAsc(preview.getSalaryMain().getId());
        List<SalaryHeader> header2 = headers.stream().filter(h -> h.getStartRow() == 2).collect(Collectors.toCollection(ArrayList::new));
        List<SalaryHeader> header1 = headers.stream().filter(h -> h.getStartRow() == 1).collect(Collectors.toCollection(ArrayList::new));
        addSignatureColumn(header1);
        addSignatureColumn(header2);
        List<UserSignature> us = userSignatureRepository.findAllUserSignatures();
        Map<String, UserSignature> usMap = us.stream().collect(Collectors.toMap(UserSignature::getJobNumber, i -> i));
        //表头
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("ABT");

            //---- 首行标题
            Row row0 = sheet.createRow(0);
            Cell titleCell = row0.createCell(0);
            titleCell.setCellValue("XXX工资表");
            CellStyle titleStyle = workbook.createCellStyle();
            // 设置字体
            Font font = workbook.createFont();
            font.setFontHeightInPoints((short) 16); // 字体大小
            font.setBold(true);                     // 加粗
            titleStyle.setFont(font);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);       // 居中
            titleStyle.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直居中
//            titleStyle.setLocked(true);
            // 应用样式
            titleCell.setCellStyle(titleStyle);

            // 计算excel总列数
            int totalColumns = Math.max(header1.size(), header2.size());
            // 添加签名列的数量
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, totalColumns));

            //---- 表头
            //表头样式
            CellStyle headerStyle = createCellStyle(workbook);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
//            headerStyle.setLocked(true);
           //写入表头
            Row row1 = sheet.createRow(1);
            Row row2 = sheet.createRow(2);
            //一级表头

            for (int c = 0; c < totalColumns; c++) {
                Cell c1 = row1.createCell(c);
                c1.setCellValue(header1.get(c).getName());
                c1.setCellStyle(headerStyle);
                Cell c2 = row2.createCell(c);
                c2.setCellValue(header2.get(c).getName());
                c2.setCellStyle(headerStyle);
            }
            //上下行合并
            for(int i = 0; i < totalColumns; i++) {
                String n1 = row1.getCell(i).getStringCellValue();
                String n2 = row2.getCell(i).getStringCellValue();
                if (StringUtils.equals(n1, n2)) {
                    //一样合并
                    sheet.addMergedRegion(new CellRangeAddress(1, 2, i, i));
                }
            }

            //------ 写入数据
            final List<List<SalaryCell>> cells = preview.getSlipTable();
            //排序
            final List<List<SalaryCell>> table = sortRows(cells);

            final List<SalarySlip> slips = preview.getSlips();
            final Map<String, SalarySlip> slipMap = slips.stream().collect(Collectors.toMap(SalarySlip::getJobNumber, i -> i));
//            final ArrayNode jsonNodes = convertJsonObject(table, preview.getSlips(), usMap);
//            写入excel
            String dir= "F:\\sig\\";
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
                if (userSig != null && slip.isForceCheck()) {
                    insertImage(dir + userSig.getFileName(), workbook, sheet, row, col,r + 3);
                }
                col = col + 1;
                //副总
                slip.setDmJobNumber("020");
                slip.setDmTime(LocalDateTime.now());
                if (StringUtils.isNotBlank(slip.getDmJobNumber()) && slip.getDmTime() != null) {
                    UserSignature dmSig = usMap.getOrDefault(slip.getDmJobNumber(), empty);
                    insertImage(dir + dmSig.getFileName(), workbook, sheet, row, col,r + 3);
                }
                col = col + 1;
                //人事
                slip.setHrJobNumber("077");
                slip.setHrTime(LocalDateTime.now());
                if (StringUtils.isNotBlank(slip.getHrJobNumber()) && slip.getHrTime() != null) {
                    UserSignature hrSig = usMap.getOrDefault(slip.getHrJobNumber(), empty);
                    insertImage(dir + hrSig.getFileName(), workbook, sheet, row, col,r + 3);
                }

                col = col + 1;
                //总经理
                slip.setCeoTime(LocalDateTime.now());
                slip.setCeoJobNumber("002");
                if (StringUtils.isNotBlank(slip.getCeoJobNumber()) && slip.getCeoTime() != null) {
                    UserSignature ceoSig = usMap.getOrDefault(slip.getCeoJobNumber(), empty);
                    insertImage(dir + ceoSig.getFileName(), workbook, sheet, row, col,r + 3);
                }

            }
            //---- 设置列宽
            //序号，姓名，工号
            sheet.setColumnWidth(0, 4 * 256);
            sheet.setColumnWidth(1, 6 * 256);
            sheet.setColumnWidth(2, 4 * 256);
            //部门
            sheet.setColumnWidth(3, 12 * 256);
            //岗位
            sheet.setColumnWidth(4, 14 * 256);
            //其他数据(不含图片)
            for (int c = 5; c < colMax; c++) {
                sheet.setColumnWidth(c, 8 * 256);
            }


            //保存excel
//            workbook.write(new FileOutputStream("C:\\Users\\Administrator\\Desktop\\poi_test.xlsx"));

            // 锁定结构（防止添加/删除 sheet）
//            sheet.protectSheet("readonly123");
//            workbook.lockStructure();
            workbook.setWorkbookPassword("readonly123", null); // 设置保护密码

            // 输出文件
            try (FileOutputStream fos = new FileOutputStream("C:\\Users\\Administrator\\Desktop\\poi_test.xlsx")) {
                workbook.write(fos);
            }

//            System.out.println("生成只读Excel完成！");
        }

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


    CellStyle lockCellStyle(Workbook workbook) {
        CellStyle lockedCellStyle = workbook.createCellStyle();
        lockedCellStyle.setLocked(true);
        return lockedCellStyle;
    }

    //转为json table
    ArrayNode convertJsonObject(List<List<SalaryCell>> list, List<SalarySlip> slips, Map<String, UserSignature> signatureMap) throws JsonProcessingException {
        Map<String,  SalarySlip> slipMap = slips.stream().collect(Collectors.toMap(SalarySlip::getJobNumber, s -> s));
        // Flatten 所有 Cell
        List<SalaryCell> allCells = list.stream()
                .flatMap(List::stream)
                .toList();
        // 按 rowIdx 分组
        Map<Integer, List<SalaryCell>> rowMap = allCells.stream()
                .collect(Collectors.groupingBy(SalaryCell::getRowIndex));
        // 排序 rowIdx
        List<Integer> sortedRowKeys = new ArrayList<>(rowMap.keySet());
        Collections.sort(sortedRowKeys);
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode rowsArray = mapper.createArrayNode();
        for (Integer rowIdx : sortedRowKeys) {
            List<SalaryCell> rowCells = rowMap.get(rowIdx);
            // 列排序
            rowCells.sort(Comparator.comparingInt(SalaryCell::getColumnIndex));

            ObjectNode rowNode = mapper.createObjectNode();
            for (SalaryCell cell : rowCells) {
                rowNode.put(cell.getLabel(), cell.getValue());
            }
            //签名
            //个人
            String jobNumber = rowNode.get("工号").asText();
            SalarySlip slip = slipMap.get(jobNumber);
            UserSignature userSig = signatureMap.get(jobNumber);
            if (userSig != null && slip.isForceCheck()) {
                rowNode.put("个人签名", userSig.getFileName());
            }
            //副总
            slip.setDmJobNumber("020");
            slip.setDmTime(LocalDateTime.now());
            if (StringUtils.isNotBlank(slip.getDmJobNumber()) && slip.getDmTime() != null) {
                UserSignature dmSig = signatureMap.get(slip.getDmJobNumber());
                rowNode.put("副总审核", dmSig.getFileName());
            }
            slip.setCeoTime(LocalDateTime.now());
            slip.setCeoJobNumber("002");
            //总经理
            if (StringUtils.isNotBlank(slip.getCeoJobNumber()) && slip.getCeoTime() != null) {
                UserSignature ceoSig = signatureMap.get(slip.getCeoJobNumber());
                rowNode.put("总经理审核", ceoSig.getFileName());
            }
            slip.setHrJobNumber("077");
            slip.setHrTime(LocalDateTime.now());
            //人事
            if (StringUtils.isNotBlank(slip.getHrJobNumber()) && slip.getHrTime() != null) {
                UserSignature hrSig = signatureMap.get(slip.getHrJobNumber());
                rowNode.put("人事审核", hrSig.getFileName());
            }

            rowsArray.add(rowNode);
        }
        return rowsArray;

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


    SalaryPreview getData() {
        CheckAuth checkAuth = new CheckAuth();
        checkAuth.setRole(CheckAuth.SL_CHK_HR);
        checkAuth.setViewAuth(CheckAuth.SL_CHK_HR);
        checkAuth.setJobNumber("112");
        final SalaryMain main = salaryService.findSalaryMainById("202503251742884518489");
        return salaryService.getSalaryCheckTable(checkAuth, main);
    }

//    List<SalaryHeader> buildHeader1(String mid) {
//        final List<SalaryHeader> header = salaryHeaderRepository.findByMidOrderByStartRowAscStartColumnAsc(mid);
//        final List<SalaryHeader> header1 = header.stream().filter(i -> i.getStartRow() == 1).toList();
//        final List<SalaryHeader> header2 = header.stream().filter(i -> i.getStartRow() == 2).toList();
//        final Map<Integer, List<SalaryHeader>> h2Map = header2.stream().collect(Collectors.groupingBy(SalaryHeader::getStartColumn, Collectors.toList()));
//        for(SalaryHeader h1 : header1) {
//            final List<SalaryHeader> h = h2Map.get(h1.getStartColumn());
//            if (h != null && !h.isEmpty()) {
//                h1.addChildren(h);
//            }
//        }
//        return header1;
//    }

}
