package cz.stanislavcapek.evidencepd.swingui.view.template;

import cz.stanislavcapek.evidencepd.domain.employee.Employee;
import cz.stanislavcapek.evidencepd.service.shiftplan.ShiftPlanService;
import cz.stanislavcapek.evidencepd.service.template.XlsxTemplateFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import java.nio.file.Path;
import java.util.List;

/**
 * Třída, která vytvoří podpůrné vlákno a uloží vygenerovanou šablonu.
 */
public class TemplateCreatingTask extends SwingWorker<Boolean, Void> {

    private static final Logger log = LogManager.getLogger(TemplateCreatingTask.class);

    private final Path path;
    private final List<Employee> selectedEmployees;
    private final int year;
    private final ShiftPlanService service;

    public TemplateCreatingTask(
            Path path,
            List<Employee> selectedEmployees,
            int year,
            ShiftPlanService service) {
        this.path = path;
        this.selectedEmployees = selectedEmployees;
        this.year = year;
        this.service = service;
    }


    @Override
    protected Boolean doInBackground() {
        XSSFWorkbook workbook = XlsxTemplateFactory.create(selectedEmployees, year);
        try {
            service.saveTemplate(path, workbook);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }
}
