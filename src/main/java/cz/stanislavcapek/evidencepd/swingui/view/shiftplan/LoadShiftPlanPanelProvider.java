package cz.stanislavcapek.evidencepd.swingui.view.shiftplan;

import com.google.inject.Inject;
import com.google.inject.Provider;
import cz.stanislavcapek.evidencepd.swingui.controller.EmployeeController;
import cz.stanislavcapek.evidencepd.swingui.controller.ShiftPlanController;
import cz.stanislavcapek.evidencepd.swingui.view.workattendance.WorkAttendanceWindowFactory;

public class LoadShiftPlanPanelProvider implements Provider<LoadShiftPlanPanel> {

    private final WorkAttendanceWindowFactory workAttendanceWindowFactory;
    private final EmployeeController employeeController;
    private final ShiftPlanController shiftPlanController;

    @Inject
    public LoadShiftPlanPanelProvider(
            WorkAttendanceWindowFactory workAttendanceWindowFactory,
            EmployeeController employeeController,
            ShiftPlanController shiftPlanController
    ) {
        this.workAttendanceWindowFactory = workAttendanceWindowFactory;
        this.employeeController = employeeController;
        this.shiftPlanController = shiftPlanController;
    }

    @Override
    public LoadShiftPlanPanel get() {
        return new LoadShiftPlanPanel(workAttendanceWindowFactory, shiftPlanController, employeeController);
    }
}
