package cz.stanislavcapek.evidencepd.ui.component.workattendance;

import cz.stanislavcapek.evidencepd.shiftplan.ShiftPlan;
import cz.stanislavcapek.evidencepd.ui.controller.ShiftPlanController;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

/**
 * Instance třídy {@code TemplateLoaderAction}
 *
 * @author Stanislav Čapek
 */
public class ShiftPlanLoadAction extends AbstractAction {

    private final ShiftPlanController controller;
    private final JFileChooser fileChooser = new JFileChooser();
    private ShiftPlan shiftPlan;
    // TODO: 01.03.2020 dodělat ikonky

    public ShiftPlanLoadAction(String name, ShiftPlanController controller) {
        super(name);
        this.controller = controller;
        putValue(
                Action.SHORT_DESCRIPTION,
                "Vyhledejte a načtěte excelovou šablonu."
        );
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_O);
        setupFileChooser();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        JComponent source = (JComponent) e.getSource();

        try {
            shiftPlan = loadWorkAttendancePlan(source);
            if (shiftPlan != null) {
                source.firePropertyChange("loaded", false, true);
            } else {
                throw new Exception("Nepodařilo načíst šablonu");
            }
        } catch (Exception ex) {
            source.firePropertyChange("loaded", true, false);
            ex.printStackTrace();
        }

    }

    public ShiftPlan getWorkAttendancePlan() {
        return shiftPlan;
    }

    private void setupFileChooser() {
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel (*.xlsx)", "xlsx"));
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setApproveButtonText("Otevřít");
        fileChooser.setDialogTitle("Načíst plán směn");
    }

    /**
     * Metoda pro načtení souboru ve formatu xlsx.
     */
    private ShiftPlan loadWorkAttendancePlan(JComponent component) throws Exception {
        int response = fileChooser.showOpenDialog(component);

        if (response == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                return controller.loadShiftPlan(file.toPath());
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}
