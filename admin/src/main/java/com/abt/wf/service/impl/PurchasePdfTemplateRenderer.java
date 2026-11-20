package com.abt.wf.service.impl;

import com.abt.sys.exception.BusinessException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.state.RenderingMode;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.Units;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * 仅渲染采购现有两种模板：读取列宽、字体大小、对齐、合并单元格、边框和签名图片。
 * 纵向明细自动分页，重复前三行；汇总与签名尾部整体保留，超长明细按行内文字续页。
 * 不承担通用 Excel 打印引擎职责，也不调用 Aspose 或任何 PDF 合并工具。
 */
final class PurchasePdfTemplateRenderer {
    private static final int HEADER_ROWS = 3;
    private static final float PADDING = 3;
    private static final Pattern WRAP_TOKENS = Pattern.compile("(?:[A-Za-z0-9μµ./%_-]+|.)[，。、；：！？）》】]*", Pattern.DOTALL);
    private final PDDocument document;
    private final PDType0Font font;
    private final XSSFWorkbook workbook;
    private final XSSFSheet sheet;
    private final DataFormatter formatter = new DataFormatter(Locale.CHINA);
    private final List<CellRangeAddress> merges;
    private final boolean acceptance;
    private final float[] widths;
    private final float[] heights;
    private final float left;
    private final float top;
    private final float bottom;
    private final PDRectangle pageSize;
    private PDPageContentStream stream;
    private float y;
    private float bodyTop;

