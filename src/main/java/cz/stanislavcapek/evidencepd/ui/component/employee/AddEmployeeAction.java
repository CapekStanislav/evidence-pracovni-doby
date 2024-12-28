package cz.stanislavcapek.evidencepd.ui.component.employee;

import cz.stanislavcapek.evidencepd.employee.Employee;
import cz.stanislavcapek.evidencepd.ui.controller.EmployeeController;
import jiconfont.icons.elusive.Elusive;
import jiconfont.swing.IconFontSwing;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Action add an employee
 */
class AddEmployeeAction extends AbstractAction {

    private final EmployeeListPanel panel;
    private final EmployeeController controller;

    AddEmployeeAction(EmployeeListPanel panel, EmployeeController controller) {
        super("Přidej");
        this.panel = panel;
        this.controller = controller;
        IconFontSwing.register(Elusive.getIconFont());
        final Elusive plusIcon = Elusive.PLUS;
        Icon addIconSmall = IconFontSwing.buildIcon(plusIcon, 12);
        Icon addIconLarge = IconFontSwing.buildIcon(plusIcon, 16);

        putValue(SHORT_DESCRIPTION, "Přidá nového zaměstnance");
        putValue(MNEMONIC_KEY, KeyEvent.VK_P);
        putValue(Action.SMALL_ICON, addIconSmall);
        putValue(Action.LARGE_ICON_KEY, addIconLarge);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (panel.isAllFilled()) {
            // create new employee
            final Employee employee = new Employee(
                    Integer.parseInt(panel.getId()),
                    panel.getFirstName(),
                    panel.getLastName()
            );
            if (controller.addEmployee(employee)) {
                // clear fields
                panel.resetEmployeeForm();
            } else {
                panel.showExistingEmployeeDialog();
            }
        }
    }
}
