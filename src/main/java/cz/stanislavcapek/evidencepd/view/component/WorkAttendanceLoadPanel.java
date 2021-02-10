/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.stanislavcapek.evidencepd.view.component;

import cz.stanislavcapek.evidencepd.model.Month;
import cz.stanislavcapek.evidencepd.shiftplan.ShiftPlan;
import cz.stanislavcapek.evidencepd.employee.Employee;
import cz.stanislavcapek.evidencepd.employee.EmployeeListModel;
import cz.stanislavcapek.evidencepd.view.component.workattendance.WorkAttendanceWindow;
import cz.stanislavcapek.evidencepd.view.component.workattendance.WorkAttendanceHistoryPanel;
import jiconfont.icons.elusive.Elusive;
import jiconfont.swing.IconFontSwing;

import javax.swing.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * GUI Generování evidence pracovní doby. Umožňuje načíst excelový soubor
 * .xlsx s plány směn. Pokud je ve správném formátu, dojde k
 * načtení a jeho zpracování. Vyskytuje-li se v souboru zaměstnanec,
 * který není součástí lokálních dat, upozorní na tuto skutečnost.
 * <p>
 * Dále umožňuje načíst již uložené evidence včetně přečasů.
 *
 * @author Stanislav Čapek
 */
public class WorkAttendanceLoadPanel extends JPanel {
    private final JLabel lblLoadValidation = new JLabel();
    private final JButton btnLoad = new JButton();
    private final JButton btnShow = new JButton("Otevřít");
    private final EmployeeListModel employeeListModel;
    private final JComboBox<Month> cmbMonths;
    private final JPanel pnlRecordFromTempl;

    private ShiftPlan shiftPlan;

    /**
     * konstruktor bez parametru.
     */
    public WorkAttendanceLoadPanel() {
        super();
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.setBorder(BorderFactory.createEmptyBorder(10, 0, 40, 0));

        this.employeeListModel = EmployeeListModel.getInstance();

        cmbMonths = new JComboBox<>(Month.values());
        cmbMonths.setRenderer((list, value, index, isSelected, cellHasFocus) -> new JLabel(value.getName()));

        final JPanel pnlRecordHistory = getWorkAttendanceHistoryPanel();
        final JPanel pnlTemplateLoader = getTemplateLoaderPanel();
        pnlRecordFromTempl = getRecordFromTemplatePanel();
        pnlRecordHistory.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlTemplateLoader.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(pnlRecordHistory);
        final int height = 10;
        this.add(Box.createVerticalStrut(height));
        this.add(pnlTemplateLoader);
        this.add(pnlRecordFromTempl);
        pnlRecordFromTempl.setVisible(false);

        this.add(Box.createVerticalGlue());
    }

    private Month[] getFilteredMonths(ShiftPlan plan) {

        final Set<Integer> availableMonths = plan.getAvailableMonths();
        return Arrays.stream(Month.values())
                .filter(month -> availableMonths.contains(month.getNumber()))
                .toArray(Month[]::new);

    }


//    private instance methods

    private JPanel getTemplateLoaderPanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        this.btnLoad.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.lblLoadValidation.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(this.btnLoad);
        panel.add(Box.createVerticalStrut(10));
        panel.add(lblLoadValidation);

