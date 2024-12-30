package cz.stanislavcapek.evidencepd.ui.component.employee.action;

import cz.stanislavcapek.evidencepd.ui.component.employee.EmployeeListPanel;
import cz.stanislavcapek.evidencepd.ui.controller.EmployeeController;
import jiconfont.icons.elusive.Elusive;
import jiconfont.swing.IconFontSwing;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Action load employees
 */
public class LoadNewEmployeesAction extends AbstractAction {

    private final EmployeeListPanel panel;
    private final EmployeeController controller;

    public LoadNewEmployeesAction(EmployeeListPanel panel, EmployeeController controller) {
        super("Načti");
        this.panel = panel;
        this.controller = controller;
        IconFontSwing.register(Elusive.getIconFont());
        final Elusive folderOpen = Elusive.FOLDER_OPEN;
        Icon loadSmall = IconFontSwing.buildIcon(folderOpen, 12);
        Icon loadLarge = IconFontSwing.buildIcon(folderOpen, 16);

        putValue(Action.SHORT_DESCRIPTION, "Načíst nový seznam zaměstnance");
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_N);
        putValue(Action.SMALL_ICON, loadSmall);
        putValue(Action.LARGE_ICON_KEY, loadLarge);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        loadEmployees();
    }

    /**
     * Obsluha načtení seznamu strážníků, smaže původní seznam a nahradí jej novým
     */
    private void loadEmployees() {
        JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
        int choice = chooser.showOpenDialog(panel);

        if (choice == JFileChooser.APPROVE_OPTION) {
            Path file = Paths.get(chooser.getSelectedFile().getAbsolutePath());
            try {
                controller.loadFromFile(file);
                showLoadingResultDialog(true);
            } catch (RuntimeException e) {
                showLoadingResultDialog(false);
            }
        }
    }

    /**
     * Ukáže dialogové okno s výsledkem načtení souboru
     *
     * @param success výsledek
     */
    private void showLoadingResultDialog(boolean success) {
        if (success) {
            JOptionPane.showMessageDialog(null, "Seznam úspěšně načten."
                    , "Načtení v pořádku", JOptionPane.PLAIN_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Seznam se nepodařilo načíst. " +
                            "Při načítání došlo k neočekávané chybě"
                    , "Chyba při načítání", JOptionPane.ERROR_MESSAGE);
        }
    }
}
