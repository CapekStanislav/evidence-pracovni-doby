package cz.stanislavcapek.evidencepd.swingui.controller;

import com.google.inject.Inject;
import cz.stanislavcapek.evidencepd.domain.employee.Employee;
import cz.stanislavcapek.evidencepd.domain.shiftplan.ShiftPlan;
import cz.stanislavcapek.evidencepd.service.shiftplan.ShiftPlanService;
import cz.stanislavcapek.evidencepd.swingui.model.EmployeeListDataListener;
import cz.stanislavcapek.evidencepd.swingui.model.EmployeeListModel;
import cz.stanislavcapek.evidencepd.swingui.model.EmployeeModelChangedListener;
import cz.stanislavcapek.evidencepd.swingui.model.ShiftPlanLoadedListener;
import cz.stanislavcapek.evidencepd.swingui.view.template.TemplateCreatingTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ShiftPlanController {

    private static final Logger log = LogManager.getLogger(ShiftPlanController.class);

    private final EmployeeListModel employeeListModel;
    private final ShiftPlanService shiftPlanService;
    private final EmployeeListDataListener modelChangedListener;
    private final List<ShiftPlanLoadedListener> shiftPlanLoadedListeners = new ArrayList<>();

    @Inject
    public ShiftPlanController(EmployeeListModel employeeListModel,
                               ShiftPlanService shiftPlanService
    ) {
        this.employeeListModel = employeeListModel;
        this.shiftPlanService = shiftPlanService;
        modelChangedListener = new EmployeeListDataListener();
        employeeListModel.addListDataListener(modelChangedListener);
    }

    public void addModelChangeListener(EmployeeModelChangedListener listener) {
        modelChangedListener.addModelChangeListener(listener);
    }

    public void addShiftPlanLoaded(ShiftPlanLoadedListener listener) {
        shiftPlanLoadedListeners.add(listener);
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

    /**
     * Metoda pro načtení souboru ve formatu xlsx.
     */
    public void selectShiftPlanFile(JComponent source) {
        JFileChooser fileChooser = setupFileChooser();
        int response = fileChooser.showOpenDialog(source);

        if (response == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            ShiftPlan shiftPlan = loadShiftPlan(file.toPath());
            shiftPlanLoadedListeners.forEach(l -> l.loaded(shiftPlan));
        }
    }

    private JFileChooser setupFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel (*.xlsx)", "xlsx"));
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setApproveButtonText("Načíst");
        fileChooser.setDialogTitle("Načíst plán směn");
        return fileChooser;
    }

    @Nullable
    private ShiftPlan loadShiftPlan(Path location) {
        try {
            return shiftPlanService.loadShiftPlan(location);
        } catch (Exception e) {
            log.error("Loading shift plan from file '{}' failed.", location.toString(), e);
            return null;
        }
    }

    public List<Employee> getMissingEmployees(ShiftPlan shiftPlan) {
        Set<Integer> employeeIds = shiftPlan.getEmployeeIds();

        return employeeIds.stream()
                .filter(Predicate.not(employeeListModel::containsEmployee))
                .map(shiftPlan::getEmployee)
                .toList();
    }
}
