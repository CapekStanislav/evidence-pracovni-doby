package cz.stanislavcapek.evidencepd.swingui.view.employee.action;

import cz.stanislavcapek.evidencepd.domain.employee.Employee;
import cz.stanislavcapek.evidencepd.swingui.controller.EmployeeController;
import cz.stanislavcapek.evidencepd.swingui.view.employee.EmployeeEditorPanel;
import cz.stanislavcapek.evidencepd.swingui.view.employee.EmployeeListPanel;
import jiconfont.icons.elusive.Elusive;
import jiconfont.swing.IconFontSwing;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Action edit en existing employee
 */
public class EditEmployeeAction extends AbstractAction {

    private final EmployeeListPanel panel;
    private final EmployeeController controller;

    public EditEmployeeAction(EmployeeListPanel panel, EmployeeController controller) {
        super("Uprav");
        this.panel = panel;
        this.controller = controller;
        IconFontSwing.register(Elusive.getIconFont());

        final Elusive wrench = Elusive.WRENCH;
        Icon editSmall = IconFontSwing.buildIcon(wrench, 12);
        Icon editLarge = IconFontSwing.buildIcon(wrench, 16);

        putValue(SHORT_DESCRIPTION, "Upraví vybraného zaměstnance");
        putValue(MNEMONIC_KEY, KeyEvent.VK_U);
        putValue(SMALL_ICON, editSmall);
        putValue(LARGE_ICON_KEY, editLarge);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int index;
        if ((index = panel.getSelectedIndex()) == -1) {
            JOptionPane.showMessageDialog(null, "Nebyl vybrán zaměstnanec k editaci",
                    "Žádný vybraný zaměstnanec",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Employee employeeToEdit = controller.getEmployeeAt(index);
        EmployeeEditorPanel employeeEditorPanel = new EmployeeEditorPanel(employeeToEdit);
        if (getEditDialog(employeeEditorPanel) == 0) {
            Employee employeeEdited = employeeEditorPanel.getNewEmployee();
            controller.updateEmployee(
                    employeeToEdit.getId(),
                    employeeEdited.getFirstName(),
                    employeeEdited.getLastName()
            );
        }
    }

    private int getEditDialog(EmployeeEditorPanel edit) {
        Object[] options = {"Ulož", "Zruš"};
        return JOptionPane.showOptionDialog(null, edit, "Editace zaměstnance", JOptionPane.YES_NO_OPTION,
                JOptionPane.PLAIN_MESSAGE, null, options, options[1]);
    }
}
