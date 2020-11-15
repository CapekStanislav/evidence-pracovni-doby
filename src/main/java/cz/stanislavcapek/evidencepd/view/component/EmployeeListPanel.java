/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.stanislavcapek.evidencepd.view.component;


import cz.stanislavcapek.evidencepd.employee.Employee;
import cz.stanislavcapek.evidencepd.employee.EmployeeListModel;
import cz.stanislavcapek.evidencepd.employee.EmployeesDao;
import cz.stanislavcapek.evidencepd.view.component.utils.EmployeeListCellRenderer;
import jiconfont.IconCode;
import jiconfont.icons.elusive.Elusive;
import jiconfont.swing.IconFontSwing;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * GUI pro práci se seznamem strážníků. Umožňuje vytvářet nového zaměstnance, mazat nebo upravovat zaměstnance a
 * načíst externí soubor XML se seznamem zaměstnanců.
 *
 * @author Stanislav Čapek
 */
public class EmployeeListPanel extends JPanel {

    private final EmployeeListModel employeeListModel;
    private final JList<Employee> employeeJList;
    private final JTextField txtId, txtFirstName, txtLastName;
    private final JButton btnAdd;
    private final JButton btnRemove;
    private final Action removeAction;
    private final Action editAciton;

    /**
     * Konstruktor bez parametru.
     */
    public EmployeeListPanel() {
        super(true);
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.employeeListModel = EmployeeListModel.getInstance();

        // action creating
        Action pridejAction = new AddAction("Přidej", "Přidá nového zaměstnance", KeyEvent.VK_P);
        removeAction = new RemoveAciton("Odeber", "Odebere vybraného zaměstnance", KeyEvent.VK_O);
        editAciton = new EditAciton("Uprav", "Upraví vybraného zaměstnance", KeyEvent.VK_U);
        Action nactiAction = new NactiAction("Načti", "Načíst nový seznam zaměstnance", KeyEvent.VK_N);

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

        // second - employee list //


        this.employeeJList = new JList<>(employeeListModel);
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
        btnAdd = new JButton(pridejAction);
        panel.add(btnAdd);

        btnRemove = new JButton(removeAction);
        setEnableDisableOdeber(employeeListModel.getSize());
        panel.add(btnRemove);

        JButton btnEdit = new JButton(editAciton);
        panel.add(btnEdit);

        JButton btn = new JButton(nactiAction);
        panel.add(btn);
        panel.setMaximumSize(panel.getPreferredSize());

        wrapPanel.add(panel);
        wrapPanel.add(Box.createVerticalGlue());
        this.add(wrapPanel);

        employeeListModel.addListDataListener(new ListDataListener() {
            @Override
            public void intervalAdded(ListDataEvent e) {
                setEnableDisableOdeber(employeeListModel.getSize());
            }

            @Override
            public void intervalRemoved(ListDataEvent e) {
                setEnableDisableOdeber(employeeListModel.getSize());
            }

            @Override
            public void contentsChanged(ListDataEvent e) {
                setEnableDisableOdeber(employeeListModel.getSize());
            }
        });
    }

