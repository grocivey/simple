package com.simple.api.util;

import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * 导出报表excel
 *
 * @author gaozhiyuan
 */
@Slf4j
public class DeriveExcelReport {

    //导出线程池
    static ThreadPoolExecutor pool = new ThreadPoolExecutor(1, 20, 30, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000),
            new DefaultThreadFactory("excel导出pool"));//默认拒绝策略

    @SneakyThrows
    public static boolean exportExcel(String title, String sheetName, List<List<String>> list, HttpServletResponse response) {
        // 导出表格(支持大量数据导出)
        Workbook workbook = new SXSSFWorkbook(100); // 在内存中保留100行，超过的行将刷新到磁盘
        long start = System.currentTimeMillis();
        boolean result = executeFast(workbook, sheetName, list);
        long end = System.currentTimeMillis();
        log.info("导出excel，workbook数据处理耗时（ms）：{}", (end - start));
        if (!result) {
            return result;
        }
        // 文件名
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(title + ".xlsx", "UTF-8"));
        long startOut = System.currentTimeMillis();
        write(workbook, response);
        long endOut = System.currentTimeMillis();
        log.info("导出excel写入输出流耗时（ms）：{}", (endOut - startOut));
        return result;
    }

    public static InputStream getExcelInputStream(String sheetName, List<List<String>> list) throws IOException {
        // 导出表格
        Workbook workbook = new SXSSFWorkbook(100); // 在内存中保留100行，超过的行将刷新到磁盘
        boolean result = execute(workbook, sheetName, list);
        if (!result) {
            return null;
        }
        // Fill an empty output stream
        ByteArrayOutputStream ouputStream = new ByteArrayOutputStream();
        workbook.write(ouputStream);
        // Create the input stream (do not forget to close the inputStream after use)
        return new ByteArrayInputStream(ouputStream.toByteArray());
    }

    static boolean execute(Workbook workbook, String sheetName, List<List<String>> list) {
        int size = list.size();
        //10万一个sheet
        int meta = 100000;
        //组数 线程数
        int groupCount = (size / meta) + 1;
        boolean result = true;
        for (int index = 0; index < groupCount; index++) {
            //声明一个工作簿,生成一个表格
            Sheet sheet = workbook.createSheet(sheetName + "-" + index);
            sheet.setDefaultColumnWidth(15);
            // workbook.setSheetName(0, title); 多个sheet

            //标题的样式
            CellStyle colStyle = POIUtil.getColStyle(workbook);
            // 普通单元格样式
            CellStyle style = POIUtil.getStyle(workbook);
            // 行
            Row row = sheet.createRow(0);
            Cell cell;
            // 表的标题
            if (!list.isEmpty()) {
                for (int i = 0; i < list.get(0).size(); i++) {
                    cell = row.createCell(i);
                    cell.setCellStyle(colStyle);
                    cell.setCellValue(list.get(0).get(i));
                }
            } else {
                return false;
            }

            int groupIndex = index * meta;
            int startIndex = groupIndex + 1;
            //每个sheet的范围
            int limit = (index == groupCount - 1) ? list.size() : (startIndex + meta);

            // 表内容
            for (int i = startIndex; i < limit; i++) {
                row = sheet.createRow(i - groupIndex);
                for (int j = 0; j < list.get(i).size(); j++) {
                    cell = row.createCell(j);
                    cell.setCellStyle(style);
                    cell.setCellValue(list.get(i).get(j));
                }
            }
        }
        return result;
    }

    static boolean executeFast(Workbook workbook, String sheetName, List<List<String>> list) throws InterruptedException {
        final boolean[] result = {true};
        int size = list.size();
        //20万一个sheet
        int meta = 200000;
        //组数 线程数
        int groupCount = (size / meta) + 1;
        List<Sheet> sheetList = new ArrayList<>();
        for (int i = 0; i < groupCount; i++) {
            sheetList.add(workbook.createSheet(sheetName + "-" + (i + 1)));
        }
        //标题的样式
        CellStyle colStyle = POIUtil.getColStyle(workbook);
        // 普通单元格样式
        CellStyle style = POIUtil.getStyle(workbook);
        CountDownLatch countDownLatch = new CountDownLatch(groupCount);
        for (int index = 0; index < groupCount; index++) {
            int finalIndex = index;
            pool.execute(() -> {
                //声明一个工作簿,生成一个表格
                Sheet sheet = sheetList.get(finalIndex);
                sheet.setDefaultColumnWidth(15);
                // 行
                Row row = sheet.createRow(0);
                Cell cell;
                // 表的标题
                if (!list.isEmpty()) {
                    for (int i = 0; i < list.get(0).size(); i++) {
                        cell = row.createCell(i);
                        cell.setCellStyle(colStyle);
                        cell.setCellValue(list.get(0).get(i));
                    }
                } else {
                    result[0] = false;
                }

                int groupIndex = finalIndex * meta;
                int startIndex = groupIndex + 1;
                //每个sheet的范围
                int limit = (finalIndex == groupCount - 1) ? list.size() : (startIndex + meta);

//                // 表内容
                for (int i = startIndex; i < limit; i++) {
                    row = sheet.createRow(i - groupIndex);
                    for (int j = 0; j < list.get(i).size(); j++) {
                        cell = row.createCell(j);
                        cell.setCellStyle(style);
                        String setValue = list.get(i).get(j);
                        //尝试转型为double，这边后期需要优化，不能所有值都尝试转型，很浪费时间和性能，要明确知道哪一列或哪一行需要转型
                        try {
                            double d = Double.parseDouble(setValue);
                            cell.setCellValue(d);
                        } catch (Exception e) {
                            cell.setCellValue(setValue);
                        }

                    }
                }
                countDownLatch.countDown();
            });

        }
        countDownLatch.await();
        return result[0];
    }

    /**
     * 导出
     *
     * @author gaozhiyuan
     * @date 2019/6/6 10:57
     */
    public static void write(Workbook workbook, HttpServletResponse response) {
        try {
            OutputStream ouputStream = response.getOutputStream();
            workbook.write(ouputStream);
            ouputStream.flush();
            ouputStream.close();
            workbook.close();
        } catch (IOException e) {
            log.error("", e);
        }
    }


    private DeriveExcelReport() {
    }
}
