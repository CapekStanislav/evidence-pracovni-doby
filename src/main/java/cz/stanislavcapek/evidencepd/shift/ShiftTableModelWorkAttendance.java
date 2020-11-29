package cz.stanislavcapek.evidencepd.shift;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import cz.stanislavcapek.evidencepd.workattendance.WorkAttendance;
import cz.stanislavcapek.evidencepd.model.Month;
import cz.stanislavcapek.evidencepd.model.WorkingTimeFund;
import cz.stanislavcapek.evidencepd.shift.servants.ShiftTimeAdjuster;
import cz.stanislavcapek.evidencepd.employee.Employee;
import cz.stanislavcapek.evidencepd.utils.Rounder;
import lombok.ToString;

import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;
import java.util.Map;

/**
 * Instance třídy {@code TabulkaSmen}
 *
 * @author Stanislav Čapek
 */
@JsonIgnoreProperties({"rowCount", "columnCount", "tableModelListeners"})
@ToString
public class ShiftTableModelWorkAttendance extends AbstractTableModel implements WorkAttendance {


    private enum Time {
        START,
        END
    }

    private final Rounder rounder = new Rounder() {
    };

    private final String[] columnNames = {"den", "od", "do", "typ", "odpr. hodin",
            "noční", "víkend", "svátek", "dovolená", "neodpracované hodiny"};
    private WorkAttendance workAttendance;
    private final Map<Integer, Shift> shifts;

    public ShiftTableModelWorkAttendance(WorkAttendance workAttendance) {
        this.workAttendance = workAttendance;
        this.shifts = workAttendance.getShifts();
    }

    @Override
    public int getRowCount() {
        return workAttendance.getShifts().size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        final Shift shift = shifts.get(++rowIndex);
        if (shift.getTypeOfShiftTwelveHours() == TypeOfShiftTwelveHours.NONE && columnIndex != 0) {
            return "";
        }
        switch (columnIndex) {
            // day
            case 0:
                return String.valueOf(rowIndex);
            // start
            case 1:
                return getTime(shift, Time.START);
            // end
            case 2:
                return getTime(shift, Time.END);
            // type
            case 3:
                return shift.getTypeOfShiftTwelveHours().toString();
            // worked out hours
            case 4:
                final double workedOut = shift.getWorkingHours().getWorkedOut();
                return workedOut == 0 ? "" : workedOut;
            // night hours
            case 5:
                double night = shift.getPremiumPayments().getNight();
                return night == 0 ? "" : round(night);
            // weekend hours
            case 6:
                final double weekend = shift.getPremiumPayments().getWeekend();
                return weekend == 0 ? "" : round(weekend);
            // holiday hours
            case 7:
                final double holiday = shift.getPremiumPayments().getHoliday();
                return holiday == 0 ? "" : round(holiday);
            // work holiday
            case 8:
                double workHoliday = shift.getWorkingHours().getHoliday();
                return workHoliday == 0 ? "" : round(workHoliday);
            // not worked out hours
            case 9:
                final double notWorkedOut = shift.getWorkingHours().getNotWorkedOut();
                return notWorkedOut == 0 ? "" : round(notWorkedOut);
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        int day = rowIndex + 1;
        final Shift shift = shifts.get(day);
        final LocalDate datum = LocalDate.of(workAttendance.getYear(), workAttendance.getMonth().getNumber(), day);

        switch (columnIndex) {
            case 1:
            case 2:
                if (columnIndex == 1) {
                    ShiftTimeAdjuster.adjustTime(shift, aValue, ShiftTimeAdjuster.TimeType.START);
                } else {
                    ShiftTimeAdjuster.adjustTime(shift, aValue, ShiftTimeAdjuster.TimeType.END);
                }
                break;

            case 3:
                if (!(aValue instanceof TypeOfShiftTwelveHours)) {
                    throw new IllegalStateException("Typ směny musí být třídy TypeOfShiftTwelveHours");
                }
                final DefaultShiftFactory shiftFactory = new DefaultShiftFactory();
                shiftFactory.setPeriod(datum);
                final Shift newShift = shiftFactory.createShift(datum, ((TypeOfShiftTwelveHours) aValue));
                shifts.replace(day, newShift);
                break;
        }
        fireTableDataChanged();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 1 || columnIndex == 2 || columnIndex == 3;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Employee getEmployee() {
        return workAttendance.getEmployee();
    }

    @Override
    public Month getMonth() {
        return workAttendance.getMonth();
    }

    @Override
    public int getYear() {
        return workAttendance.getYear();
    }

    @Override
    public WorkingTimeFund.TypeOfWeeklyWorkingTime getTypeOfWeeklyWorkingTime() {
        return workAttendance.getTypeOfWeeklyWorkingTime();
    }

    @Override
    public double getLastMonth() {
        return workAttendance.getLastMonth();
    }

    @Override
    public Map<Integer, Shift> getShifts() {
        return workAttendance.getShifts();
    }

    private String getTime(Shift shift, Time timeTyp) {
        if (shift.getTypeOfShiftTwelveHours() == TypeOfShiftTwelveHours.NONE) {
            return "";
        }
        switch (timeTyp) {
            case START:
                return shift.getStart().toLocalTime().toString();
            case END:
                return shift.getEnd().toLocalTime().toString();
            default:
                return "invalid time";
        }
    }

    private double round(double toRound) {
        return rounder.getRoundedDouble(toRound, 2);
    }

}