    /**
     * Obsluha načtení seznamu strážníků, smaže původní seznam a nahradí jej novým
     */
    private void loadEmployees() {
        // TODO: 15.11.2020 opět rozdělit do metod
        JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
        int volba = chooser.showOpenDialog(null);

        if (volba == JFileChooser.APPROVE_OPTION) {
            Path file = Paths.get(chooser.getSelectedFile().getAbsolutePath());
            EmployeesDao io = new EmployeesDao();

            try {
                boolean b = false;
                List<Employee> seznam;
                if ((seznam = io.load(file)) != null) {
                    employeeListModel.clearList();
                    for (Employee s :
                            seznam) {
                        employeeListModel.addEmployee(s);
                    }
                    b = true;
                }
                showLoadingResultDialog(b);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Nepodařilo se načíst seznam strážníků. " +
                                "Při náčítání souboru došlo k chybě",
                        "Chyba při náčítání", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    /**
     * Ukáže dialogové okno s výsledkem načtení souboru
     *
     * @param b výsledek
     */
    private void showLoadingResultDialog(boolean b) {
        if (b) {
            JOptionPane.showMessageDialog(null, "Seznam úspěšně načten."
                    , "Načtení v pořádku", JOptionPane.PLAIN_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Seznam se nepodařilo načíst. " +
                            "Nesprávný formát souboru"
                    , "Chyba při načítání", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Metoda validuje textové pole ID. Nesmí být prázdné a musí být číslo.
     *
     * @return false, prázdné nebo to není číslo
     */
    private boolean validateId() {
        try {
            Integer.parseInt(txtId.getText());
            txtId.setBackground(Color.white);
            return true;
        } catch (NumberFormatException e) {
            txtId.setBackground(Color.YELLOW);
            return false;
        }
    }

    /**
     * Metoda validuje textová pole jména a příjmení, nesmí být prázdná
     *
     * @param txtField textové pole
     * @return false, když je alespoň jedno pole prázdné
     */
    private boolean validateName(JTextField txtField) {
        if (txtField.getText().equalsIgnoreCase("")) {
            txtField.setBackground(Color.YELLOW);
            return false;
        } else {
            txtField.setBackground(Color.WHITE);
            return true;
        }
    }

    /**
     * Metoda kontroluje zda jsou všechny pole (Služební číslo, jméno a
     * příjmení) správně vyplněné. Pokud není žádá o doplnění.
     *
     * @return {@code true} - v případě, kdy je vše vyplněno<br>
     * {@code false} - alespoň jedno pole je prázdné
     */
    private Boolean isAllFilled() {
        boolean valId = validateId();
        boolean valJmeno = validateName(txtFirstName);
        boolean valPrijmeni = validateName(txtLastName);

        boolean isAllFilled = true;
        if (!valId || !valJmeno || !valPrijmeni) {
            isAllFilled = false;
        }
        if (!isAllFilled) {
            showNeededFieldsDialog(valId, valJmeno, valPrijmeni);
        }
        return isAllFilled;
    }

    /**
     * Ukáže dialogové okno, které ukáže co je ještě třeba vyplnit.
     *
     * @param id       boolean
     * @param jmeno    boolean
     * @param prijmeni boolean
     */
    private void showNeededFieldsDialog(boolean id, boolean jmeno, boolean prijmeni) {
        String zprava = "";
        if (!id) {
            zprava += "Služební číslo je buď prázdné nebo neobsahuje číslo \n";
        }
        if (!jmeno) {
            zprava += "Jméno nesmí být prázdné \n";
        }
        if (!prijmeni) {
            zprava += "Příjmení nesmí být prázdné \n";
        }
        JOptionPane.showMessageDialog(btnAdd,
                "Vyskytla se chyba při zadání u těchto položek: \n"
                        + zprava,
                "Nesprávné údaje",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Zneplatnění tlačítka odeber, když není co odebírat. Seznam je prázdný.
     *
     * @param size velikost seznamu
     */
    private void setEnableDisableOdeber(int size) {
        btnRemove.setEnabled(size > 0);
    }

    /**
     * Akce PŘIDEJ
     */
    private class AddAction extends AbstractAction {

        AddAction(String name, String description, int mnemonic) {
            super(name);
            IconFontSwing.register(Elusive.getIconFont());
            final Elusive plusIcon = Elusive.PLUS;
            Icon addIconSmall = IconFontSwing.buildIcon(plusIcon, 12);
            Icon addIconLarge = IconFontSwing.buildIcon(plusIcon, 16);

            putValue(SHORT_DESCRIPTION, description);
            putValue(MNEMONIC_KEY, mnemonic);
            putValue(Action.SMALL_ICON, addIconSmall);
            putValue(Action.LARGE_ICON_KEY, addIconLarge);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (isAllFilled()) {
                // create new employee
                final Employee employee = new Employee(
                        Integer.parseInt(txtId.getText()),
                        txtFirstName.getText(),
                        txtLastName.getText()
                );
                if (employeeListModel.addEmployee(employee)) {
                    // clear fields
                    txtId.setText("");
                    txtFirstName.setText("");
                    txtLastName.setText("");

                } else {
                    showExistingEnployeeDialog();
                }
            }
        }

        private void showExistingEnployeeDialog() {
            JOptionPane.showMessageDialog(btnAdd, "Strážník nebyl přidán! Již existuje strážník " +
                    "se stejným služebním číslem \n", "Nelze přidat strážníka", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Akce ODEBER
     */
    private class RemoveAciton extends AbstractAction {

        RemoveAciton(String name, String description, int mnemonic) {
            super(name);
            IconFontSwing.register(Elusive.getIconFont());
            final IconCode removIcon = Elusive.REMOVE;
            Icon removeIconSmall = IconFontSwing.buildIcon(removIcon, 12);
            Icon removeIconLarge = IconFontSwing.buildIcon(removIcon, 16);

            putValue(SHORT_DESCRIPTION, description);
            putValue(MNEMONIC_KEY, mnemonic);
            putValue(Action.SMALL_ICON, removeIconSmall);
            putValue(Action.LARGE_ICON_KEY, removeIconLarge);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int indexToRemove = employeeJList.getSelectedIndex();

            if (!(indexToRemove < 0)) {
                if (showRemovalConfirmationDialog(indexToRemove) == JOptionPane.YES_OPTION) {
                    Employee toRemove = employeeListModel.getElementAt(indexToRemove);
                    employeeListModel.removeEmployee(toRemove);
                }
            }

            if (employeeListModel.getSize() == 0) {
                btnRemove.setEnabled(false);
            }
        }

        private int showRemovalConfirmationDialog(int indexToRemove) {
            Object[] anoNe = {"Ano", "Ne"};
            final String message = "Opravdu odebrat: " +
                    employeeListModel.getElementAt(indexToRemove).getFullName();
            return JOptionPane.showOptionDialog(btnRemove,
                    message,
                    "Odebrání strážníka ze seznamu",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, anoNe, anoNe[1]);
        }
    }

    /**
     * Akce UPRAV
     */
    private class EditAciton extends AbstractAction {

        EditAciton(String name, String description, int mnemonic) {
            super(name);
            IconFontSwing.register(Elusive.getIconFont());

            final Elusive wrench = Elusive.WRENCH;
            Icon editSmall = IconFontSwing.buildIcon(wrench, 12);
            Icon editLarge = IconFontSwing.buildIcon(wrench, 16);

            putValue(SHORT_DESCRIPTION, description);
            putValue(MNEMONIC_KEY, mnemonic);
            putValue(SMALL_ICON, editSmall);
            putValue(LARGE_ICON_KEY, editLarge);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int index;
            if ((index = employeeJList.getSelectedIndex()) == -1) {
                JOptionPane.showMessageDialog(null, "Nebyl vybrán zaměstnanec k editaci",
                        "Žádný vybraný zaměstnanec",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            Employee employeeToEdit = employeeJList.getModel().getElementAt(index);
            EmployeeEditorPanel employeeEditorPanel = new EmployeeEditorPanel(employeeToEdit);
            if (getEditationDialog(employeeEditorPanel) == 0) {
                Employee employeeEdited = employeeEditorPanel.getNewEmployee();
                employeeToEdit.setFirstName(employeeEdited.getFirstName());
                employeeToEdit.setLastName(employeeEdited.getLastName());
                employeeListModel.fireModelChanged();
            }

        }

        private int getEditationDialog(EmployeeEditorPanel edit) {
            Object[] options = {"Ulož", "Zruš"};
            int volba = JOptionPane.showOptionDialog(null, edit, "Editace zaměstnance", JOptionPane.YES_NO_OPTION,
                    JOptionPane.PLAIN_MESSAGE, null, options, options[1]);
            return volba;
        }
    }

    /**
     * NAČTI zaměstnance
     */
    private class NactiAction extends AbstractAction {

        NactiAction(String name, String description, int mnemonic) {
            super(name);
            IconFontSwing.register(Elusive.getIconFont());
            final Elusive folderOpen = Elusive.FOLDER_OPEN;
            Icon loadSmall = IconFontSwing.buildIcon(folderOpen, 12);
            Icon loadLarge = IconFontSwing.buildIcon(folderOpen, 16);

            putValue(Action.SHORT_DESCRIPTION, description);
            putValue(Action.MNEMONIC_KEY, mnemonic);
            putValue(Action.SMALL_ICON, loadSmall);
            putValue(Action.LARGE_ICON_KEY, loadLarge);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            loadEmployees();
        }
    }

    /**
     * Contextové menu pro JList.
     */
    private class ContextMenuJList extends JPopupMenu {
        ContextMenuJList() {
            super();
            add(editAciton);
            add(removeAction);
        }
    }
}
