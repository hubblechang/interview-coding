package many_utils.excelModule;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * 使用 Apache POI 读取 Excel 文件，支持处理合并单元格
 */
public class ExcelPOIReaderUtil {

    /**
     * 读取 Excel 文件为字符串列表，自动处理合并单元格
     *
     * @param filePath Excel 文件路径
     * @param sheetIndex 工作表索引（从0开始）
     * @return 解析后的字符串数据列表
     * @throws IOException 文件读取异常
     */
    public static List<List<String>> readExcelWithMerge(String filePath, int sheetIndex) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        Workbook workbook = null;

        try {
            // 根据文件扩展名选择对应的工作簿实现
            if (filePath.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(fis);
            } else if (filePath.endsWith(".xls")) {
                workbook = new HSSFWorkbook(fis);
            } else {
                throw new IllegalArgumentException("不支持的文件格式");
            }

            Sheet sheet = workbook.getSheetAt(sheetIndex);
            return readSheetWithMerge(sheet);
        } finally {
            if (workbook != null) {
                workbook.close();
            }
            fis.close();
        }
    }

    /**
     * 读取工作表数据，处理合并单元格
     *
     * @param sheet 工作表对象
     * @return 解析后的字符串数据列表
     */
    public static List<List<String>> readSheetWithMerge(Sheet sheet) {
        List<List<String>> result = new ArrayList<>();

        // 获取合并区域信息
        List<CellRangeAddress> mergedRegions = sheet.getMergedRegions();

        // 确定数据范围
        int firstRow = sheet.getFirstRowNum();
        int lastRow = sheet.getLastRowNum();

        // 创建二维数组存储数据
        Map<String, String> mergedData = new HashMap<>();

        // 先处理合并单元格，记录合并区域的值
        for (CellRangeAddress mergedRegion : mergedRegions) {
            int firstRowNum = mergedRegion.getFirstRow();
            int lastRowNum = mergedRegion.getLastRow();
            int firstColNum = mergedRegion.getFirstColumn();
            int lastColNum = mergedRegion.getLastColumn();

            // 获取合并区域第一个单元格的值
            Row firstRowData = sheet.getRow(firstRowNum);
            String value = "";
            if (firstRowData != null) {
                Cell firstCell = firstRowData.getCell(firstColNum);
                value = getCellValueAsString(firstCell);
            }

            // 记录合并区域所有单元格的值
            for (int i = firstRowNum; i <= lastRowNum; i++) {
                for (int j = firstColNum; j <= lastColNum; j++) {
                    mergedData.put(i + "," + j, value);
                }
            }
        }

        // 读取所有行数据
        for (int i = firstRow; i <= lastRow; i++) {
            Row row = sheet.getRow(i);
            List<String> rowData = new ArrayList<>();

            if (row != null) {
                // 确定行的最大列数
                int firstCellNum = row.getFirstCellNum();
                int lastCellNum = row.getLastCellNum();

                if (firstCellNum >= 0 && lastCellNum >= 0) {
                    for (int j = firstCellNum; j < lastCellNum; j++) {
                        String key = i + "," + j;
                        if (mergedData.containsKey(key)) {
                            // 使用合并单元格的值
                            rowData.add(mergedData.get(key));
                        } else {
                            // 普通单元格的值
                            Cell cell = row.getCell(j);
                            rowData.add(getCellValueAsString(cell));
                        }
                    }
                }
            }
            result.add(rowData);
        }

        return result;
    }

    /**
     * 获取单元格的字符串值
     *
     * @param cell 单元格对象
     * @return 单元格的字符串值
     */
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // 避免科学计数法显示
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return String.valueOf(cell.getNumericCellValue());
                } catch (Exception e) {
                    return cell.getStringCellValue();
                }
            case BLANK:
                return "";
            default:
                return "";
        }
    }

    /**
     * 读取 Excel 文件的所有工作表
     *
     * @param filePath Excel 文件路径
     * @return 所有工作表的数据，key为工作表名称，value为数据
     * @throws IOException 文件读取异常
     */
    public static Map<String, List<List<String>>> readAllSheets(String filePath) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        Workbook workbook = null;

        try {
            if (filePath.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(fis);
            } else if (filePath.endsWith(".xls")) {
                workbook = new HSSFWorkbook(fis);
            } else {
                throw new IllegalArgumentException("不支持的文件格式");
            }

            Map<String, List<List<String>>> allSheetsData = new HashMap<>();

            int numberOfSheets = workbook.getNumberOfSheets();
            for (int i = 0; i < numberOfSheets; i++) {
                Sheet sheet = workbook.getSheetAt(i);
                String sheetName = sheet.getSheetName();
                allSheetsData.put(sheetName, readSheetWithMerge(sheet));
            }

            return allSheetsData;
        } finally {
            if (workbook != null) {
                workbook.close();
            }
            fis.close();
        }
    }

    public static void main(String[] args) {
        try {
            // 读取第一个工作表
            List<List<String>> data = ExcelPOIReaderUtil.readExcelWithMerge(
                    "/Users/chang/Desktop/test_excel.xlsx",  // 文件路径
                    0                       // 第一个工作表
            );

            // 打印结果
            for (List<String> row : data) {
                System.out.println(row);
            }

            // 读取所有工作表
            Map<String, List<List<String>>> allData = ExcelPOIReaderUtil.readAllSheets(
                    "/Users/chang/Desktop/test_excel.xlsx"   // 文件路径
            );

            // 遍历所有工作表数据
            for (Map.Entry<String, List<List<String>>> entry : allData.entrySet()) {
                System.out.println("工作表: " + entry.getKey());
                for (List<String> row : entry.getValue()) {
                    System.out.println(row);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

/*

*/
/**
 * 使用 Apache POI 4.1.2 读取 Excel 文件，支持处理合并单元格
 *//*

public class ExcelPOIReaderUtil {

    */
/**
     * 读取 Excel 文件为字符串列表，自动处理合并单元格
     *
     * @param filePath Excel 文件路径
     * @param sheetIndex 工作表索引（从0开始）
     * @return 解析后的字符串数据列表
     * @throws IOException 文件读取异常
     *//*

    public static List<List<String>> readExcelWithMerge(String filePath, int sheetIndex) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        Workbook workbook = null;

        try {
            // 根据文件扩展名选择对应的工作簿实现
            if (filePath.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(fis);
            } else if (filePath.endsWith(".xls")) {
                workbook = new HSSFWorkbook(fis);
            } else {
                throw new IllegalArgumentException("不支持的文件格式");
            }

            Sheet sheet = workbook.getSheetAt(sheetIndex);
            return readSheetWithMerge(sheet);
        } finally {
            if (workbook != null) {
                workbook.close();
            }
            fis.close();
        }
    }

    */
/**
     * 读取工作表数据，处理合并单元格
     *
     * @param sheet 工作表对象
     * @return 解析后的字符串数据列表
     *//*

    public static List<List<String>> readSheetWithMerge(Sheet sheet) {
        List<List<String>> result = new ArrayList<>();

        // 获取合并区域信息
        List<CellRangeAddress> mergedRegions = sheet.getMergedRegions();

        // 确定数据范围
        int firstRow = sheet.getFirstRowNum();
        int lastRow = sheet.getLastRowNum();

        // 创建映射存储合并单元格的值
        Map<String, String> mergedData = new HashMap<>();

        // 先处理合并单元格，记录合并区域的值
        for (CellRangeAddress mergedRegion : mergedRegions) {
            int firstRowNum = mergedRegion.getFirstRow();
            int lastRowNum = mergedRegion.getLastRow();
            int firstColNum = mergedRegion.getFirstColumn();
            int lastColNum = mergedRegion.getLastColumn();

            // 获取合并区域第一个单元格的值
            Row firstRowData = sheet.getRow(firstRowNum);
            String value = "";
            if (firstRowData != null) {
                Cell firstCell = firstRowData.getCell(firstColNum);
                value = getCellValueAsString(firstCell);
            }

            // 记录合并区域所有单元格的值
            for (int i = firstRowNum; i <= lastRowNum; i++) {
                for (int j = firstColNum; j <= lastColNum; j++) {
                    mergedData.put(i + "," + j, value);
                }
            }
        }

        // 读取所有行数据
        for (int i = firstRow; i <= lastRow; i++) {
            Row row = sheet.getRow(i);
            List<String> rowData = new ArrayList<>();

            if (row != null) {
                // 确定行的最大列数
                int lastCellNum = row.getLastCellNum();

                if (lastCellNum >= 0) {
                    for (int j = 0; j < lastCellNum; j++) {
                        String key = i + "," + j;
                        if (mergedData.containsKey(key)) {
                            // 使用合并单元格的值
                            rowData.add(mergedData.get(key));
                        } else {
                            // 普通单元格的值
                            Cell cell = row.getCell(j);
                            rowData.add(getCellValueAsString(cell));
                        }
                    }
                }
            }
            result.add(rowData);
        }

        return result;
    }

    */
/**
     * 获取单元格的字符串值
     *
     * @param cell 单元格对象
     * @return 单元格的字符串值
     *//*

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    double numericValue = cell.getNumericCellValue();
                    // 检查是否为整数
                    if (numericValue == Math.floor(numericValue)) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return String.valueOf(cell.getNumericCellValue());
                } catch (Exception e) {
                    return cell.getStringCellValue();
                }
            case BLANK:
                return "";
            default:
                return "";
        }
    }

    */
/**
     * 读取 Excel 文件的所有工作表
     *
     * @param filePath Excel 文件路径
     * @return 所有工作表的数据，key为工作表名称，value为数据
     * @throws IOException 文件读取异常
     *//*

    public static Map<String, List<List<String>>> readAllSheets(String filePath) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        Workbook workbook = null;

        try {
            if (filePath.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(fis);
            } else if (filePath.endsWith(".xls")) {
                workbook = new HSSFWorkbook(fis);
            } else {
                throw new IllegalArgumentException("不支持的文件格式");
            }

            Map<String, List<List<String>>> allSheetsData = new HashMap<>();

            int numberOfSheets = workbook.getNumberOfSheets();
            for (int i = 0; i < numberOfSheets; i++) {
                Sheet sheet = workbook.getSheetAt(i);
                String sheetName = sheet.getSheetName();
                allSheetsData.put(sheetName, readSheetWithMerge(sheet));
            }

            return allSheetsData;
        } finally {
            if (workbook != null) {
                workbook.close();
            }
            fis.close();
        }
    }

    */
/**
     * 读取指定工作表名称的数据
     *
     * @param filePath Excel 文件路径
     * @param sheetName 工作表名称
     * @return 解析后的字符串数据列表
     * @throws IOException 文件读取异常
     *//*

    public static List<List<String>> readExcelWithMerge(String filePath, String sheetName) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        Workbook workbook = null;

        try {
            if (filePath.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(fis);
            } else if (filePath.endsWith(".xls")) {
                workbook = new HSSFWorkbook(fis);
            } else {
                throw new IllegalArgumentException("不支持的文件格式");
            }

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalArgumentException("找不到工作表: " + sheetName);
            }

            return readSheetWithMerge(sheet);
        } finally {
            if (workbook != null) {
                workbook.close();
            }
            fis.close();
        }
    }

    public static void main(String[] args) {
        try {
            // 读取第一个工作表
            List<List<String>> data = ExcelPOIReaderUtil.readExcelWithMerge(
                    "/Users/chang/Desktop/test_excel.xlsx",  // 文件路径
                    0                       // 第一个工作表
            );

            // 打印结果
            for (List<String> row : data) {
                System.out.println(row);
            }

            // 读取所有工作表
            Map<String, List<List<String>>> allData = ExcelPOIReaderUtil.readAllSheets(
                    "/Users/chang/Desktop/test_excel.xlsx"   // 文件路径
            );

            // 遍历所有工作表数据
            for (Map.Entry<String, List<List<String>>> entry : allData.entrySet()) {
                System.out.println("工作表: " + entry.getKey());
                for (List<String> row : entry.getValue()) {
                    System.out.println(row);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
*/
