package cz.stanislavcapek.evidencepd.swingui.view.shiftplan;

import cz.stanislavcapek.evidencepd.domain.employee.Employee;
import cz.stanislavcapek.evidencepd.swingui.controller.EmployeeController;
import cz.stanislavcapek.evidencepd.swingui.view.employee.EmployeeEditorPanel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class FoundNewEmployeesDialog {

    private final JPanel content;

    public FoundNewEmployeesDialog(List<Employee> newEmployees, EmployeeController employeeController) {
        List<JPanel> panels = new ArrayList<>();
        for (Employee employee : newEmployees) {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
            panel.setBorder(BorderFactory.createEmptyBorder(5, 50, 5, 50));
            panel.add(new JLabel(employee.getFullName()));
            panel.add(Box.createHorizontalGlue());
            final JButton btnAdd = new JButton("přidat");
            panel.add(btnAdd);
            final JButton btnEditAndAdd = new JButton("upravit a přidat");
            panel.add(btnEditAndAdd);

            btnEditAndAdd.addActionListener(e -> {
                EmployeeEditorPanel edit = new EmployeeEditorPanel(employee);
                Object[] options = {"Uložit", "Zrušit"};
                int choice = JOptionPane.showOptionDialog(null, edit, "Editace zaměstnance", JOptionPane.YES_NO_OPTION,
                        JOptionPane.PLAIN_MESSAGE, null, options, options[1]);
                if (choice == 0) {
                    Employee edited = edit.getNewEmployee();
                    employee.setFirstName(edited.getFirstName());
                    employee.setLastName(edited.getLastName());
                    employeeController.addEmployee(employee);
                    btnAdd.setEnabled(false);
                    btnEditAndAdd.setEnabled(false);
                }
            });
            btnAdd.addActionListener(e -> {
                employeeController.addEmployee(employee);
                btnAdd.setEnabled(false);
                btnEditAndAdd.setEnabled(false);
            });

            panels.add(panel);
        }

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
        JPanel panel = new JPanel();
        final JLabel label = new JLabel(
                "V plánu služeb se vyskytují zaměstnanci,kteří ještě nejsou součástí seznamu.");
        panel.add(label);
        contentPane.add(panel);
        panels.forEach(contentPane::add);

        this.content = contentPane;
    }

    public void show() {
        JOptionPane.showMessageDialog(null, content,
                "Nalezeni nový zaměstnanci",
                JOptionPane.PLAIN_MESSAGE);
    }
}
