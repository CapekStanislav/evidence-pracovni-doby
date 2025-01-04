package cz.stanislavcapek.evidencepd.service.shiftplan;

import com.google.inject.Inject;
import cz.stanislavcapek.evidencepd.domain.employee.Employee;
import cz.stanislavcapek.evidencepd.domain.shiftplan.Month;
import cz.stanislavcapek.evidencepd.domain.shiftplan.ShiftPlanModel;
import cz.stanislavcapek.evidencepd.model.WorkingTimeFund;
import cz.stanislavcapek.evidencepd.service.workattendance.WorkAttendance;
import cz.stanislavcapek.evidencepd.service.workattendance.WorkAttendanceFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.*;

import static org.apache.poi.ss.usermodel.CellType.FORMULA;
import static org.apache.poi.ss.usermodel.CellType.NUMERIC;

public class ShiftPlanFactory {
    private final WorkAttendanceFactory workAttendanceFactory;

    @Inject
    public ShiftPlanFactory(WorkAttendanceFactory workAttendanceFactory) {
        this.workAttendanceFactory = workAttendanceFactory;
    }

    ShiftPlan create(XSSFWorkbook workbook, WorkingTimeFund.TypeOfWeeklyWorkingTime typeOfWeeklyWorkingTime) {
        return loadAllPlan(workbook);
    }

    private ShiftPlan loadAllPlan(XSSFWorkbook workbook) {
        Map<Month, Map<Integer, WorkAttendance>> shiftsInYear = new TreeMap<>();
        Set<Integer> employeeIds = new TreeSet<>();
        int year = getYear(workbook);

        final Iterator<Sheet> sheetIterator = workbook.sheetIterator();

        while (sheetIterator.hasNext()) {
            final Sheet sheet = sheetIterator.next();
            final Month month = Month.getByName(sheet.getSheetName());

            int[] ids = getEmployeeIdByMonth(sheet);
            Map<Integer, WorkAttendance> byMonth = new TreeMap<>();
            final int numOfEmployees = getNumberOfEmployees(sheet);
            for (int j = 0; j < numOfEmployees; j++) {
                int id = ids[j];
                employeeIds.add(id);
                ShiftsByMonth shiftsByMonth = new ShiftsByMonth(
                        getWholeRowByEmployee(sheet, id),
                        month,
                        getEmployee(sheet, id),
                        year
                );
                WorkAttendance workAttendance = workAttendanceFactory.create(shiftsByMonth);
                byMonth.put(id, workAttendance);
            }
            shiftsInYear.put(month, byMonth);
        }

        return new ShiftPlanModel(year, shiftsInYear);
    }

    private int[] getEmployeeIdByMonth(Sheet sheet) {
        final int numberOfEmployees = getNumberOfEmployees(sheet);
        final int[] ids = new int[numberOfEmployees];

        for (int i = 0; i < numberOfEmployees; i++) {
            Row row = sheet.getRow(2 + i);
            Cell cell = row.getCell(2);
            ids[i] = (int) cell.getNumericCellValue();
        }
        return ids;
    }

    private int getNumberOfEmployees(Sheet sheet) {
        int count = 0;
        int rowIndex = 2;
        final Row sheetRow = sheet.getRow(rowIndex);
        Cell cell = null;
        if (sheetRow != null) {
            cell = sheetRow.getCell(2);
        }

        while (cell != null && cell.getNumericCellValue() > 0) {
            count++;
            rowIndex++;
            final Row row = sheet.getRow(rowIndex);
            if (row != null) {
                cell = row.getCell(2);
            } else {
                break;
            }
        }
        return count;
    }

    private String getEmployeeName(Sheet sheet, int id) {
        String name = "";
        int row = getEmployeeRow(sheet, id);

        if (row != 0) {
            Cell cell = sheet.getRow(row).getCell(0);
            if (cell != null) {
                name = cell.getStringCellValue();
            }
        }

        return name;
    }

    private List<String> getWholeRowByEmployee(Sheet sheet, int id) throws IllegalArgumentException {
        List<String> wholeRow;

        int employeeRow = getEmployeeRow(sheet, id);

        Row row = sheet.getRow(employeeRow);
        DataFormatter formatter = new DataFormatter();

        List<String> finalWholeRow = new ArrayList<>();
        row.forEach(cell -> {
            if (cell.getCellType() == FORMULA) {
                if (cell.getCachedFormulaResultType() == NUMERIC) {
                    finalWholeRow.add(cell.getNumericCellValue() + "");
                }
            } else {
                String cellValue = formatter.formatCellValue(cell);
                finalWholeRow.add(cellValue);
            }
        });
        wholeRow = finalWholeRow;

        return wholeRow;
    }

    private Employee getEmployee(Sheet sheet, int id) {
        final String name = getEmployeeName(sheet, id);
        final String[] split = name.split(" ");

        return new Employee(
                id,
                split[0],
                split[1]
        );
    }

    private int getEmployeeRow(Sheet sheet, int id) {
        final int[] i = {0};
        final int[] employeeRow = {0};

        sheet.forEach(row -> {
            Cell cell = row.getCell(2);
            if (cell != null) {
                if (cell.getCellType() == NUMERIC) {
                    if ((int) cell.getNumericCellValue() == id) {
                        employeeRow[0] = i[0];
                    }
                }
            }
            i[0]++;
        });
        return employeeRow[0];
    }

    private int getYear(XSSFWorkbook workbook) {
        XSSFCell yearCell = workbook.getSheetAt(0).getRow(0).getCell(0);
        if (yearCell != null) {
            if (yearCell.getCellType() == NUMERIC) {
                return (int) yearCell.getNumericCellValue();
            }
        }
        return 0;
    }
}
