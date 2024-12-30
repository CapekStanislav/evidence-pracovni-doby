package cz.stanislavcapek.evidencepd.ui.component;

import com.google.inject.Inject;
import com.google.inject.Provider;
import cz.stanislavcapek.evidencepd.employee.EmployeeListModel;
import cz.stanislavcapek.evidencepd.ui.component.workattendance.WorkAttendanceWindowFactory;
import cz.stanislavcapek.evidencepd.ui.controller.ShiftPlanController;

public class LoadShiftPlanPanelProvider implements Provider<LoadShiftPlanPanel> {

    private final WorkAttendanceWindowFactory workAttendanceWindowFactory;
    private final EmployeeListModel employeeListModel;
    private final ShiftPlanController shiftPlanController;

    @Inject
    public LoadShiftPlanPanelProvider(
            WorkAttendanceWindowFactory workAttendanceWindowFactory,
            EmployeeListModel employeeListModel,
            ShiftPlanController shiftPlanController
    ) {
        this.workAttendanceWindowFactory = workAttendanceWindowFactory;
        this.employeeListModel = employeeListModel;
        this.shiftPlanController = shiftPlanController;
    }

    @Override
    public LoadShiftPlanPanel get() {
        return new LoadShiftPlanPanel(workAttendanceWindowFactory, shiftPlanController, employeeListModel);
    }
}
