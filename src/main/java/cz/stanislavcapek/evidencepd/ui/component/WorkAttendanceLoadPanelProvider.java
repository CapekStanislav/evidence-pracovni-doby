package cz.stanislavcapek.evidencepd.ui.component;

import com.google.inject.Inject;
import com.google.inject.Provider;
import cz.stanislavcapek.evidencepd.employee.EmployeeListModel;
import cz.stanislavcapek.evidencepd.ui.component.workattendance.WorkAttendanceWindowFactory;

public class WorkAttendanceLoadPanelProvider implements Provider<WorkAttendanceLoadPanel> {

    private final WorkAttendanceWindowFactory workAttendanceWindowFactory;
    private final EmployeeListModel employeeListModel;

    @Inject
    public WorkAttendanceLoadPanelProvider(
            WorkAttendanceWindowFactory workAttendanceWindowFactory,
            EmployeeListModel employeeListModel
    ) {
        this.workAttendanceWindowFactory = workAttendanceWindowFactory;
        this.employeeListModel = employeeListModel;
    }

    @Override
    public WorkAttendanceLoadPanel get() {
        return new WorkAttendanceLoadPanel(workAttendanceWindowFactory, employeeListModel);
    }
}
