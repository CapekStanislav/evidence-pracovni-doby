package cz.stanislavcapek.evidencepd.ui.component.employee;

import com.google.inject.Inject;
import cz.stanislavcapek.evidencepd.employee.Employee;
import cz.stanislavcapek.evidencepd.employee.EmployeeListModel;
import cz.stanislavcapek.evidencepd.employee.EmployeeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.Collections;
import java.util.List;

public class EmployeesLoader {

    private static final Logger log = LogManager.getLogger(EmployeesLoader.class);
    private final EmployeeService service;
    private final EmployeeListModel employeeListModel;
    private boolean defaultLocation = true;


    @Inject
    public EmployeesLoader(EmployeeService service, EmployeeListModel employeeListModel) {
        this.service = service;
        this.employeeListModel = employeeListModel;
    }

    public void tryLoadEmployees() {
        boolean done = false;
        while (!done) {
            try {
                List<Employee> employees = findEmployeeListFile(defaultLocation);
                done = initEmployeeListModel(employees);
            } catch (RuntimeException e) {
                log.error("Unable to load employees from default location.", e);
                done = showFileExceptionDialog();
            }
        }
    }

    private boolean initEmployeeListModel(List<Employee> list) {
        if (list.isEmpty()) {
            if (showFileIsEmptyDialog() == JOptionPane.NO_OPTION) {
                employeeListModel.clearList();
                return true;
            } else {
                return false;
            }
        }

        employeeListModel.clearList();
        list.forEach(employeeListModel::addEmployee);
        return true;
    }

    private List<Employee> findEmployeeListFile(boolean defLoc) {
        if (defLoc) {
            defaultLocation = false;
            return service.load();
        }

        final JFileChooser chooser = getChooserForJsonFiles();

        if (chooser.showOpenDialog(null) == JOptionPane.YES_OPTION) {
            final File selectedFile = chooser.getSelectedFile();
            return service.load(selectedFile.toPath());
        }

        return Collections.emptyList();
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
