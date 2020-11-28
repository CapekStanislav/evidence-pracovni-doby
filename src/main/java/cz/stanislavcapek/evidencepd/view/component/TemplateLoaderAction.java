package cz.stanislavcapek.evidencepd.view.component;

import cz.stanislavcapek.evidencepd.dao.Dao;
import cz.stanislavcapek.evidencepd.shiftplan.ShiftPlan;
import cz.stanislavcapek.evidencepd.shiftplan.XlsxDao;
import cz.stanislavcapek.evidencepd.model.WorkingTimeFund;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.nio.file.Path;

/**
 * Instance třídy {@code TemplateLoaderAction}
 *
 * @author Stanislav Čapek
 */
public class TemplateLoaderAction extends AbstractAction {

    private JFileChooser fileChooser = new JFileChooser();
    private ShiftPlan shiftPlan;
    // TODO: 01.03.2020 dodělat ikonky

    public TemplateLoaderAction(String name) {
        super(name);
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
            shiftPlan = nactiPlanSluzeb(source);
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

    public ShiftPlan getPlanSmen() {
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
    private ShiftPlan nactiPlanSluzeb(JComponent component) throws Exception {
        int vracenaHodnota = fileChooser.showOpenDialog(component);

        if (vracenaHodnota == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            final Dao<XSSFWorkbook> io = new XlsxDao();
            XSSFWorkbook workbook = io.load(Path.of(file.toURI()));
            return new ShiftPlan(workbook, WorkingTimeFund.TypeOfWeeklyWorkingTime.MULTISHIFT_CONTINUOUS);
        }
        return null;
    }
}
