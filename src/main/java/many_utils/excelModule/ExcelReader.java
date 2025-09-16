package many_utils.excelModule;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ExcelReader {

    public static void main(String[] args) {
        String xlsxFilePath = "/Users/chang/Desktop/test_excel.xlsx";
//        String xlsFilePath = "sample.xls";

        System.out.println("Reading XLSX file with merged cells...");
        readExcel(xlsxFilePath);

//        System.out.println("\nReading XLS file with merged cells...");
//        readExcel(xlsFilePath);
    }

    public static void readExcel(String filePath) {
        try (FileInputStream fis = new FileInputStream(new File(filePath))) {
            Workbook workbook = WorkbookFactory.create(fis);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                for (Cell cell : row) {
                    String cellValue = getCellValue(sheet, cell);
                    System.out.print(cellValue + "\t");
                }
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the value of a cell, handling merged regions.
     *
     * @param sheet The worksheet.
     * @param cell  The cell to get the value for.
     * @return The cell's value as a string.
     */
    private static String getCellValue(Sheet sheet, Cell cell) {
        if (isMergedCell(sheet, cell)) {
            return getMergedRegionValue(sheet, cell);
        } else {
            return getFormattedCellValue(cell);
        }
    }

    /**
     * Checks if a cell is part of a merged region.
     *
     * @param sheet The worksheet.
     * @param cell  The cell to check.
     * @return True if the cell is in a merged region, false otherwise.
     */
    private static boolean isMergedCell(Sheet sheet, Cell cell) {
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress mergedRegion = sheet.getMergedRegion(i);
            if (mergedRegion.isInRange(cell)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds and returns the value of the top-left cell of a merged region.
     *
     * @param sheet The worksheet.
     * @param cell  A cell within the merged region.
     * @return The value of the top-left cell.
     */
    private static String getMergedRegionValue(Sheet sheet, Cell cell) {
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress mergedRegion = sheet.getMergedRegion(i);
            if (mergedRegion.isInRange(cell)) {
                Row firstRow = sheet.getRow(mergedRegion.getFirstRow());
                Cell firstCell = firstRow.getCell(mergedRegion.getFirstColumn());
                return getFormattedCellValue(firstCell);
            }
        }
        return "";
    }

    /**
     * Gets the formatted value of a non-merged cell.
     *
     * @param cell The cell to get the value from.
     * @return The formatted cell value.
     */
    private static String getFormattedCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellTypeEnum()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (IllegalStateException e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BLANK:
                return "";
            default:
                return cell.toString();
        }
    }
}