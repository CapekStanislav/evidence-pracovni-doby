package cz.stanislavcapek.evidencepd.ui.component.workattendance;

import com.google.inject.Inject;
import cz.stanislavcapek.evidencepd.workattendance.WorkAttendance;

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
