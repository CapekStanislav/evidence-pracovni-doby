/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.stanislavcapek.evidencepd.ui.component.employee;

import cz.stanislavcapek.evidencepd.employee.Employee;
import cz.stanislavcapek.evidencepd.ui.component.employee.action.AddEmployeeAction;
import cz.stanislavcapek.evidencepd.ui.component.employee.action.EditEmployeeAction;
import cz.stanislavcapek.evidencepd.ui.component.employee.action.LoadNewEmployeesAction;
import cz.stanislavcapek.evidencepd.ui.component.employee.action.RemoveEmployeeAction;
import cz.stanislavcapek.evidencepd.ui.component.utils.EmployeeListCellRenderer;
import cz.stanislavcapek.evidencepd.ui.component.utils.EmptyStringInputVerifier;
import cz.stanislavcapek.evidencepd.ui.component.utils.IntegerInputVerifier;
import cz.stanislavcapek.evidencepd.ui.controller.EmployeeController;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * GUI pro práci se seznamem strážníků. Umožňuje vytvářet nového zaměstnance, mazat nebo upravovat zaměstnance a
 * načíst externí soubor JSON se seznamem zaměstnanců.
 *
 * @author Stanislav Čapek
 */
public class EmployeeListPanel extends JPanel implements EmployeeSizeChangedListeners {
    private final JList<Employee> employeeJList;
    private final JTextField txtId, txtFirstName, txtLastName;
    private final JButton btnAdd;
    private final JButton btnRemove;
    private final Action removeAction;
    private final Action editAction;
    private final EmployeeController employeeController;

    public EmployeeListPanel(EmployeeController employeeController) {
        super(true);
        this.employeeController = employeeController;
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        // action creating
        Action addAction = new AddEmployeeAction(this, employeeController);
        removeAction = new RemoveEmployeeAction(this, employeeController);
        editAction = new EditEmployeeAction(this, employeeController);
        Action loadEmployeesAction = new LoadNewEmployeesAction(this, employeeController);


        // JPanel paddning //
        this.setBorder(new EmptyBorder(5, 5, 5, 5));

        JPanel wrapPanel;

//        first wrapPanel
        wrapPanel = new JPanel();
        wrapPanel.setLayout(new BoxLayout(wrapPanel, BoxLayout.LINE_AXIS));


        // first - new employee panel //
        JPanel pnlEmployee = new JPanel();
        pnlEmployee.setLayout(new BoxLayout(pnlEmployee, BoxLayout.PAGE_AXIS));
        pnlEmployee.setBorder(new TitledBorder("Nový zaměstnanec"));

        // general setting //
        JPanel panel = new JPanel();
        Border border5x5 = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        panel.setBorder(border5x5);
        JLabel lbl;
        int sizeOfTextField = 15;

        // first row //
        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
        lbl = new JLabel("Služ. číslo:");
        panel.add(lbl);

        panel.add(Box.createHorizontalGlue());

        txtId = new JTextField(sizeOfTextField);
        txtId.setMaximumSize(txtId.getPreferredSize());
        txtId.setHorizontalAlignment(JTextField.LEFT);
        panel.add(txtId);
        pnlEmployee.add(panel);

        // second row //
        panel = new JPanel();
        panel.setBorder(border5x5);
        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

        lbl = new JLabel("Jméno:");
        panel.add(lbl);

        panel.add(Box.createHorizontalGlue());

        txtFirstName = new JTextField(sizeOfTextField);
        txtFirstName.setMaximumSize(txtFirstName.getPreferredSize());
        txtFirstName.setHorizontalAlignment(JTextField.LEFT);
        panel.add(txtFirstName);
        pnlEmployee.add(panel);

        // third row //
        panel = new JPanel();
        panel.setBorder(border5x5);
        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

        lbl = new JLabel("Příjmení:");
        panel.add(lbl);

        panel.add(Box.createHorizontalGlue());

        txtLastName = new JTextField(sizeOfTextField);
        txtLastName.setMaximumSize(txtLastName.getPreferredSize());
        txtLastName.setHorizontalAlignment(JTextField.LEFT);
        panel.add(txtLastName);
        pnlEmployee.add(panel);

        pnlEmployee.add(Box.createVerticalGlue());

        wrapPanel.add(pnlEmployee);

        // second row - employee list //

        this.employeeJList = new JList<>(employeeController.getModel());
        this.employeeJList.setCellRenderer(new EmployeeListCellRenderer());
        //LIST.setFixedCellWidth(150);
        employeeJList.setVisibleRowCount(6);
        employeeJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeJList.setComponentPopupMenu(new ContextMenuJList());

        JScrollPane sp = new JScrollPane(employeeJList);
        sp.setWheelScrollingEnabled(true);
        sp.setPreferredSize(new Dimension(180, 120));
        sp.setBorder(new TitledBorder("Seznam zaměstnanců"));
        wrapPanel.add(sp);

        this.add(wrapPanel);

        // second wrapPanel //
        wrapPanel = new JPanel();
        wrapPanel.setLayout(new BoxLayout(wrapPanel, BoxLayout.PAGE_AXIS));
        wrapPanel.setBorder(new EmptyBorder(10, 0, 10, 0));


        // add and remove buttons //
        panel = new JPanel();
        panel.setLayout(new GridLayout(0, 4, 15, 0));
        btnAdd = new JButton(addAction);
        panel.add(btnAdd);

        btnRemove = new JButton(removeAction);
        // setEnableRemoveButton(employeeController.getEmployeeCount()); FIXME might not needed
        panel.add(btnRemove);

        JButton btnEdit = new JButton(editAction);
        panel.add(btnEdit);

        JButton btn = new JButton(loadEmployeesAction);
        panel.add(btn);
        panel.setMaximumSize(panel.getPreferredSize());

        wrapPanel.add(panel);
        wrapPanel.add(Box.createVerticalGlue());
        this.add(wrapPanel);

        employeeController.addSizeChangedListener(this);
    }

