package com.abt.salary.service.impl;

import com.abt.salary.entity.SalaryCell;
import com.abt.salary.entity.SalaryHeader;
import com.abt.salary.entity.SalaryMain;
import com.abt.salary.model.CheckAuth;
import com.abt.salary.model.SalaryPreview;
import com.abt.salary.repository.SalaryHeaderRepository;
import com.abt.salary.service.SalaryService;
import com.abt.wf.entity.UserSignature;
import com.abt.wf.repository.UserSignatureRepository;
import com.aspose.cells.Cell;
import com.aspose.cells.Color;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.aspose.cells.*;
import org.junit.jupiter.api.Test;
import org.quartz.Job;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
class SalaryServiceImplTest {

    @Autowired
    private SalaryService salaryService;
    @Autowired
    private UserSignatureRepository userSignatureRepository;

    @Autowired
    private SalaryHeaderRepository salaryHeaderRepository;
    @Autowired
    private Job job;

    @Test
    void testPreview() {
//        final SalaryPreview salaryPreview = salaryService.extractAndValidate("C:\\Users\\Administrator\\Desktop\\salary_test.xlsx", "slm1");
    }

    @Test
    void testFind() {
    }

    @Test
    void testJob() throws SchedulerException {
    }

    SalaryPreview getData() {
        CheckAuth checkAuth = new CheckAuth();
        checkAuth.setRole(CheckAuth.SL_CHK_HR);
        checkAuth.setViewAuth(CheckAuth.SL_CHK_HR);
        checkAuth.setJobNumber("112");
        final SalaryMain main = salaryService.findSalaryMainById("202503251742884518489");
        return salaryService.getSalaryCheckTable(checkAuth, main);
    }

    //一级表头
    List<SalaryHeader> buildHeader1(String mid) {
        final List<SalaryHeader> header = salaryHeaderRepository.findByMidOrderByStartRowAscStartColumnAsc(mid);
        final List<SalaryHeader> header1 = header.stream().filter(i -> i.getStartRow() == 1).toList();
        final List<SalaryHeader> header2 = header.stream().filter(i -> i.getStartRow() == 2).toList();
        final Map<Integer, List<SalaryHeader>> h2Map = header2.stream().collect(Collectors.groupingBy(SalaryHeader::getStartColumn, Collectors.toList()));
        for(SalaryHeader h1 : header1) {
            final List<SalaryHeader> h = h2Map.get(h1.getStartColumn());
            if (h != null && !h.isEmpty()) {
                h1.addChildren(h);
            }
        }
        return header1;
    }

    List<SalaryHeader> buildSignatureHeader(int startCol) {
        List<SalaryHeader> header = new ArrayList<>();
        SalaryHeader h1 = new SalaryHeader();
        h1.setStartRow(1);
        h1.setStartColumn(startCol + 1);
        h1.setName("个人签名");
        h1.addChildren(List.of());

        return header;
    }

    Map<String, UserSignature> getSignatureMap() {
        final List<UserSignature> list = userSignatureRepository.findAllUserSignatures();
        return list.stream().collect(Collectors.toMap(UserSignature::getJobNumber, us -> us));
    }

