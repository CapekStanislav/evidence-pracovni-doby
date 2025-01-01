package cz.stanislavcapek.evidencepd.swingui.view.employee;

import com.google.inject.Inject;
import cz.stanislavcapek.evidencepd.swingui.controller.EmployeeController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class EmployeesLoader {

    private static final Logger log = LogManager.getLogger(EmployeesLoader.class);

    private final EmployeeController controller;
    private boolean defaultLocation = true;

    @Inject
    public EmployeesLoader(EmployeeController controller) {
        this.controller = controller;
    }

    public void tryLoadEmployees() {
        boolean done = false;
        while (!done) {
            try {
                done = findEmployeeListFile(defaultLocation);
            } catch (RuntimeException e) {
                log.error("Loading employees from a file failed.", e);
                done = showFileExceptionDialog();
            }
        }
    }

    private boolean findEmployeeListFile(boolean defLoc) {
        if (defLoc) {
            defaultLocation = false;
            return controller.loadModel(null, this::showFileIsEmptyDialog);
        }

        final JFileChooser chooser = getChooserForJsonFiles();

        if (chooser.showOpenDialog(null) == JOptionPane.YES_OPTION) {
            final File selectedFile = chooser.getSelectedFile();
            return controller.loadModel(selectedFile.toPath(), this::showFileIsEmptyDialog);
        }
        return true;
    }

    private JFileChooser getChooserForJsonFiles() {
        JFileChooser chooser = new JFileChooser();
        final FileNameExtensionFilter jsonFilter = new FileNameExtensionFilter("JSON (*.json)", "json");
        chooser.setFileFilter(jsonFilter);
        chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        chooser.setApproveButtonText("otevřít");
        return chooser;
    }

    private boolean showFileExceptionDialog() {
        Object[] option = {"Vyhledat", "Pokračovat"};
        final String message = """
                Soubor se seznamem zaměstnanců buď neexistuje nebo je poškozen.\s
                \s
                Vyhledat seznam ručně?""";
        final String title = "Chyba při načtení souboru";

        int choice = JOptionPane.showOptionDialog(
                null,
                message,
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE,
                null,
                option,
                option[0]);
        return choice == JOptionPane.NO_OPTION;
    }

    private int showFileIsEmptyDialog() {
        Object[] option = {"Vyhledat", "Pokračovat"};
        final String message = """
                Načtený seznam zaměstnanců je prázdný.\s
                \s
                Vyhledat seznam ručně nebo pokračovat?""";
        final String title = "Seznam zaměstnanců je prázdný";

        return JOptionPane.showOptionDialog(
                null,
                message,
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE,
                null,
                option,
                option[0]);
    }
}
