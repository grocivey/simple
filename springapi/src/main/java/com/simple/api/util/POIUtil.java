package com.simple.api.util;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class POIUtil {


    public static int getIntFromColor(int Red, int Green, int Blue){
        Red = (Red << 16) & 0x00FF0000;
        Green = (Green << 8) & 0x0000FF00;
        Blue = Blue & 0x000000FF;
        return 0xFF000000 | Red | Green | Blue;
    }

    /**
     * int转byte[]
     */
    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        result[0] = (byte)((i >> 24) & 0xFF);
        result[1] = (byte)((i >> 16) & 0xFF);
        result[2] = (byte)((i >> 8) & 0xFF);
        result[3] = (byte)(i & 0xFF);
        return result;
    }

    /**
     * 设置单元格样式
     *
     * @author gaozhiyuan
     * @date 2019/6/5 15:19
     */
    public static CellStyle getStyle(Workbook workbook) {
        XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();
        //自定义颜色对象
        XSSFColor color = new XSSFColor();
//根据你需要的rgb值获取byte数组
        color.setRGB(intToByteArray(getIntFromColor(255,255,255)));
        style.setFillForegroundColor(color);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        // 生成另一个字体
        Font font = workbook.createFont();
        font.setBold(true);
        return style;
    }

    /**
     * 设置表头单元格样式
     *
     * @author gaozhiyuan
     * @date 2019/6/5 15:20
     */
    public static CellStyle getColStyle(Workbook workbook) {
        // 生成一个样式
        XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();//自定义颜色对象
        XSSFColor color = new XSSFColor();
//根据你需要的rgb值获取byte数组
        color.setRGB(intToByteArray(getIntFromColor(255,255,255)));
        style.setFillForegroundColor(color);
        // 设置这些样式
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        // 生成一个字体
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);
        // 把字体应用到当前的样式
        style.setFont(font);
        return style;

    }

    /**
     * 获取excel第一列的内容
     *
     * @param file  excel
     * @param getExtensionName 文件扩展名
     * @return List
     * @throws IOException
     * @author gzy
     */
    public static List<String> getFirstColumnFromExcel(MultipartFile file, String getExtensionName) throws IOException {
        List<String> fsuIds = new ArrayList<>();
        Workbook workBook = null;
        if ("xls".equals(getExtensionName)) {
            POIFSFileSystem fs = new POIFSFileSystem(file.getInputStream());
            workBook = new HSSFWorkbook(fs);
        } else {
            workBook = new XSSFWorkbook(file.getInputStream());
        }
        Sheet sheet = workBook.getSheetAt(0);
        int rows = sheet.getLastRowNum() + 1;
        for (int r = 1; r < rows; r++) {
            Row row = sheet.getRow(r);
            if (row == null) {
                continue;
            }
            String fsuId = POIUtil.getStringVal(row.getCell(0));
            if (!StringUtils.isEmpty(fsuId)) {
                fsuIds.add(fsuId);
            }
        }
        return fsuIds;
    }

    /**
     * 将数据都转为string
     *
     * @author gzy
     */
    public static String getStringVal(Cell cell) {
        String cellValue = "";
        if (null != cell) {
            //以下是判断数据类型
            switch (cell.getCellType()) {
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) { //判断是否为日期类型
                        Date date = cell.getDateCellValue();
                        DateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
                        cellValue = formater.format(date);
                    } else {
                        DecimalFormat df = new DecimalFormat("####.####");
                        cellValue = df.format(cell.getNumericCellValue());
                    }
                    break;
                case STRING:
                    cellValue = cell.getStringCellValue();
                    break;
                case BOOLEAN:
                    cellValue = cell.getBooleanCellValue() + "";
                    break;
                case BLANK: //空值
                    cellValue = "";
                    break;
                case ERROR:
                    cellValue = "";
                    break;
                default:
                    cellValue = "";
            }
        }
        return replaceBlank(cellValue);
    }

    /**
     * 替换一些制表符等
     *
     * @author gzy
     */
    public static String replaceBlank(String str) {

        return StringUtils.trim(str).replaceAll("[\\r\\n\\t]", "");
    }

    private POIUtil() {
    }
}
