package cz.stanislavcapek.evidencepd;

import com.google.inject.AbstractModule;
import cz.stanislavcapek.evidencepd.employee.EmployeeListModel;
import cz.stanislavcapek.evidencepd.employee.EmployeeListModelProvider;
import cz.stanislavcapek.evidencepd.ui.component.LoadShiftPlanPanel;
import cz.stanislavcapek.evidencepd.ui.component.LoadShiftPlanPanelProvider;
import cz.stanislavcapek.evidencepd.ui.component.employee.EmployeeListPanel;
import cz.stanislavcapek.evidencepd.ui.component.employee.EmployeeListPanelProvider;
import cz.stanislavcapek.evidencepd.ui.component.template.WorkAttendanceTemplatePanel;
import cz.stanislavcapek.evidencepd.ui.component.template.WorkAttendanceTemplatePanelProvider;

public class MainModule extends AbstractModule {

    @Override
    public void configure() {
        bind(LoadShiftPlanPanel.class).toProvider(LoadShiftPlanPanelProvider.class);
        bind(EmployeeListModel.class).toProvider(EmployeeListModelProvider.class);
        bind(EmployeeListPanel.class).toProvider(EmployeeListPanelProvider.class);
        bind(WorkAttendanceTemplatePanel.class).toProvider(WorkAttendanceTemplatePanelProvider.class);
    }

}
