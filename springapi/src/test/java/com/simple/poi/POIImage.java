package com.simple.poi;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.*;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTMarker;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class POIImage {
    /**
     * 获取excel表中的图片
     *
     * @return
     * @throws IOException
     * @throws EncryptedDocumentException
     * @Param fis 文件输入流
     * @Param sheetNum Excel表中的sheet编号
     */
    public static Map<String, PictureData> getPictureFromExcel(File file, int sheetNum) throws EncryptedDocumentException, IOException {

        //获取图片PictureData集合
        String fileName = file.getName();
        Workbook workbook = null;
        if (StringUtils.isEmpty(fileName)) {
            return null;
        }
        FileInputStream fileInputStream = new FileInputStream(file);
        if (fileName.endsWith("xls")) {
            //2003
            workbook = new HSSFWorkbook(fileInputStream);
            HSSFSheet sheet = (HSSFSheet) workbook.getSheetAt(sheetNum - 1);
            Map<String, PictureData> pictures = getPictures(sheet);
            return pictures;
        } else if (fileName.endsWith("xlsx")) {
            //2007
            workbook = new XSSFWorkbook(fileInputStream);
            XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(sheetNum - 1);
            Map<String, PictureData> pictures = getPictures(sheet);
            return pictures;
        }
        return new HashMap();
    }

    /**
     * 获取图片和位置 (xls版)
     * @param sheet
     * @return
     * @throws IOException
     */
    public static Map<String, PictureData> getPictures (HSSFSheet sheet) throws IOException {
        Map<String, PictureData> map = new HashMap<String, PictureData>();
        List<HSSFShape> list = sheet.getDrawingPatriarch().getChildren();
        for (HSSFShape shape : list) {
            if (shape instanceof HSSFPicture) {
                HSSFPicture picture = (HSSFPicture) shape;
                HSSFClientAnchor cAnchor = picture.getClientAnchor();
                PictureData pdata = picture.getPictureData();
                String key = cAnchor.getRow1() + "-" + cAnchor.getCol1(); // 行号-列号
                map.put(key, pdata);
            }
        }
        return map;
    }

    /**
     * 获取图片和位置 (xlsx版)
     * @param sheet
     * @return
     * @throws IOException
     */
    public static Map<String, PictureData> getPictures (XSSFSheet sheet) throws IOException {
        Map<String, PictureData> map = new HashMap<String, PictureData>();
        List<POIXMLDocumentPart> list = sheet.getRelations();
        for (POIXMLDocumentPart part : list) {
            if (part instanceof XSSFDrawing) {
                XSSFDrawing drawing = (XSSFDrawing) part;
                List<XSSFShape> shapes = drawing.getShapes();
                for (XSSFShape shape : shapes) {
                    XSSFPicture picture = (XSSFPicture) shape;
                    XSSFClientAnchor anchor = picture.getPreferredSize();
                    CTMarker marker = anchor.getFrom();
                    String key = marker.getRow() + "-" + marker.getCol();
                    map.put(key, picture.getPictureData());
                }
            }
        }
        return map;
    }

    public static void main(String [] args) throws IOException {
        File file = new File("C:\\Users\\gzy\\Desktop\\导入用户模板.xlsx");
        //获取图片数据
        Map<String, PictureData> map = getPictureFromExcel(file, 1);
        //根据图片行列位置获取图片
        PictureData picData = (PictureData)map.get("1-4");
        //后缀名
        String extension = picData.suggestFileExtension();
        byte[] data = picData.getData();
        FileOutputStream out = new FileOutputStream("D:/gzypicture." + extension);
        out.write(data);
        out.close();
    }



}
