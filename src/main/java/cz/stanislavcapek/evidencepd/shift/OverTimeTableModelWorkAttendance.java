package cz.stanislavcapek.evidencepd.shift;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import cz.stanislavcapek.evidencepd.workattendance.WorkAttendance;
import cz.stanislavcapek.evidencepd.model.WorkingTimeFund;
import cz.stanislavcapek.evidencepd.model.Month;
import cz.stanislavcapek.evidencepd.shift.servants.ShiftTimeAdjuster;
import cz.stanislavcapek.evidencepd.employee.Employee;
import cz.stanislavcapek.evidencepd.utils.Rounder;
import lombok.ToString;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Instance třídy {@code PrescasyTable}
 * <p>
 * vždy seřazeno dle datumu
 *
 * @author Stanislav Čapek
 */
@JsonIgnoreProperties({"rowCount", "columnCount", "tableModelListeners"})
@ToString
public class OverTimeTableModelWorkAttendance extends AbstractTableModel implements WorkAttendance {

    private final String[] columnNames = {"den", "od", "do", "typ", "odpr. hodin", "noční", "víkend", "svátek"};
    private final WorkAttendance workAttendance;
    private final Rounder rounder = new Rounder() {
    };
    private final List<Shift> shiftList;

    public OverTimeTableModelWorkAttendance(WorkAttendance workAttendance) {
        this.workAttendance = workAttendance;
        shiftList = new ArrayList<>(this.workAttendance.getShifts().values());
        sortAndNotify();
    }

    @Override
    public int getRowCount() {
        return shiftList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return this.columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        final Shift overtime = shiftList.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return overtime.getStart().toLocalDate().format(DateTimeFormatter.ofPattern("d. M"));
            case 1:
                return overtime.getStart().toLocalTime();
            case 2:
                return overtime.getEnd().toLocalTime();
            case 3:
                return overtime.getTypeOfShiftTwelveHours();
            case 4:
                return round(overtime.getWorkingHours().getWorkedOut());
            case 5:
                return round(overtime.getPremiumPayments().getNight());
            case 6:
                return round(overtime.getPremiumPayments().getWeekend());
            case 7:
                return round(overtime.getPremiumPayments().getHoliday());
            default:
                return "";
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        final Shift overtime = shiftList.get(rowIndex);
        switch (columnIndex) {
            case 1:
            case 2:
                if (columnIndex == 1) {
                    ShiftTimeAdjuster.adjustTime(overtime, aValue, ShiftTimeAdjuster.TimeType.START);
                } else {
                    ShiftTimeAdjuster.adjustTime(overtime, aValue, ShiftTimeAdjuster.TimeType.END);
                }
                sortAndNotify();
                break;
            default:
                throw new IllegalArgumentException(
                        String.format("Pro sloupec %s není nastavena editace.", columnIndex)
                );
        }

    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 1 || columnIndex == 2;
    }

    /**
     * Přidá do seznamu přesčas
     *
     * @param overtime nová přesčasová směna
     */
    public void addOvertime(Shift overtime) {
        shiftList.add(overtime);
        sortAndNotify();
    }


    /**
     * Odebrání směny ze seznamu
     *
     * @param index pořadí směny
     */
    public void removeOvertime(int index) {
        shiftList.remove(index);
        sortAndNotify();
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
        final Map<Integer, Shift> map = new TreeMap<>();
        for (int i = 0; i < shiftList.size(); i++) {
            final Shift shift = shiftList.get(i);
            map.put(i, shift);
        }
        return map;
    }

    private void sortAndNotify() {
        sortList();
        this.fireTableDataChanged();
    }

    private double round(double toRound) {
        return rounder.getRoundedDouble(toRound, 2);
    }

    private void sortList() {
        shiftList.sort(Comparator.comparing(Shift::getStart));
    }

}