        final TemplateLoaderAction action = new TemplateLoaderAction("Načíst šablonu");
        btnLoad.setAction(action);
        btnLoad.addPropertyChangeListener(
                "loaded",
                evt -> validateLoadedTemplate(action, evt)
        );
        return panel;
    }

    private JPanel getWorkAttendanceHistoryPanel() {
        final JButton btnLoadFromHistory = new JButton("Načíst z uložených");
        btnLoadFromHistory.addActionListener(this::showWorkAttendanceHistoryDialog);

        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
        panel.add(btnLoadFromHistory);

        return panel;
    }

    public void validateLoadedTemplate(TemplateLoaderAction action, PropertyChangeEvent evt) {
        IconFontSwing.register(Elusive.getIconFont());
        final int fontSize = 12;
        Icon goodIcon = IconFontSwing.buildIcon(Elusive.OK, fontSize, Color.GREEN);
        Icon wrongIcon = IconFontSwing.buildIcon(Elusive.REMOVE, fontSize, Color.RED);
        if ((boolean) evt.getNewValue()) {
            shiftPlan = action.getWorkAttendancePlan();
            final String txt = String.format("Plán služeb %d", shiftPlan.getYear());
            lblLoadValidation.setText(txt);
            lblLoadValidation.setIcon(goodIcon);
            pnlRecordFromTempl.setVisible(true);
            final Set<Integer> ids = compareLists();
            if (!ids.isEmpty()) {
                showPossibilityAddEmployeesDialog(ids);
            }
            cmbMonths.setModel(new DefaultComboBoxModel<>(getFilteredMonths(shiftPlan)));
        } else {
            lblLoadValidation.setText("Není načten správný soubor");
            lblLoadValidation.setIcon(wrongIcon);
            pnlRecordFromTempl.setVisible(false);
        }
    }

    private JPanel getRecordFromTemplatePanel() {
        final JPanel panel = new JPanel();
        panel.add(cmbMonths);
        panel.add(btnShow);
        btnShow.addActionListener(this::showRecordFromTemplateWindow);
        return panel;
    }

    private void showRecordFromTemplateWindow(ActionEvent e) {
        final WorkAttendanceWindow window = new WorkAttendanceWindow(
                shiftPlan,
                cmbMonths.getSelectedIndex() + 1
        );
        window.setVisible(true);
    }

    private void showWorkAttendanceHistoryDialog(ActionEvent e) {
        final WorkAttendanceHistoryPanel records = new WorkAttendanceHistoryPanel().showListDialog();
        if (records.isChosen()) {
            new WorkAttendanceWindow(records.getRecordName()).setVisible(true);
        }
    }

    /**
     * Metoda zobrazí upozornění, že existují v načteném plánu směn zaměstnanci, kteří nejsou
     * ještě součástí lokálních dat.
     *
     * @param ids množnina ID zaměstnanců
     */
    private void showPossibilityAddEmployeesDialog(Set<Integer> ids) {
//        final int length = ids.size();
        List<JPanel> panels = new ArrayList<>();
        for (Integer id : ids) {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
            final String fullName = shiftPlan.getEmployee(id).getFullName();
            final String[] split = fullName.split(" ");
            final Employee employee = new Employee(id, split[0], split[1]);
            panel.setBorder(BorderFactory.createEmptyBorder(5, 50, 5, 50));
            panel.add(new JLabel(employee.getFullName()));
            panel.add(Box.createHorizontalGlue());
            final JButton btnAdd = new JButton("přidej");
            panel.add(btnAdd);
            final JButton btnEditAndAdd = new JButton("uprav a přidej");
            panel.add(btnEditAndAdd);

            btnEditAndAdd.addActionListener(e -> {
                EmployeeEditorPanel edit = new EmployeeEditorPanel(employee);
                Object[] options = {"Ulož", "Zruš"};
                int volba = JOptionPane.showOptionDialog(null, edit, "Editace zaměstnance", JOptionPane.YES_NO_OPTION,
                        JOptionPane.PLAIN_MESSAGE, null, options, options[1]);
                if (volba == 0) {
                    Employee upraveny = edit.getNewEmployee();
                    employee.setFirstName(upraveny.getFirstName());
                    employee.setLastName(upraveny.getLastName());
                    employeeListModel.addEmployee(employee);
                    btnAdd.setEnabled(false);
                    btnEditAndAdd.setEnabled(false);
                }
            });
            btnAdd.addActionListener(e -> {
                employeeListModel.addEmployee(employee);
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

        JOptionPane.showMessageDialog(null, contentPane,
                "Nalezeni nový zaměstnanci",
                JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Vrátí množinu ID, které jsou v plánu směn, ale nejsou v načteném
     * seznamu v aplikaci.
     *
     * @return množinu ID, které nejsou v aplikaci
     */
    private Set<Integer> compareLists() {
        final Set<Integer> employeeIds = shiftPlan.getEmployeeIds();
        return employeeIds.stream()
                .filter(Predicate.not(employeeListModel::containsEmployee))
                .collect(Collectors.toSet());
    }

}
