package cz.stanislavcapek.evidencepd.swingui.controller;

import com.google.inject.Inject;
import cz.stanislavcapek.evidencepd.domain.employee.Employee;
import cz.stanislavcapek.evidencepd.service.shiftplan.ShiftPlan;
import cz.stanislavcapek.evidencepd.service.shiftplan.ShiftPlanService;
import cz.stanislavcapek.evidencepd.swingui.model.EmployeeListDataListener;
import cz.stanislavcapek.evidencepd.swingui.model.EmployeeListModel;
import cz.stanislavcapek.evidencepd.swingui.model.EmployeeModelChangedListener;
import cz.stanislavcapek.evidencepd.swingui.view.template.TemplateCreatingTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

public class ShiftPlanController {

    private static final Logger log = LogManager.getLogger(ShiftPlanController.class);

    private final ShiftPlanService shiftPlanService;
    private final EmployeeListDataListener modelChangedListener;

    @Inject
    public ShiftPlanController(EmployeeListModel employeeListModel,
                               ShiftPlanService shiftPlanService
    ) {
        this.shiftPlanService = shiftPlanService;
        modelChangedListener = new EmployeeListDataListener();
        employeeListModel.addListDataListener(modelChangedListener);
    }

    public void addModelChangeListener(EmployeeModelChangedListener listener) {
        modelChangedListener.addModelChangeListener(listener);
    }

    public void createTemplate(Path path, List<Employee> selectedEmployees, int year, Consumer<Boolean> resultCallback) {
        SwingWorker<Boolean, Void> templateCreatingTask = new TemplateCreatingTask(path, selectedEmployees, year, shiftPlanService);

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
