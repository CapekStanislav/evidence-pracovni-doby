package cz.stanislavcapek.evidencepd.ui.component;

import com.google.inject.Inject;
import com.google.inject.Provider;
import cz.stanislavcapek.evidencepd.ui.component.workattendance.WorkAttendanceWindowFactory;

public class WorkAttendanceLoadPanelProvider implements Provider<WorkAttendanceLoadPanel> {

    private final WorkAttendanceWindowFactory workAttendanceWindowFactory;

    @Inject
    public WorkAttendanceLoadPanelProvider(WorkAttendanceWindowFactory workAttendanceWindowFactory) {
        this.workAttendanceWindowFactory = workAttendanceWindowFactory;
    }

    @Override
    public WorkAttendanceLoadPanel get() {
        return new WorkAttendanceLoadPanel(workAttendanceWindowFactory);
    }
}
