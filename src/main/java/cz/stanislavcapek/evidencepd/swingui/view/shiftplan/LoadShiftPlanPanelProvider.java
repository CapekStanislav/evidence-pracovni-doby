package cz.stanislavcapek.evidencepd.swingui.view.shiftplan;

import com.google.inject.Inject;
import com.google.inject.Provider;
import cz.stanislavcapek.evidencepd.swingui.controller.ShiftPlanController;
import cz.stanislavcapek.evidencepd.swingui.model.EmployeeListModel;
import cz.stanislavcapek.evidencepd.swingui.view.workattendance.WorkAttendanceWindowFactory;

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
