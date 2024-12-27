package cz.stanislavcapek.evidencepd;

import com.google.inject.AbstractModule;
import cz.stanislavcapek.evidencepd.employee.EmployeeListModel;
import cz.stanislavcapek.evidencepd.employee.EmployeeListModelProvider;
import cz.stanislavcapek.evidencepd.ui.component.*;

public class MainModule extends AbstractModule {

    @Override
    public void configure() {
        bind(WorkAttendanceLoadPanel.class).toProvider(WorkAttendanceLoadPanelProvider.class);
        bind(EmployeeListModel.class).toProvider(EmployeeListModelProvider.class);
        bind(EmployeeListPanel.class).toProvider(EmployeeListPanelProvider.class);
        bind(WorkAttendanceTemplatePanel.class).toProvider(WorkAttendanceTemplatePanelProvider.class);
    }

}
