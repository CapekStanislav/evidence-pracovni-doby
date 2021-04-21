package cz.stanislavcapek.evidencepd.shiftplan;

import cz.stanislavcapek.evidencepd.utils.Constraint;
import cz.stanislavcapek.evidencepd.workattendance.WorkAttendance;
import cz.stanislavcapek.evidencepd.model.Month;
import cz.stanislavcapek.evidencepd.model.WorkingTimeFund;
import cz.stanislavcapek.evidencepd.shift.Shift;
import cz.stanislavcapek.evidencepd.workattendance.DefaultWorkAttendance;
import cz.stanislavcapek.evidencepd.employee.Employee;
import cz.stanislavcapek.evidencepd.workattendance.exception.WorkAttendanceNotFoundException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.*;
import java.util.stream.Collectors;

import static org.apache.poi.ss.usermodel.CellType.FORMULA;
import static org.apache.poi.ss.usermodel.CellType.NUMERIC;

/**
 * An instance of class {@code ShiftPlan}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
@EqualsAndHashCode
@ToString
public class ShiftPlan {

    @ToString.Exclude
    private final XSSFWorkbook workbook;
    private final WorkingTimeFund.TypeOfWeeklyWorkingTime typeOfWeeklyWorkingTime;
    @ToString.Exclude
    private final Map<Integer, Map<Integer, WorkAttendance>> shiftsInYear = new TreeMap<>();
    private Set<Integer> employeeIds;
    private final Constraint<Integer> monthNumberConstraint = MonthNumberConstraint.getInstance();
    private final Constraint<Integer> workAttConstraint;
    private final Constraint<MonthAndIdValue> monthIdConstraint;
    private int year;

    /**
     * Map {@code <Měsíc, Map<ID,WorkAttendance>> }
     */

    public ShiftPlan(XSSFWorkbook workbook, WorkingTimeFund.TypeOfWeeklyWorkingTime typeOfWeeklyWorkingTime) {
        this.workbook = workbook;
        this.typeOfWeeklyWorkingTime = typeOfWeeklyWorkingTime;
        this.year = getYear();

        workAttConstraint = new Constraint<>(
                shiftsInYear::containsKey,
                WorkAttendanceNotFoundException::new
        );

        monthIdConstraint = new Constraint<>(
                monthAndId -> isEmployee(monthAndId.getId(), monthAndId.getMonth()),
                monthAndId -> {
                    final String s = String
                            .format("Zaměstnanec s id %d se nenachází v zadaném měsíci.", monthAndId.getId());
                    return new IllegalArgumentException(s);
                }
        );


        loadAllPlan();
    }

    /**
     * Metoda vrátí číslo roku v šabloně.
     *
     * @return číslo roku v šabloně, {@code nenalezeno} = 0
     */
    public int getYear() {
        XSSFCell yearCell = workbook.getSheetAt(0).getRow(0).getCell(0);
        if (yearCell != null) {
            if (yearCell.getCellType() == NUMERIC) {
                return (int) yearCell.getNumericCellValue();
            }
        }
        return 0;
    }

    /**
     * @param monthNum měsíc
     * @param id       id zaměstnance
     * @return evidence směn
     * @throws InvalidMonthNumberException     pokud hodnota měsíce není v rozmezí 1-12
     * @throws WorkAttendanceNotFoundException pokud se v plánu nenachází zadaný měsíc
     * @throws IllegalArgumentException        pokud se id zaměstnance nenachází v daném měsíci
     */
    public WorkAttendance getWorkAttendance(int monthNum, int id) {
        monthNumberConstraint.orThrow(monthNum);
        workAttConstraint.orThrow(monthNum);
        monthIdConstraint.orThrow(MonthAndIdValue.of(monthNum, id));
        return shiftsInYear.get(monthNum).get(id);
    }

    /**
     * @param monthNum měsíc
     * @return Mapu {@code <ID, WorkAttendance>}
     * @throws IllegalArgumentException pokud hodnota měsíce není v rozmezí 1-12
     */
    public Map<Integer, WorkAttendance> getWorkAttendanceByMonth(int monthNum) {
        monthNumberConstraint.orThrow(monthNum);
        workAttConstraint.orThrow(monthNum);
        return shiftsInYear.get(monthNum);
    }

    /**
     * @param monthNum měsíc
     * @param id       zaměstnance
     * @return seznam přesčasů
     * @throws InvalidMonthNumberException     pokud hodnota měsíce není v rozmezí 1-12
     * @throws WorkAttendanceNotFoundException pokud se v plánu nenachází zadaný měsíc
     * @throws IllegalArgumentException        pokud se id zaměstnance nenachází v daném měsíci
     */
    public WorkAttendance getWorkAttendanceOvertime(int monthNum, int id) {
        monthNumberConstraint.orThrow(monthNum);
        workAttConstraint.orThrow(monthNum);
        monthIdConstraint.orThrow(MonthAndIdValue.of(monthNum, id));

        List<Shift> overtimesByMonth;
        try {
            overtimesByMonth = new OvertimesByMonth(workbook, monthNum, id).getOvertimes();
        } catch (Exception e) {
            overtimesByMonth = new ArrayList<>();
        }
        return convertToWorkAttendance(overtimesByMonth, getYear(), monthNum, id);
    }

    /**
     * Zjistí, jestli se zaměstnanec nachází v celém plánu
     * směn.
     *
     * @param id označení zaměstnance
     * @return {@code true} zaměstnanec se nachází v plánu
     * {@code false} zaměstnanec se nenachází v plánu
     */
    public boolean isEmployee(int id) {
        return employeeIds.contains(id);
    }

    /**
     * Zjistí, jestli se zaměstnanec nachází v konkrétním měsíci.
     *
     * @param id       označení zaměstnance
     * @param monthNum měsíc ve kterém hledáme (1-12)
     * @return {@code true} zaměstnanec se nachází v měsíci
     * {@code false} zaměstnanec se nenachází v měsíci
     * @throws IllegalArgumentException měsíc je mimo požadovaný rozsah
     */
    public boolean isEmployee(int id, int monthNum) throws IllegalArgumentException {
        monthNumberConstraint.orThrow(monthNum);
        if (!shiftsInYear.containsKey(monthNum)) {
            return false;
        }
        return shiftsInYear.get(monthNum).containsKey(id);
    }

    /**
     * Pomocná metoda pro vytvoření zaměstnance dle jeho ID
     *
     * @param id zaměstnance
     * @return nová instance zaměstnance
     */
    public Employee getEmployee(int id) {
        final String name = getEmployeeName(id);
        final String[] split = name.split(" ");

        return new Employee(
                id,
                split[0],
                split[1]
        );
    }

    public Set<Integer> getEmployeeIds() {
        return employeeIds;
    }

    public Set<Integer> getAvailableMonths() {
        return shiftsInYear.keySet();
    }

    /**
     * Metoda validuje zadaný měsíc, který musí být v rozmezí 1 - 12
     *
     * @param num číslo měsíce
     * @return nachází se v požadovaném rozmezí
     */
    private boolean isValidMonth(int num) {
        return Month.isValidMonth(num);
    }

    /**
     * Vytvoří za jednotlivé měsíce v roce směny pro jednotlivé zaměstnance.
     * 1 měsíc -> (id, směny) * počet strážníků
     */
    private void loadAllPlan() {
        employeeIds = new TreeSet<>();

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

    private WorkAttendance convertToWorkAttendance(ShiftsByMonth shiftsByMonth) {
        Employee employee = shiftsByMonth.getEmployee();
        final Month month = Month.valueOf(shiftsByMonth.getMonth());
        Map<Integer, Shift> shifts = new TreeMap<>();

        shiftsByMonth.getShifts()
                .forEach(shift -> shifts.put(shift.getStart().getDayOfMonth(), shift));
        return new DefaultWorkAttendance(
                employee,
                month,
                shiftsByMonth.getYear(),
                typeOfWeeklyWorkingTime,
                shiftsByMonth.getFromLastMonth(),
                shifts
        );
    }

    private WorkAttendance convertToWorkAttendance(List<Shift> overtimes, int year, int month, int id) {
        final Month monthTyp = Month.valueOf(month);
        final Employee employee = getEmployee(id);

        final Map<Integer, Shift> shiftMap = overtimes.stream()
                .collect(Collectors.toMap(overtimes::indexOf, shift -> shift));

        final int lastMonth = 0;
        return new DefaultWorkAttendance(
                employee,
                monthTyp,
                year,
                typeOfWeeklyWorkingTime,
                lastMonth,
                shiftMap
        );
    }

    /**
     * Získá ID u zaměstnanců vygenerovaných v šabloně v zadaném měsíci.
     *
     * @param month měsíc ve kterém hledáme
     * @return seznam ID vygenerovaných strážníků
     */
    private int[] getEmployeeIdByMonth(int month) {
        final int numberOfEmployees = getNumberOfEmployees(month);
        final int[] ids = new int[numberOfEmployees];
        month = month - 1;

        XSSFSheet sheet = workbook.getSheetAt(month);
        for (int i = 0; i < numberOfEmployees; i++) {
            XSSFRow row = sheet.getRow(2 + i);
            XSSFCell cell = row.getCell(2);
            ids[i] = (int) cell.getNumericCellValue();
        }
        return ids;
    }

    /**
     * Pomocná metoda, která vrací počet vygenerovaných zaměstnanců v šabloně
     * v daný měsíc.
     *
     * @param month 1-12
     * @return počet zaměstnanců
     */
    private int getNumberOfEmployees(int month) {
        month = month - 1;

        XSSFSheet sheet = workbook.getSheetAt(month);

        int count = 0;
        int rowIndex = 2;
        final XSSFRow sheetRow = sheet.getRow(rowIndex);
        Cell cell = null;
        if (sheetRow != null) {
            cell = sheetRow.getCell(2);
        }

        while (cell != null && cell.getNumericCellValue() > 0) {
            count++;
            rowIndex++;
            final XSSFRow row = sheet.getRow(rowIndex);
            if (row != null) {
                cell = row.getCell(2);
            } else {
                break;
            }
        }
        return count;
    }

    /**
     * Vrátí celý řádek, dle zadaného ID zaměstnance a měsíce (1-12).
     *
     * @param id    číslo zaměstnance
     * @param month měsíc (list) ve kterém hledám (1-12)
     * @return pole s textovou reprezentací celého řádku
     * @throws IllegalArgumentException měsíc je mimo rozmezí
     */
    private List<String> getWholeRowByEmployee(int id, int month) throws IllegalArgumentException {
        List<String> wholeRow;

        monthNumberConstraint.orThrow(month);

        int employeeRow = getEmployeeRow(id);

        XSSFRow row = workbook.getSheetAt(month - 1).getRow(employeeRow);
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

    /**
     * Vrátí číslo řádku (0 based) ve kterém se nachází zaměstnanec, vyhledaný dle ID.
     *
     * @param id číslo zaměstnance, kterého hledám
     * @return celé číslo řádku, nenalezeno = 0
     */
    private int getEmployeeRow(int id) {
        final int[] i = {0};
        final int[] employeeRow = {0};

        XSSFSheet sheet = workbook.getSheetAt(0);
        sheet.forEach(row -> {
            XSSFCell cell = (XSSFCell) row.getCell(2);
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

    /**
     * Získá jméno zaměstnance vygenerovaného v šabloně
     *
     * @param id číslo zaměstnance
     * @return String jméno zaměstnance,  prázdný String, pokud není nalezen strážník s odpovídajícím služebním
     * číslem
     */
    private String getEmployeeName(int id) {
        String name = "";
        int row = getEmployeeRow(id);

        if (row != 0) {
            XSSFCell cell = workbook.getSheetAt(0).getRow(row).getCell(0);
            if (cell != null) {
                name = cell.getStringCellValue();
            }

        }
        return name;
    }

    @Value(staticConstructor = "of")
    private static class MonthAndIdValue {
        int month;
        int id;
    }


}
