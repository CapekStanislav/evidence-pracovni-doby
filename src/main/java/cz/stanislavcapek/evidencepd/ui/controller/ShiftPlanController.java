package cz.stanislavcapek.evidencepd.ui.controller;

import com.google.inject.Inject;
import cz.stanislavcapek.evidencepd.employee.Employee;
import cz.stanislavcapek.evidencepd.employee.EmployeeListModel;
import cz.stanislavcapek.evidencepd.shiftplan.ShiftPlan;
import cz.stanislavcapek.evidencepd.shiftplan.ShiftPlanService;
import cz.stanislavcapek.evidencepd.ui.component.template.TemplateCreatingTaskFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

public class ShiftPlanController {

    private static final Logger log = LogManager.getLogger(ShiftPlanController.class);

    private final EmployeeListModel employeeListModel;
    private final ShiftPlanService shiftPlanService;
    private final TemplateCreatingTaskFactory creatingTaskFactory;

    @Inject
    public ShiftPlanController(EmployeeListModel employeeListModel,
                               ShiftPlanService shiftPlanService,
                               TemplateCreatingTaskFactory creatingTaskFactory) {
        this.employeeListModel = employeeListModel;
        this.shiftPlanService = shiftPlanService;
        this.creatingTaskFactory = creatingTaskFactory;
    }

    public Employee getEmployeeAt(int index) {
        return employeeListModel.getElementAt(index);
    }

    public void addListDataListener(ListDataListener listener) {
        employeeListModel.addListDataListener(listener);
    }

    public int getEmployeeCount() {
        return employeeListModel.getSize();
    }

    public void createTemplate(Path path, List<Employee> selectedEmployees, int year, Consumer<Boolean> resultCallback) {
        SwingWorker<Boolean, Void> templateCreatingTask = creatingTaskFactory.create(path, selectedEmployees, year);

        templateCreatingTask.addPropertyChangeListener(evt -> {
            if ("state".equals(evt.getPropertyName()) &&
                    evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {
                try {
                    Boolean result = templateCreatingTask.get();
                    resultCallback.accept(result);
                } catch (Exception e) {
                    log.error("Creating template task failed.", e);
                    resultCallback.accept(false);
                }

            }
        });

        templateCreatingTask.execute();
    }

    public ShiftPlan loadShiftPlan(Path path) {
        return shiftPlanService.loadShiftPlan(path);
    }
}