    @Override
    public void sizeChanged(int size) {
        setEnableRemoveButton(size > 0);
    }

    public void setEnableRemoveButton(boolean enabled) {
        btnRemove.setEnabled(enabled);
    }

    public String getId() {
        return txtId.getText();
    }

    public String getFirstName() {
        return txtFirstName.getText();
    }

    public String getLastName() {
        return txtLastName.getText();
    }

    public void resetEmployeeForm() {
        txtId.setText("");
        txtFirstName.setText("");
        txtLastName.setText("");
    }

    public boolean isAllFilled() {
        final InputVerifier stringInputVerifier = new EmptyStringInputVerifier() {
            @Override
            public boolean shouldYieldFocus(JComponent source, JComponent target) {
                if (super.shouldYieldFocus(source, target)) {
                    return true;
                } else {
                    String message = "Políčko musí být vyplněné.";
                    JOptionPane.showMessageDialog(source, message);
                    return false;
                }
            }
        };
        final InputVerifier integerInputVerifier = new IntegerInputVerifier();

        return integerInputVerifier.shouldYieldFocus(txtId, null) &&
                stringInputVerifier.shouldYieldFocus(txtFirstName, null) &&
                stringInputVerifier.shouldYieldFocus(txtLastName, null);
    }

    public void showExistingEmployeeDialog() {
        JOptionPane.showMessageDialog(btnAdd, "Strážník nebyl přidán! Již existuje strážník " +
                "se stejným služebním číslem \n", "Nelze přidat strážníka", JOptionPane.ERROR_MESSAGE);
    }

    public int getSelectedIndex() {
        return employeeJList.getSelectedIndex();
    }

    public int showRemovalConfirmationDialog(int indexToRemove) {
        Object[] anoNe = {"Ano", "Ne"};
        final String message = "Opravdu odebrat: " +
                employeeController.getEmployeeAt(indexToRemove).getFullName();
        return JOptionPane.showOptionDialog(btnRemove,
                message,
                "Odebrání strážníka ze seznamu",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, anoNe, anoNe[1]);
    }

    public void setRemoveButtonEnabled(boolean enabled) {
        btnRemove.setEnabled(enabled);
    }

    /**
     * Contextual menu for employees JList.
     */
    private class ContextMenuJList extends JPopupMenu {
        ContextMenuJList() {
            super();
            add(editAction);
            add(removeAction);
        }
    }
}
