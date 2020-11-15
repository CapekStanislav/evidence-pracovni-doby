package cz.stanislavcapek.evidencepd.pdf;

import cz.stanislavcapek.evidencepd.model.WorkingTimeFund;
import cz.stanislavcapek.evidencepd.record.Record;
import cz.stanislavcapek.evidencepd.shift.WorkingTime;
import cz.stanislavcapek.evidencepd.shift.Shift;

import javax.swing.table.TableModel;
import java.time.LocalDate;

/**
 * An instance of class {@code RecordDocument}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public class RecordDocument {

    private final TableModel model;
    private final Record record;

    public <T extends TableModel> RecordDocument(Record record, T model) {
        this.record = record;
        this.model = model;
    }

    public String getName() {
        return record.getEmployee().getFullName();
    }

    public int getYear() {
        return record.getYear();
    }

    public String getMonth() {
        return record.getMonth().getName();
    }

    /**
     * @param rowIndex 0 base index
     * @return date of shift
     */
    public LocalDate getDate(int rowIndex) {
        final int day = rowIndex + 1;
        return record.getShifts().get(day).getStart().toLocalDate();
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
                record.getTypeOfWeeklyWorkingTime()
        );
    }

    public double getLastMonthHours() {
        return record.getLastMonth();
    }

    public double getNextMonthHours() {
        return (getWorkedHours() + getLastMonthHours()) - getWorkTimeFund();
    }

    public double getWorkedHours() {
        return record.getShifts().values()
                .stream()
                .map(Shift::getWorkingHours)
                .mapToDouble(WorkingTime::getWorkedOut)
                .sum();

    }
}