    @Test
    void createExcel() throws Exception {
        final SalaryPreview preview = getData();

        final List<SalaryHeader> header1 = buildHeader1(preview.getSalaryMain().getId());
        //添加签名header
        Workbook workbook = new Workbook();
        Worksheet worksheet = workbook.getWorksheets().get(0);

//        // 设置A3纸张大小和横向
//        worksheet.getPageSetup().setPaperSize(PaperSizeType.PAPER_A_3);
//        worksheet.getPageSetup().setOrientation(PageOrientationType.LANDSCAPE);
//
//        // 设置页边距（单位：英寸）
//        worksheet.getPageSetup().setLeftMargin(0.2);
//        worksheet.getPageSetup().setRightMargin(0.2);
//        worksheet.getPageSetup().setTopMargin(0.2);
//        worksheet.getPageSetup().setBottomMargin(0.2);
//
//        // 设置适应一页宽度
//        worksheet.getPageSetup().setFitToPagesWide(1);

        // 添加标题
        Cell titleCell = worksheet.getCells().get(0, 0);
        titleCell.setValue("XXXX工资工资表");
        Style titleStyle = titleCell.getStyle();
        titleStyle.getFont().setBold(true);
        titleStyle.getFont().setSize(24);
        titleStyle.setHorizontalAlignment(TextAlignmentType.CENTER);
        titleCell.setStyle(titleStyle);
        
        // 计算二级标题的总列数
        int totalColumns = 0;
        for (SalaryHeader h1 : header1) {
            if (h1.getChildren() != null && !h1.getChildren().isEmpty()) {
                totalColumns += h1.getChildren().size();
            } else {
                totalColumns += 1;
            }
        }
        
        // 添加签名列的数量
        int signatureColumnsCount = 4; // 个人签名、副总签名、人事签名、总经理签名
        totalColumns += signatureColumnsCount;
        
        // 合并标题单元格
        if (totalColumns > 1) {
            worksheet.getCells().merge(0, 0, 1, totalColumns);
        }

        // 创建表头样式
        Style headerStyle = workbook.createStyle();
        headerStyle.setHorizontalAlignment(TextAlignmentType.CENTER);
        headerStyle.setBackgroundColor(Color.getLightGray());
        headerStyle.setBorder(BorderType.TOP_BORDER, CellBorderType.THIN, Color.getLightGray());
        headerStyle.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getLightGray());
        headerStyle.setBorder(BorderType.LEFT_BORDER, CellBorderType.THIN, Color.getLightGray());
        headerStyle.setBorder(BorderType.RIGHT_BORDER, CellBorderType.THIN, Color.getLightGray());
        
        // 添加表头
        int colIndex = 0;

        //签名
        final Map<String, UserSignature> sigMap = getSignatureMap();

        // 按照header1的顺序添加表头，排除"负责人签字"和"是否确认"列
        for (SalaryHeader h1 : header1) {
            // 跳过"负责人签字"和"是否确认"列
            if ("负责人签字".equals(h1.getName()) || "是否确认".equals(h1.getName())) {
                continue;
            }
            
            // 设置一级表头
            Cell cell = worksheet.getCells().get(1, colIndex);
            cell.setValue(h1.getName());
            cell.setStyle(headerStyle);
            
            // 计算该表头占用的列数
            int colSpan = 1;
            if (h1.getChildren() != null && !h1.getChildren().isEmpty()) {
                // 过滤掉子表头中的"负责人签字"和"是否确认"
                List<SalaryHeader> filteredChildren = h1.getChildren().stream()
                    .filter(child -> !"负责人签字".equals(child.getName()) && !"是否确认".equals(child.getName()))
                    .toList();
                colSpan = filteredChildren.size();
                
                // 合并一级表头单元格
                if (colSpan > 1) {
                    worksheet.getCells().merge(1, colIndex, 1, colSpan);
                }
                
                // 添加二级表头
                for (SalaryHeader child : filteredChildren) {
                    Cell subCell = worksheet.getCells().get(2, colIndex);
                    subCell.setValue(child.getName());
                    subCell.setStyle(headerStyle);
                    
                    // 如果一级表头和二级表头名称相同，进行行合并
                    if (h1.getName().equals(child.getName())) {
                        worksheet.getCells().merge(1, colIndex, 2, 1);
                    }
                    
                    colIndex++;
                }
            } else {
                // 如果没有子表头，则一级表头占两行
                worksheet.getCells().merge(1, colIndex, 2, 1);
                colIndex++;
            }
        }
        
        // 在所有数据列之后添加签名列
        String[] signatureColumns = {"个人签名", "副总签名", "人事签名", "总经理签名"};

        for (String signName : signatureColumns) {
            Cell cell = worksheet.getCells().get(1, colIndex);
            cell.setValue(signName);
            cell.setStyle(headerStyle);
            
            // 表头合并行
            worksheet.getCells().merge(1, colIndex, 2, 1);
            colIndex++;
        }

