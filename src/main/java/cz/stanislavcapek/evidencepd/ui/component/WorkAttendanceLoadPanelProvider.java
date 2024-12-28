package cz.stanislavcapek.evidencepd.ui.component;

import com.google.inject.Inject;
import com.google.inject.Provider;
import cz.stanislavcapek.evidencepd.employee.EmployeeListModel;
import cz.stanislavcapek.evidencepd.ui.component.workattendance.WorkAttendanceWindowFactory;
import cz.stanislavcapek.evidencepd.ui.controller.TemplateController;

public class WorkAttendanceLoadPanelProvider implements Provider<WorkAttendanceLoadPanel> {

    private final WorkAttendanceWindowFactory workAttendanceWindowFactory;
    private final EmployeeListModel employeeListModel;
    private final TemplateController templateController;

    @Inject
    public WorkAttendanceLoadPanelProvider(
            WorkAttendanceWindowFactory workAttendanceWindowFactory,
            EmployeeListModel employeeListModel,
            TemplateController templateController
    ) {
        this.workAttendanceWindowFactory = workAttendanceWindowFactory;
        this.employeeListModel = employeeListModel;
        this.templateController = templateController;
    }

    @Override
    public WorkAttendanceLoadPanel get() {
        return new WorkAttendanceLoadPanel(workAttendanceWindowFactory, templateController, employeeListModel);
    }
}
