package cz.stanislavcapek.evidencepd.ui.component.workattendance;

import com.google.inject.Inject;
import cz.stanislavcapek.evidencepd.shiftplan.ShiftPlan;

public class WorkAttendanceWindowFactory {

    private final WorkAttendancePanelFactory workAttendancePanelFactory;

    @Inject
    public WorkAttendanceWindowFactory(WorkAttendancePanelFactory workAttendancePanelFactory) {
        this.workAttendancePanelFactory = workAttendancePanelFactory;
    }

    public WorkAttendanceWindow create(ShiftPlan shiftPlan, int month) {
        return new WorkAttendanceWindow(shiftPlan, month, workAttendancePanelFactory);
    }
}
