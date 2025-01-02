package cz.stanislavcapek.evidencepd.service.shiftplan;

import com.google.inject.Inject;
import cz.stanislavcapek.evidencepd.model.Month;
import cz.stanislavcapek.evidencepd.model.WorkingTimeFund;
import cz.stanislavcapek.evidencepd.service.workattendance.WorkAttendance;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.*;

public class ShiftPlanFactory {
    @Inject
    public ShiftPlanFactory() {
    }

    ShiftPlan create(XSSFWorkbook workbook, WorkingTimeFund.TypeOfWeeklyWorkingTime typeOfWeeklyWorkingTime) {
        return null;
    }

    /**
     * Vytvoří za jednotlivé měsíce v roce směny pro jednotlivé zaměstnance.
     * 1 měsíc -> (id, směny) * počet strážníků
     */
    private void loadAllPlan() {
        Set<Integer> employeeIds = new TreeSet<>();

        final Iterator<Sheet> sheetIterator = workbook.sheetIterator();

        while (sheetIterator.hasNext()) {
            final Sheet sheet = sheetIterator.next();
            final int i = workbook.getSheetIndex(sheet) + 1;
            final int monthNumber = Month.getNumberByName(sheet.getSheetName());

            if (!Month.isValidMonth(monthNumber)) {
                continue;
            }

            int[] ids = getEmployeeIdByMonth(i);
            Map<Integer, WorkAttendance> byMonth = new TreeMap<>();
            final int numOfEmployees = getNumberOfEmployees(i);
            for (int j = 0; j < numOfEmployees; j++) {
                int id = ids[j];
                employeeIds.add(id);
                ShiftsByMonth shiftsByMonth = new ShiftsByMonth(
                        getWholeRowByEmployee(id, i),
                        monthNumber,
                        getEmployee(id),
                        this.year
                );
                WorkAttendance workAttendance = convertToWorkAttendance(shiftsByMonth);
                byMonth.put(id, workAttendance);
            }
            shiftsInYear.put(monthNumber, byMonth);

        }
    }
}
