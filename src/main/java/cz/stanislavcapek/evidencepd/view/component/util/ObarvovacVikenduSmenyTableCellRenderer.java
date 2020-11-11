package cz.stanislavcapek.evidencepd.view.component.util;

import cz.stanislavcapek.evidencepd.evidence.Evidence;

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
public class ObarvovacVikenduSmenyTableCellRenderer extends DefaultTableCellRenderer {
    private final Evidence smeny;

    public ObarvovacVikenduSmenyTableCellRenderer(Evidence smeny) {
        this.smeny = smeny;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        c.setForeground(Color.BLACK);
        int den = row + 1;
        final DayOfWeek denVTydnu = smeny.getSmeny().get(den).getZacatek().toLocalDate().getDayOfWeek();
        if (denVTydnu == DayOfWeek.SATURDAY || denVTydnu == DayOfWeek.SUNDAY) {
            c.setBackground(Color.LIGHT_GRAY);
        } else {
            c.setBackground(Color.WHITE);
        }
        return c;
    }
}
