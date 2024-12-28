/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.stanislavcapek.evidencepd.ui.component.template;

import cz.stanislavcapek.evidencepd.employee.Employee;
import cz.stanislavcapek.evidencepd.employee.EmployeeListModel;
import cz.stanislavcapek.evidencepd.ui.component.utils.IntegerInputVerifier;
import cz.stanislavcapek.evidencepd.ui.controller.ShiftPlanController;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * GUI Generování šablony pro zadaný ROK. Výstupní formát je excelový soubor xlsx.
 *
 * @author Stanislav Čapek
 */
public class WorkAttendanceTemplatePanel extends JPanel {
    private final JButton btnGenerate;
    private final JTextField txtYear;
    private final JViewport viewport;
    private final List<Employee> selectedEmployeeList;
    private final ShiftPlanController controller;


    public WorkAttendanceTemplatePanel(ShiftPlanController controller) {
        super(true);
        this.controller = controller;
        selectedEmployeeList = new ArrayList<>();

        // JPanel padding //
        this.setBorder(new EmptyBorder(10, 5, 20, 5));
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        JLabel lbl;
        Border borderLeftAndRight = BorderFactory.createEmptyBorder(0, 75, 0, 75);
        JPanel wrapPanel = new JPanel();
        wrapPanel.setLayout(new BoxLayout(wrapPanel, BoxLayout.LINE_AXIS));
        wrapPanel.setBorder(borderLeftAndRight);

        // first panel //
        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder("Nastavení roku"));

        // first row //
        lbl = new JLabel("Rok:");
        panel.add(lbl);