    PurchasePdfTemplateRenderer(PDDocument document, PDType0Font font, XSSFWorkbook workbook, boolean acceptance)
            throws IOException {
        this.document = document;
        this.font = font;
        this.workbook = workbook;
        this.sheet = workbook.getSheetAt(0);
        this.acceptance = acceptance;
        this.merges = sheet.getMergedRegions();
        pageSize = sheet.getPrintSetup().getLandscape()
                ? new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()) : PDRectangle.A4;
        left = (float) sheet.getMargin(org.apache.poi.ss.usermodel.PageMargin.LEFT) * 72;
        top = (float) sheet.getMargin(org.apache.poi.ss.usermodel.PageMargin.TOP) * 72;
        bottom = (float) sheet.getMargin(org.apache.poi.ss.usermodel.PageMargin.BOTTOM) * 72;
        // 仓库存在六列与七列采购模板，列数以表头为准，不能截掉最右侧用途、合计等字段。
        int columns = sheet.getRow(HEADER_ROWS - 1).getLastCellNum();
        widths = new float[columns];
        float total = 0;
        for (int i = 0; i < columns; i++) total += sheet.getColumnWidth(i);
        float available = pageSize.getWidth() - left
                - (float) sheet.getMargin(org.apache.poi.ss.usermodel.PageMargin.RIGHT) * 72;
        for (int i = 0; i < columns; i++) widths[i] = available * sheet.getColumnWidth(i) / total;
        heights = new float[sheet.getLastRowNum() + 1];
        for (int i = 0; i < heights.length; i++) heights[i] = rowHeight(i);
    }

    void render() throws IOException {
        try {
            newPage();
            int footerStart = sheet.getLastRowNum() - (acceptance ? 2 : 3);
            float footerHeight = 0;
            for (int row = footerStart; row < heights.length; row++) footerHeight += heights[row];
            for (int row = HEADER_ROWS; row < footerStart; row++) {
                // 最后一条明细与签名尾部能放入一页时一起换页，避免出现仅有表头和签名的尾页。
                if (row == footerStart - 1 && heights[row] + footerHeight > y - bottom
                        && heights[row] + footerHeight <= bodyTop - bottom) newPage();
                renderDetail(row);
            }
            if (footerHeight > y - bottom) newPage();
            if (footerHeight > y - bottom) {
                throw new BusinessException("采购 PDF 模板签名及说明区域超出一页，请联系管理员检查模板");
            }
            for (int row = footerStart; row < heights.length; row++) {
                drawRow(row, 0, Integer.MAX_VALUE, heights[row]);
            }
        } finally {
            if (stream != null) stream.close();
        }
    }

    private void newPage() throws IOException {
        if (stream != null) {
            if (y < bodyTop) {
                float tableWidth = 0;
                for (float width : widths) tableWidth += width;
                line(left, y, left + tableWidth, y);
            }
            stream.close();
        }
        PDPage page = new PDPage(pageSize);
        document.addPage(page);
        stream = new PDPageContentStream(document, page);
        stream.setLineWidth(0.6f);
        y = pageSize.getHeight() - top;
        for (int row = 0; row < HEADER_ROWS; row++) drawRow(row, 0, Integer.MAX_VALUE, heights[row]);
        bodyTop = y;
    }

    /** 普通明细整行换页，超过单页可用高度的单条明细才分段，避免截断或死循环。 */
    private void renderDetail(int rowIndex) throws IOException {
        if (heights[rowIndex] <= y - bottom) {
            drawRow(rowIndex, 0, Integer.MAX_VALUE, heights[rowIndex]);
            return;
        }
        if (y < bodyTop - 0.01f) newPage();
        if (heights[rowIndex] <= y - bottom) {
            drawRow(rowIndex, 0, Integer.MAX_VALUE, heights[rowIndex]);
            return;
        }
        int offset = 0;
        int totalLines = 1;
        float leading = 1;
        for (int col = 0; col < widths.length; col++) {
            Cell cell = cell(rowIndex, col);
            if (covered(rowIndex, col)) continue;
            totalLines = Math.max(totalLines, lines(cell, cellWidth(rowIndex, col)).size());
            leading = Math.max(leading, fontSize(cell) * 1.2f);
        }
        while (offset < totalLines) {
            int capacity = (int) ((y - bottom - PADDING * 2) / leading);
            if (capacity < 1) throw new BusinessException("采购 PDF 模板没有可用的明细打印空间");
            int count = Math.min(capacity, totalLines - offset);
            drawRow(rowIndex, offset, count, count * leading + PADDING * 2);
            offset += count;
            if (offset < totalLines) newPage();
        }
    }

    private float rowHeight(int index) throws IOException {
        Row row = sheet.getRow(index);
        float height = row == null ? sheet.getDefaultRowHeightInPoints() : row.getHeightInPoints();
        for (int col = 0; col < widths.length; col++) {
            if (covered(index, col)) continue;
            Cell cell = cell(index, col);
            height = Math.max(height, lines(cell, cellWidth(index, col)).size() * fontSize(cell) * 1.2f + PADDING * 2);
        }
        return height;
    }

    private void drawRow(int row, int offset, int limit, float height) throws IOException {
        float x = left;
        for (int col = 0; col < widths.length; col++) {
            if (!covered(row, col)) {
                Cell cell = cell(row, col);
                float width = cellWidth(row, col);
                drawText(cell, x, y, width, height, offset, limit);
                borders(row, col, x, y, width, height);
            }
            x += widths[col];
        }
        if (offset == 0) drawPictures(row, y, height);
        y -= height;
    }

    private void drawText(Cell cell, float x, float rowTop, float width, float height, int offset, int limit)
            throws IOException {
        List<String> all = lines(cell, width);
        int end = (int) Math.min(all.size(), (long) offset + limit);
        if (offset >= end) return;
        float size = fontSize(cell);
        float leading = size * 1.2f;
        CellStyle style = cell == null ? null : cell.getCellStyle();
        VerticalAlignment vertical = style == null ? VerticalAlignment.CENTER : style.getVerticalAlignment();
        float blockHeight = (end - offset) * leading;
        float inset = vertical == VerticalAlignment.TOP ? PADDING
                : vertical == VerticalAlignment.BOTTOM ? height - blockHeight - PADDING : (height - blockHeight) / 2;
        float baseline = rowTop - Math.max(PADDING, inset) - size;
        HorizontalAlignment align = style == null ? HorizontalAlignment.LEFT : style.getAlignment();
        if (align == HorizontalAlignment.GENERAL && cell != null && cell.getCellType() == CellType.NUMERIC) {
            align = HorizontalAlignment.RIGHT;
        }
        boolean bold = cell != null && workbook.getFontAt(style.getFontIndexAsInt()).getBold();
        for (int i = offset; i < end; i++) {
            String line = all.get(i);
            float textWidth = font.getStringWidth(line) * size / 1000;
            float tx = align == HorizontalAlignment.CENTER ? x + (width - textWidth) / 2
                    : align == HorizontalAlignment.RIGHT ? x + width - PADDING - textWidth : x + PADDING;
            stream.beginText();
            stream.setFont(font, size);
            stream.setRenderingMode(bold ? RenderingMode.FILL_STROKE : RenderingMode.FILL);
            stream.setLineWidth(bold ? 0.15f : 0.6f);
            stream.newLineAtOffset(tx, baseline);
            stream.showText(line);
            stream.endText();
            baseline -= leading;
        }
        stream.setLineWidth(0.6f);
    }

    private void borders(int row, int col, float x, float rowTop, float width, float height) throws IOException {
        Cell first = cell(row, col);
        CellStyle start = first == null ? null : first.getCellStyle();
        int lastColumn = col;
        CellRangeAddress merged = merge(row, col);
        if (merged != null) lastColumn = merged.getLastColumn();
        Cell last = cell(row, lastColumn);
        CellStyle end = last == null ? start : last.getCellStyle();
        if (start == null) return;
        if (start.getBorderTop() != BorderStyle.NONE) line(x, rowTop, x + width, rowTop);
        if (start.getBorderBottom() != BorderStyle.NONE) line(x, rowTop - height, x + width, rowTop - height);
        if (start.getBorderLeft() != BorderStyle.NONE) line(x, rowTop, x, rowTop - height);
        if (end != null && end.getBorderRight() != BorderStyle.NONE) line(x + width, rowTop, x + width, rowTop - height);
    }

    private void line(float x1, float y1, float x2, float y2) throws IOException {
        stream.moveTo(x1, y1);
        stream.lineTo(x2, y2);
        stream.stroke();
    }

    private void drawPictures(int row, float rowTop, float height) throws IOException {
        var drawing = sheet.getDrawingPatriarch();
        if (drawing == null) return;
        for (var shape : drawing.getShapes()) {
            if (!(shape instanceof XSSFPicture picture)) continue;
            var anchor = picture.getClientAnchor();
            if (anchor == null || anchor.getRow1() != row || anchor.getCol1() >= widths.length) continue;
            float x = left;
            for (int i = 0; i < anchor.getCol1(); i++) x += widths[i];
            float width = cellWidth(row, anchor.getCol1());
            float inset = Math.max(PADDING, (float) anchor.getDx1() / Units.EMU_PER_POINT);
            float availableWidth = Math.max(1, width - inset - PADDING);
            float availableHeight = Math.max(1, height - PADDING * 2);
            PDImageXObject image = PDImageXObject.createFromByteArray(document, picture.getPictureData().getData(), "signature");
            float ratio = Math.min(availableWidth / image.getWidth(), availableHeight / image.getHeight());
            float imageWidth = image.getWidth() * ratio;
            float imageHeight = image.getHeight() * ratio;
            stream.drawImage(image, x + inset, rowTop - (height + imageHeight) / 2, imageWidth, imageHeight);
        }
    }

    /** 按 Unicode 码点换行，保留换行符；无法编码的字符明确失败，不静默丢失业务内容。 */
    private List<String> lines(Cell cell, float width) throws IOException {
        String value = cell == null ? "" : formatter.formatCellValue(cell);
        if (value.isEmpty()) return List.of();
        List<String> result = new ArrayList<>();
        float size = fontSize(cell);
        for (String paragraph : value.replace("\r\n", "\n").replace('\r', '\n').split("\n", -1)) {
            StringBuilder line = new StringBuilder();
            float used = 0;
            var tokens = WRAP_TOKENS.matcher(paragraph);
            while (tokens.find()) {
                String token = tokens.group();
                // 数字、型号等能完整放入一行时整体换行，避免把 1000 拆成 100 和 0。
                float tokenWidth = 0;
                for (int cp : token.codePoints().toArray()) tokenWidth += characterWidth(cp, size);
                if (!line.isEmpty() && used + tokenWidth > width - PADDING * 2
                        && tokenWidth <= width - PADDING * 2) {
                    result.add(line.toString()); line.setLength(0); used = 0;
                }
                for (int cp : token.codePoints().toArray()) {
                    if (Character.isISOControl(cp) && cp != '\t') continue;
                    String character = cp == '\t' ? " " : new String(Character.toChars(cp));
                    float advance = characterWidth(cp, size);
                    if (used + advance > width - PADDING * 2 && !line.isEmpty()) {
                        result.add(line.toString());
                        line.setLength(0);
                        used = 0;
                    }
                    line.append(character);
                    used += advance;
                }
            }
            result.add(line.toString());
        }
        return result;
    }

    private float characterWidth(int cp, float size) throws IOException {
        if (Character.isISOControl(cp) && cp != '\t') return 0;
        String character = cp == '\t' ? " " : new String(Character.toChars(cp));
        try {
            return font.getStringWidth(character) * size / 1000;
        } catch (IllegalArgumentException e) {
            throw new BusinessException("采购 PDF 字体不支持字符 U+" + Integer.toHexString(cp).toUpperCase(Locale.ROOT)
                    + "，请联系管理员配置完整中文字体");
        }
    }

    private float fontSize(Cell cell) {
        if (cell == null) return 10.5f;
        Font cellFont = workbook.getFontAt(cell.getCellStyle().getFontIndexAsInt());
        return cellFont.getFontHeight() / 20f;
    }

    private Cell cell(int row, int col) {
        Row value = sheet.getRow(row);
        return value == null ? null : value.getCell(col);
    }

    private CellRangeAddress merge(int row, int col) {
        return merges.stream().filter(range -> range.isInRange(row, col)).findFirst().orElse(null);
    }

    private boolean covered(int row, int col) {
        CellRangeAddress range = merge(row, col);
        return range != null && (range.getFirstColumn() != col || range.getFirstRow() != row);
    }

    private float cellWidth(int row, int col) {
        CellRangeAddress range = merge(row, col);
        int end = range == null ? col : Math.min(widths.length - 1, range.getLastColumn());
        float width = 0;
        for (int i = col; i <= end; i++) width += widths[i];
        return width;
    }
}
