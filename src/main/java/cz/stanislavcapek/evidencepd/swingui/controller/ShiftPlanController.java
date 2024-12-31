package cz.stanislavcapek.evidencepd.swingui.controller;

import com.google.inject.Inject;
import cz.stanislavcapek.evidencepd.domain.employee.Employee;
import cz.stanislavcapek.evidencepd.service.shiftplan.ShiftPlan;
import cz.stanislavcapek.evidencepd.service.shiftplan.ShiftPlanService;
import cz.stanislavcapek.evidencepd.swingui.model.EmployeeListModel;
import cz.stanislavcapek.evidencepd.swingui.view.template.EmployeeListDataListener;
import cz.stanislavcapek.evidencepd.swingui.view.template.EmployeeModelChangedListener;
import cz.stanislavcapek.evidencepd.swingui.view.template.TemplateCreatingTaskFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

public class ShiftPlanController {

    private static final Logger log = LogManager.getLogger(ShiftPlanController.class);

    private final EmployeeListModel employeeListModel;
    private final ShiftPlanService shiftPlanService;
    private final TemplateCreatingTaskFactory creatingTaskFactory;
    private final EmployeeListDataListener modelChangedListener;

    @Inject
    public ShiftPlanController(EmployeeListModel employeeListModel,
                               ShiftPlanService shiftPlanService,
                               TemplateCreatingTaskFactory creatingTaskFactory
    ) {
        this.employeeListModel = employeeListModel;
        this.shiftPlanService = shiftPlanService;
        this.creatingTaskFactory = creatingTaskFactory;
        modelChangedListener = new EmployeeListDataListener();
        employeeListModel.addListDataListener(modelChangedListener);
    }

    public void addModelChangeListener(EmployeeModelChangedListener listener) {
        modelChangedListener.addModelChangeListener(listener);
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
