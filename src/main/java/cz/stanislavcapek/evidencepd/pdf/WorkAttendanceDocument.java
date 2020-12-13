package cz.stanislavcapek.evidencepd.pdf;

import cz.stanislavcapek.evidencepd.model.WorkingTimeFund;
import cz.stanislavcapek.evidencepd.workattendance.WorkAttendance;
import cz.stanislavcapek.evidencepd.shift.WorkingTime;
import cz.stanislavcapek.evidencepd.shift.Shift;

import javax.swing.table.TableModel;
import java.time.LocalDate;

/**
 * An instance of class {@code WorkAttendanceDocument}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public class WorkAttendanceDocument {

    private final TableModel model;
    private final WorkAttendance workAttendance;

    public <T extends TableModel> WorkAttendanceDocument(WorkAttendance workAttendance, T model) {
        this.workAttendance = workAttendance;
        this.model = model;
    }

    public String getName() {
        return workAttendance.getEmployee().getFullName();
    }

    public int getYear() {
        return workAttendance.getYear();
    }

    public String getMonth() {
        return workAttendance.getMonth().getName();
    }

    /**
     * @param rowIndex 0 base index
     * @return date of shift
     */
    public LocalDate getDate(int rowIndex) {
        final int day = rowIndex + 1;
        return workAttendance.getShifts().get(day).getStart().toLocalDate();
    }

    public int getColumnCount() {
        return model.getColumnCount();
    }

    public String getColumnName(int columnIndex) {
        return model.getColumnName(columnIndex);
    }

    public int getRowCount() {
        return model.getRowCount();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return model.getValueAt(rowIndex, columnIndex);
    }

    public double getWorkTimeFund() {
        return WorkingTimeFund.calculateWorkingTimeFund(
                getDate(0),
                workAttendance.getTypeOfWeeklyWorkingTime()
        );
    }

    public double getLastMonthHours() {
        return workAttendance.getLastMonth();
    }

    public double getNextMonthHours() {
        final double totalWorkedHours = getWorkedHours() + getHolidayHours() +
                getNotWorkedHours() + getLastMonthHours();
        return totalWorkedHours - getWorkTimeFund();
    }

    public double getWorkedHours() {
        return workAttendance.getShifts().values()
                .stream()
                .map(Shift::getWorkingHours)
                .mapToDouble(WorkingTime::getWorkedOut)
                .sum();

    }

    public double getNotWorkedHours() {
        return workAttendance.getShifts().values()
                .stream()
                .map(Shift::getWorkingHours)
                .mapToDouble(WorkingTime::getNotWorkedOut)
                .sum();
    }

    public double getHolidayHours() {
        return workAttendance.getShifts().values()
                .stream()
                .map(Shift::getWorkingHours)
                .mapToDouble(WorkingTime::getHoliday)
                .sum();
    }

}
