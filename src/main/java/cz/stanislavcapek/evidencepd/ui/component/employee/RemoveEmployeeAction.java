package cz.stanislavcapek.evidencepd.ui.component.employee;

import cz.stanislavcapek.evidencepd.employee.Employee;
import cz.stanislavcapek.evidencepd.ui.controller.EmployeeController;
import jiconfont.IconCode;
import jiconfont.icons.elusive.Elusive;
import jiconfont.swing.IconFontSwing;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Action remove the employee
 */
class RemoveEmployeeAction extends AbstractAction {

    private final EmployeeListPanel panel;
    private final EmployeeController controller;

    RemoveEmployeeAction(EmployeeListPanel panel, EmployeeController controller) {
        super("Odeber");
        this.panel = panel;
        this.controller = controller;
        IconFontSwing.register(Elusive.getIconFont());
        final IconCode removeIcon = Elusive.REMOVE;
        Icon removeIconSmall = IconFontSwing.buildIcon(removeIcon, 12);
        Icon removeIconLarge = IconFontSwing.buildIcon(removeIcon, 16);

        putValue(SHORT_DESCRIPTION, "Odebere vybraného zaměstnance");
        putValue(MNEMONIC_KEY, KeyEvent.VK_O);
        putValue(Action.SMALL_ICON, removeIconSmall);
        putValue(Action.LARGE_ICON_KEY, removeIconLarge);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int indexToRemove = panel.getSelectedIndex();

        if (!(indexToRemove < 0)) {
            if (panel.showRemovalConfirmationDialog(indexToRemove) == JOptionPane.YES_OPTION) {
                Employee toRemove = controller.getEmployeeAt(indexToRemove);
                controller.removeEmployee(toRemove);
            }
        }

        if (controller.getEmployeeCount() == 0) {
            panel.setRemoveButtonEnabled(false);
        }
    }
}
