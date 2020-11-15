package cz.stanislavcapek.evidencepd.view.component.utils;

import cz.stanislavcapek.evidencepd.record.Record;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.time.DayOfWeek;

/**
 * Instance třídy {@code ObarvovacVikenduTableCellRenderer}
 *
 * @author Stanislav Čapek
 */
public class ColorerWeekendShiftTableCellRenderer extends DefaultTableCellRenderer {
    private final Record record;

    public ColorerWeekendShiftTableCellRenderer(Record record) {
        this.record = record;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        c.setForeground(Color.BLACK);
        int day = row + 1;
        final DayOfWeek dayOfWeek = record.getShifts().get(day).getStart().toLocalDate().getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            c.setBackground(Color.LIGHT_GRAY);
        } else {
            c.setBackground(Color.WHITE);
        }
        return c;
    }
}