        // 添加数据并排序
        List<List<SalaryCell>> dataRows = preview.getSlipTable();
        // dataRows.sort(Comparator.comparing(row -> row.stream()
        //         .filter(cell -> "序号".equals(cell.getName()))
        //         .findFirst()
        //         .map(SalaryCell::getValue)
        //         .orElse("")));

        //插入数据
        for (int rowIndex = 0; rowIndex < dataRows.size(); rowIndex++) {
            List<SalaryCell> row = dataRows.get(rowIndex);
            int dataCellIndex = 0;
            int ri = rowIndex + 3;
            //设置行高
            System.out.println("row: " + rowIndex);
            worksheet.getCells().setRowHeightPixel(rowIndex, 30);

            for (int cellIndex = 0; cellIndex < row.size(); cellIndex++) {
                SalaryCell salaryCell = row.get(cellIndex);
                
                // 跳过"负责人签字"和"是否确认"列
                if ("负责人签字".equals(salaryCell.getName()) || "是否确认".equals(salaryCell.getName())) {
                    continue;
                }
                
                Cell cell = worksheet.getCells().get(ri, dataCellIndex);
                cell.setValue(salaryCell.getValue());
                
                // 设置数据单元格边框
                Style style = cell.getStyle();
                style.setBorder(BorderType.TOP_BORDER, CellBorderType.THIN, Color.getLightGray());
                style.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getLightGray());
                style.setBorder(BorderType.LEFT_BORDER, CellBorderType.THIN, Color.getLightGray());
                style.setBorder(BorderType.RIGHT_BORDER, CellBorderType.THIN, Color.getLightGray());
                cell.setStyle(style);
                
                dataCellIndex++;
            }
            
            // 添加签名单元格
            for (int i = 0; i < signatureColumns.length; i++) {
                int ci = dataCellIndex + i;
                Cell signCell = worksheet.getCells().get(ri, ci);

                //设置图片单元格列宽
                worksheet.getCells().setColumnWidthPixel(rowIndex, 105);
                
                // 设置单元格边框和样式
                Style style = signCell.getStyle();
                style.setBorder(BorderType.TOP_BORDER, CellBorderType.THIN, Color.getLightGray());
                style.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getLightGray());
                style.setBorder(BorderType.LEFT_BORDER, CellBorderType.THIN, Color.getLightGray());
                style.setBorder(BorderType.RIGHT_BORDER, CellBorderType.THIN, Color.getLightGray());
                signCell.setStyle(style);

                //{"个人签名", "副总签名", "人事签名", "总经理签名"};
                
                // 为第一列（个人签名）添加签名图片
                if (i == 0) {
                    // 获取员工工号
                    String jobNumber = "";
                    final Optional<SalaryCell> op = row.stream().filter(cell -> "工号".equals(cell.getLabel())).findFirst();
                    if (op.isPresent()) {
                        jobNumber = op.get().getValue();
                    }

                    if (!jobNumber.isEmpty()) {
                        final UserSignature signature = sigMap.get(jobNumber);
                        if (signature != null) {
                            final String fileName = signature.getFileName();
                            //拼接地址
                            String path = "F:\\sig\\" + fileName;
                            File file = new File(path);
                            if (file.exists()) {
                                System.out.println("sig fileName: " + fileName);
                                int picIdx = worksheet.getPictures().add(ri, ci, path);
                                Picture picture = worksheet.getPictures().get(picIdx);
                                picture.setPlacement(PlacementType.MOVE_AND_SIZE); // 图片跟随单元格移动和调整
                                picture.setHeight(worksheet.getCells().getRowHeightPixel(rowIndex));  // 设置高度
                                picture.setWidth(worksheet.getCells().getColumnWidthPixel(colIndex)); // 设置宽度
                            }
                        }
                    }
                }
            }
        }

        // 自动调整列宽
