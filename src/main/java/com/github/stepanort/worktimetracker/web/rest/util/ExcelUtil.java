package com.github.stepanort.worktimetracker.web.rest.util;

import com.github.stepanort.worktimetracker.domain.Expense;
import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author stepan.ort
 */
public class ExcelUtil {

    public static byte[] generate(List<Expense> expenses) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet spreadsheet = workbook.createSheet("Expenses");
        Object[] objectArr = new Object[]{
            "id",
            "user",
            "name",
            "value",
            "receipt"
        };
        XSSFRow row = spreadsheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        fillRow(objectArr, row, style);
        for (int rowid = 0; rowid < expenses.size(); rowid++) {
            Expense expense = expenses.get(rowid);
            row = spreadsheet.createRow(rowid + 1);
            objectArr = new Object[]{
                String.valueOf(expense.getId()),
                expense.getUser().getLogin(),
                expense.getName(),
                String.valueOf(expense.getValue()),
                (expense.getReceiptContentType() != null ? "1" : "0")
            };
            fillRow(objectArr, row, null);
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            workbook.write(bos);
        } finally {
            bos.close();
        }
        return bos.toByteArray();
    }

    private static void fillRow(Object[] objectArr, XSSFRow row, CellStyle style) {
        int cellid = 0;
        for (Object obj : objectArr) {
            Cell cell = row.createCell(cellid++);
            cell.setCellValue((String) obj);
            if (style != null) {
                cell.setCellStyle(style);
            }
        }
    }
}
