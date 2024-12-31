package cz.stanislavcapek.evidencepd.swingui.view.workattendance;

import com.google.inject.Inject;
import cz.stanislavcapek.evidencepd.service.workattendance.WorkAttendance;

public class WorkAttendancePanelFactory {

    private final DocumentCreatingTaskFactory documentCreatingTaskFactory;

    @Inject
    public WorkAttendancePanelFactory(DocumentCreatingTaskFactory documentCreatingTaskFactory) {
        this.documentCreatingTaskFactory = documentCreatingTaskFactory;
    }

    WorkAttendancePanel create(WorkAttendance shiftsWorkAttendance, WorkAttendance overtimesWorkAttendance) {
        return new WorkAttendancePanel(shiftsWorkAttendance, overtimesWorkAttendance, documentCreatingTaskFactory);
    }
}
