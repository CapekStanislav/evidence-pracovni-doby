package cz.stanislavcapek.evidencepd.view.component;

import cz.stanislavcapek.evidencepd.dao.Dao;
import cz.stanislavcapek.evidencepd.plansmen.XlsxDao;
import cz.stanislavcapek.evidencepd.plansmen.PlanSmen;
import cz.stanislavcapek.evidencepd.model.FondPracovniDoby;
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
 * Instance třídy {@code NacistSablonuAction}
 *
 * @author Stanislav Čapek
 */
public class NacistSablonuAction extends AbstractAction {

    private JFileChooser fileChooser = new JFileChooser();
    private PlanSmen planSmen;
    // TODO: 01.03.2020 dodělat ikonky

    public NacistSablonuAction(String name) {
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
            planSmen = nactiPlanSluzeb(source);
            if (planSmen != null) {
                source.firePropertyChange("loaded", false, true);
            } else {
                throw new Exception("Nepodařilo načíst šablonu");
            }
        } catch (Exception ex) {
            source.firePropertyChange("loaded", true, false);
            ex.printStackTrace();
        }

    }

    public PlanSmen getPlanSmen() {
        return planSmen;
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
    private PlanSmen nactiPlanSluzeb(JComponent component) throws Exception {
        int vracenaHodnota = fileChooser.showOpenDialog(component);

        if (vracenaHodnota == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            final Dao<XSSFWorkbook> io = new XlsxDao();
            XSSFWorkbook workbook = io.nacti(Path.of(file.toURI()));
            return new PlanSmen(workbook, FondPracovniDoby.DruhTydenniPracDoby.VICESMENNY_NEPRETRZITY);
        }
        return null;
    }
}