        txtYear = new JTextField(10);
        final InputVerifier integerInputVerifier = new IntegerInputVerifier() {
            @Override
            public boolean shouldYieldFocus(JComponent source, JComponent target) {
                if (super.shouldYieldFocus(source, target)) {
                    btnGenerate.setEnabled(true);
                    return true;
                } else {
                    btnGenerate.setEnabled(false);
                    return false;
                }
            }
        };
        txtYear.setInputVerifier(integerInputVerifier);
        Set<AWTKeyStroke> keys = txtYear.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
        Set<AWTKeyStroke> newKeys = new HashSet<>(keys);
        newKeys.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ENTER, 0));
        txtYear.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, newKeys);
        panel.add(txtYear);

        wrapPanel.add(panel);
        this.add(wrapPanel);


        // second panel //
        wrapPanel = new JPanel();
        wrapPanel.setLayout(new BoxLayout(wrapPanel, BoxLayout.LINE_AXIS));
        wrapPanel.setBorder(borderLeftAndRight);

        JScrollPane sp = new JScrollPane();
        sp.setViewportView(createCheckBoxedList());
        viewport = sp.getViewport();
        sp.setWheelScrollingEnabled(true);
        sp.setBorder(new TitledBorder("Výběr strážníků"));
        wrapPanel.add(sp);
        this.add(wrapPanel);

        // last row
        wrapPanel = new JPanel();
        wrapPanel.setLayout(new BoxLayout(wrapPanel, BoxLayout.LINE_AXIS));

        panel = new JPanel();

        btnGenerate = new JButton("Generuj šablonu");
        btnGenerate.addActionListener(e -> createTemplate());
        btnGenerate.setEnabled(false);
        panel.add(btnGenerate);

        wrapPanel.add(panel);
        this.add(wrapPanel);

        this.add(Box.createVerticalGlue());

        controller.addListDataListener(new ListDataListener() {
            @Override
            public void intervalAdded(ListDataEvent e) {
                sp.setViewportView(createCheckBoxedList());
            }

            @Override
            public void intervalRemoved(ListDataEvent e) {
                sp.setViewportView(createCheckBoxedList());
            }

            @Override
            public void contentsChanged(ListDataEvent e) {
                sp.setViewportView(createCheckBoxedList());
            }
        });

    }

    /**
     * Podpůrná metoda, která synchronizuje {@link EmployeeListModel} s vybranými (zaškrtnutými) zaměstnanci.
     */
    private void updateSelectedEmployeeList() {
        selectedEmployeeList.clear();
        Component[] comp = ((JPanel) viewport.getView()).getComponents();
        for (int i = 0; i < comp.length; i++) {
            JCheckBox box = (JCheckBox) comp[i];
            if (box.isSelected()) {
                selectedEmployeeList.add(controller.getEmployeeAt(i));
            }
        }
    }

    /**
     * Vytvoří panel s aktualizovaný seznamem strážníků. Reaguje na základě {@link ListDataListener}.
     *
     * @return CheckBox seznam
     */
    private JPanel createCheckBoxedList() {
        JPanel panel = new JPanel(new GridBagLayout(), true);
        GridBagConstraints dpg = new GridBagConstraints();

        // general setting
        dpg.insets = new Insets(5, 5, 5, 5);
        dpg.gridx = 0;
        dpg.gridy = 0;
        dpg.anchor = GridBagConstraints.LINE_START;

        // first row //
        JCheckBox box;
        for (int i = 0; i < controller.getEmployeeCount(); i++) {
            Employee employee = controller.getEmployeeAt(i);
            box = new JCheckBox(employee.getId() + " " + employee.getFullName());
            box.setSelected(true);
            panel.add(box, dpg);
            dpg.gridy++;
        }
        return panel;
    }

    /**
     * Ukáže chybovou hlášku o špatně vyplněném poli ROK.
     */
    private void showInvalidYearDialog() {
        JOptionPane.showMessageDialog(btnGenerate, "Není buď vyplněn ROK, nebo není správně zadaný",
                "Nevyplněný ROK", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Metoda, která vytvoří šablonu a poté ji uloží.
     */
    private void createTemplate() {
        int year;
        if (!txtYear.getInputVerifier().verify(txtYear)) {
            showInvalidYearDialog();
            return;
        } else {
            year = Integer.parseInt(txtYear.getText());
        }

        JFileChooser chooser = getFileChooserTemplateFile(year);

        if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            Path enteredPath = Paths.get(chooser.getSelectedFile().getPath());

            enteredPath = resolveFileExtension(enteredPath);

            // update selected employee list by their checked
            updateSelectedEmployeeList();

            // create side task
            if (Files.exists(enteredPath)) {
                if (showFileAlreadyExistDialog() != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            disableGenerateBtnAndExecute(enteredPath);
        }
    }

    /**
     * @return {@link JOptionPane} konstanty
     */
    private int showFileAlreadyExistDialog() {
        String[] options = {"Ano", "Ne"};
        return JOptionPane.showOptionDialog(
                null, "Soubor již existuje, chcete ho přepsat?",
                "Přepsat soubor?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[1]);
    }

    /**
     * Přídá k jménu souboru příponu {@code .xlsx}
     *
     * @param toResolve cesta kam se má přiřadit přípona
     * @return cesta s příponou
     */
    private Path resolveFileExtension(Path toResolve) {
        final String fileExtension = ".xlsx";
        if (!toResolve.getFileName().toString().contains(fileExtension)) {
            toResolve = toResolve.resolveSibling(toResolve.getFileName() + fileExtension);
        }
        return toResolve;
    }

    private void disableGenerateBtnAndExecute(Path selectedPath) {
        btnGenerate.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        controller.createTemplate(
                selectedPath,
                selectedEmployeeList,
                Integer.parseInt(txtYear.getText()),
                this::onTemplateCreateDone
        );
    }

    private JFileChooser getFileChooserTemplateFile(int year) {
        final String fileName = "Plán služeb " + year;
        Path defFileName = Paths.get(fileName);

        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Excel (*.xlsx)", "xlsx"));

        Path homeDir = Paths.get(System.getProperty("user.dir"));
        chooser.setCurrentDirectory(homeDir.toFile());
        chooser.setSelectedFile(defFileName.toFile());

        return chooser;
    }

    void onTemplateCreateDone(boolean success) {
        SwingUtilities.invokeLater(() -> {
            btnGenerate.setEnabled(true);
            setCursor(null);
            showSavingResultDialog(success);
        });
    }

    /**
     * Zobrazí výsledek uložení na základě pravdivosti.
     *
     * @param b hodnota dle výsledku
     */
    private void showSavingResultDialog(boolean b) {
        if (b) {
            JOptionPane.showMessageDialog(null, "Šablona úspěšně vytvořena."
                    , "Šablona vytvořena", JOptionPane.PLAIN_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Šablonu se nepodařilo vytvořit."
                    , "Chyba při generování šablony", JOptionPane.ERROR_MESSAGE);
        }
    }

}
