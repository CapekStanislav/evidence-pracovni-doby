package cz.stanislavcapek.evidencepd.view.component.utils;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Instance třídy {@code ColorerWeekendOvertimeTableCellRenderer}
 *
 * @author Stanislav Čapek
 */
public class ColorerWeekendOvertimeTableCellRenderer extends DefaultTableCellRenderer {

    private final int year;

    public ColorerWeekendOvertimeTableCellRenderer(int year) {
        this.year = year;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        c.setForeground(Color.BLACK);
        try {

            LocalDate date = LocalDate.parse(String.format("%s %s", value, year), DateTimeFormatter.ofPattern("d. M yyyy"));
            ;
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                c.setBackground(Color.LIGHT_GRAY);
            } else {
                c.setBackground(Color.WHITE);
            }
            return c;
        } catch (Exception e) {
            return c;
        }
    }
}