//        worksheet.autoFitColumns();

        // 保存文件
        workbook.save("C:\\Users\\Administrator\\Desktop\\salary_table.xlsx", SaveFormat.XLSX);
    }

//    void createPdf() {
//        CheckAuth checkAuth = new CheckAuth();
//        checkAuth.setRole(CheckAuth.SL_CHK_HR);
//        checkAuth.setViewAuth(CheckAuth.SL_CHK_HR);
//        checkAuth.setJobNumber("112");
//        final SalaryMain main = salaryService.findSalaryMainById("202503191742366140031");
//        final SalaryPreview preview = salaryService.getSalaryCheckTable(checkAuth, main);
//
//        //生成Pdf
//        // Initialize document object
//        Document document = new Document();
//
//        //Add page
//        Page page = document.getPages().add();
////        final PageSize a3 = PageSize.getA3();
//
//        // 创建表格对象
//        Table table = new Table();
//        table.setBorder(new BorderInfo(BorderSide.All, 1F)); // 设置表格边框
//        table.setDefaultCellBorder(new BorderInfo(BorderSide.All, 0.5F)); // 设置单元格边框
//        table.setDefaultCellPadding(new MarginInfo(5, 5, 5, 5)); // 单元格内边距
//        table.setBroken(TableBroken.Vertical); // 允许分页
//        //字体
//        String fontPath = "C:\\Windows\\Fonts\\simhei.ttf"; // 这里指定字体文件路径
//        Font font = FontRepository.openFont(fontPath); // 加载字体
////        // 获取文本状态并设置字体
//        TextState textState = new TextState();
//        textState.setFont(font);  // 设置为自定义字体
//        textState.setFontSize(12); // 设置字体大小
//
//        // 多级添加表头
//        //返回的是二级表头, parentLabel是一级表头
//        final List<SalaryHeader> header = preview.getHeader();
//        //设置table的宽度
//        System.out.println("Header size: " + header.size());
//        String columnWidths = "100 ".repeat(header.size()).trim();
//        table.setColumnWidths(columnWidths);
//
//        final Map<String, List<SalaryHeader>> h1Map = header.stream().collect(Collectors.groupingBy(SalaryHeader::getParentLabel, Collectors.toList()));
//        //一级表头
//        Row headerRow1 = table.getRows().add();
//        //二级表头
//        Row headerRow2 = table.getRows().add();
//        int maxLen = 0;
//
//
//        for (Map.Entry<String, List<SalaryHeader>> entry : h1Map.entrySet()) {
//            System.out.printf("header1 key: %s: \n", entry.getKey());
//            Cell cell1 = headerRow1.getCells().add(entry.getKey());
////            cell1.setDefaultCellTextState(textState);
//            String k = entry.getKey();
//            List<SalaryHeader> v = entry.getValue();
//            cell1.setColSpan(v.size()); // 合并列
//            //是否合并行，只有一个子节点，而且列名一样
////            if (v.size() == 1 && k.equals(v.get(0).getName())) {
////                cell1.setRowSpan(2);
////            }
//            cell1.setBackgroundColor(Color.getLightGray());
//            if (!v.isEmpty()) {
//                maxLen = Math.max(maxLen, v.size());
//                for (SalaryHeader h2 : v) {
//                    headerRow2.getCells().add(h2.getName());
//                }
//            }
//        }
//
//        //A3横向
//        page.getPageInfo().setWidth(1191.0);  // 宽度 1191 pt
//        page.getPageInfo().setHeight(842.0);  // 高度 842 pt
//        //页面边距
//        page.getPageInfo().getMargin().setLeft(5);
//        page.getPageInfo().getMargin().setRight(5);
//        page.getPageInfo().getMargin().setTop(5);
//        page.getPageInfo().getMargin().setBottom(5);
//
//        page.getParagraphs().add(new TextFragment("Salary Table!"));
//        page.getParagraphs().add(table);
//
//
//        PdfSaveOptions saveOptions = new PdfSaveOptions();
//        document.save("C:\\Users\\Administrator\\Desktop\\sl_test.pdf", saveOptions);
//    }



}