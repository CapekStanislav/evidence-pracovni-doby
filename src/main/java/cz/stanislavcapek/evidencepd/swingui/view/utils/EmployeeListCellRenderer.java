package cz.stanislavcapek.evidencepd.swingui.view.utils;

import cz.stanislavcapek.evidencepd.domain.employee.Employee;

import javax.swing.*;
import java.awt.*;

/**
 * An instance of class {@code EmployeeListCellRenderer}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public class EmployeeListCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(
            JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        final Employee employee = value instanceof Employee ? ((Employee) value) : null;
        setText(String.format("%s - %s", employee.getId(), employee.getFullName()));
        return this;
    }
}
