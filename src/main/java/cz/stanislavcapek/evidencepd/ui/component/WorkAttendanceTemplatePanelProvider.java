package cz.stanislavcapek.evidencepd.ui.component;

import com.google.inject.Inject;
import com.google.inject.Provider;
import cz.stanislavcapek.evidencepd.employee.EmployeeListModel;

public class WorkAttendanceTemplatePanelProvider implements Provider<WorkAttendanceTemplatePanel> {
    private final EmployeeListModel employeeListModel;

    @Inject
    public WorkAttendanceTemplatePanelProvider(EmployeeListModel employeeListModel) {
        this.employeeListModel = employeeListModel;
    }

    @Override
    public WorkAttendanceTemplatePanel get() {
        return new WorkAttendanceTemplatePanel(employeeListModel);
    }
}
