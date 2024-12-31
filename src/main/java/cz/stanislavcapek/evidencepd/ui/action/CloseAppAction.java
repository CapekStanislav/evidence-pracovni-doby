package cz.stanislavcapek.evidencepd.ui.action;

import cz.stanislavcapek.evidencepd.ui.controller.EmployeeController;
import jiconfont.icons.elusive.Elusive;
import jiconfont.swing.IconFontSwing;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class CloseAppAction extends AbstractAction {

    private final EmployeeController employeeController;

    public CloseAppAction(String name, String desc, int mnemonic, EmployeeController employeeController) {
        super(name);
        this.employeeController = employeeController;
        IconFontSwing.register(Elusive.getIconFont());
        final Elusive closeIcon = Elusive.OFF;
        Icon closeSmall = IconFontSwing.buildIcon(closeIcon, 16);
        Icon closeLarge = IconFontSwing.buildIcon(closeIcon, 24);

        putValue(Action.SHORT_DESCRIPTION, desc);
        putValue(Action.MNEMONIC_KEY, mnemonic);
        putValue(Action.SMALL_ICON, closeSmall);
        putValue(Action.LARGE_ICON_KEY, closeLarge);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (!employeeController.isModelSaved()) {
            showModelNotSavedDialog();
        }

        showExitDialog();
    }

    private void showModelNotSavedDialog() {
        Object[] options = {"Ano", "Ne"};
        int answer = JOptionPane.showOptionDialog(null,
                "Aktuální seznam strážníků není uložen. \n \n",
                "Chcete jej uložit?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
        if (answer == JOptionPane.YES_NO_OPTION) {
            employeeController.saveModel();
        }
    }

    private static void showExitDialog() {
        Object[] options = {"Ano", "Ne"};
        int answer = JOptionPane.showOptionDialog(null,
                "Opravdu si přejete ukončit program? \n \n",
                "Ukončit program?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
        if (answer == JOptionPane.YES_NO_OPTION) {
            System.exit(0);
        }
    }
}
