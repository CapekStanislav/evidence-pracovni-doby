package cz.stanislavcapek.evidencepd.ui.component.employee;

import cz.stanislavcapek.evidencepd.employee.Employee;
import cz.stanislavcapek.evidencepd.ui.component.utils.EmptyStringInputVerifier;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * GUI pro editaci zaměstnance předaného v parametru konstruktoru. Třída na požádání vrací nového upraveného
 * {@link Employee}. Původně vložený zaměstnanec zůstává beze změn.
 *
 * @author Stanislav Čapek
 */
public class EmployeeEditorPanel extends JPanel {

    private final JTextField txtFirstName;
    private final JTextField txtLastName;
    private final Employee newEmployee;

    public EmployeeEditorPanel(Employee employee) {
        newEmployee = new Employee(employee.getId(),
                employee.getFirstName(),
                employee.getLastName());

        InputVerifier stringVerifier = new EmptyStringInputVerifier() {
            @Override
            public boolean shouldYieldFocus(JComponent source, JComponent target) {
                if (super.shouldYieldFocus(source, target)) {
                    return true;
                } else {
                    String message = "Políčko musí být vyplněno";
                    JOptionPane.showMessageDialog(EmployeeEditorPanel.this, message);
                    return false;
                }
            }
        };

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        JLabel lbl = new JLabel("Identifikační číslo: " + employee.getId());
        panel.add(lbl);
        panel.add(Box.createRigidArea(new Dimension(10, 10)));

        lbl = new JLabel("Jméno");
        panel.add(lbl);

        txtFirstName = new JTextField(employee.getFirstName());
        txtFirstName.setInputVerifier(stringVerifier);
        txtFirstName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                newEmployee.setFirstName(txtFirstName.getText());
            }
        });
        panel.add(txtFirstName);

        panel.add(Box.createRigidArea(new Dimension(5, 5)));

        lbl = new JLabel("Příjmení");
        panel.add(lbl);

        txtLastName = new JTextField(employee.getLastName());
        txtLastName.setInputVerifier(stringVerifier);
        txtLastName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                newEmployee.setLastName(txtLastName.getText());
            }
        });
        panel.add(txtLastName);
        panel.add(Box.createRigidArea(new Dimension(10, 10)));

        this.add(panel);
    }
    
    public Employee getNewEmployee() {
        return newEmployee;
    }

}

