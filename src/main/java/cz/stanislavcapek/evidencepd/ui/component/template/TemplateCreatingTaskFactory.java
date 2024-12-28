package cz.stanislavcapek.evidencepd.ui.component.template;

import com.google.inject.Inject;
import cz.stanislavcapek.evidencepd.employee.Employee;
import cz.stanislavcapek.evidencepd.shiftplan.ShiftPlanService;

import javax.swing.*;
import java.nio.file.Path;
import java.util.List;

public class TemplateCreatingTaskFactory {

    private final ShiftPlanService shiftPlanService;

    @Inject
    public TemplateCreatingTaskFactory(ShiftPlanService shiftPlanService) {
        this.shiftPlanService = shiftPlanService;
    }

    public SwingWorker<Boolean, Void> create(Path path, List<Employee> employees, int year) {
        return new TemplateCreatingTask(path, employees, year, shiftPlanService);
    }
}
