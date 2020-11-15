package cz.stanislavcapek.evidencepd.view.component;

import cz.stanislavcapek.evidencepd.employee.Employee;

import javax.swing.*;
import java.awt.Dimension;

/**
 * GUI pro editaci zaměstnance předaného v parametru konstruktoru. Třída na požádání vrací nového upraveného
 * {@link Employee}. Původně vložený zaměstnanec zůstává beze změn.
 *
 * @author Stanislav Čapek
 */
class EmployeeEditorPanel extends JPanel {

    private final JTextField txtFirstName;
    private final JTextField txtLastName;
    private Employee newEmployee;

    /**
     * Konstruktor
     *
     * @param employee zaměstnanec k úpravě
     */
    EmployeeEditorPanel(Employee employee) {
        newEmployee = new Employee(employee.getId(),
                employee.getFirstName(),
                employee.getLastName());

        StringVerifier stringVerifier = new StringVerifier();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        JLabel lbl = new JLabel("Identifikační číslo: " + employee.getId());
        panel.add(lbl);
        panel.add(Box.createRigidArea(new Dimension(10, 10)));

        lbl = new JLabel("Jméno");
        panel.add(lbl);

        txtFirstName = new JTextField(employee.getFirstName());
        txtFirstName.setInputVerifier(stringVerifier);
        panel.add(txtFirstName);

        panel.add(Box.createRigidArea(new Dimension(5, 5)));

        lbl = new JLabel("Příjmení");
        panel.add(lbl);

        txtLastName = new JTextField(employee.getLastName());
        txtLastName.setInputVerifier(stringVerifier);
        panel.add(txtLastName);
        panel.add(Box.createRigidArea(new Dimension(10, 10)));

        this.add(panel);
    }

    /**
     * Vrátí nově upraveného zaměstnance.
     *
     * @return {@link Employee} upravený zaměstnanec
     */
    Employee getNewEmployee() {
        return newEmployee;
    }

    /**
     * Validátor textového řetězce
     */
    private class StringVerifier extends InputVerifier {

        // FIXME: 13.11.2020 odtrátovat - a komunikaci editoru řešit přes listener PropertyChangeListener

        @Override
        public boolean verify(JComponent input) {
            if (input instanceof JTextField) {
                return !((JTextField) input).getText().equals("");
            }
            return false;
        }

        @Override
        public boolean shouldYieldFocus(JComponent source) {

            if (verify(source)) {
                if (source.equals(txtFirstName)) {
                    String firstName = ((JTextField) source).getText();
                    newEmployee.setFirstName(firstName);
                } else if (source.equals(txtLastName)) {
                    String lastName = ((JTextField) source).getText();
                    newEmployee.setLastName(lastName);
                }
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Nesmí obsahovat prázdné pole",
                        "Chyba při zadávání", JOptionPane.WARNING_MESSAGE);
            }
            return false;
        }
    }

}

