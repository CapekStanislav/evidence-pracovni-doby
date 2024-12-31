package cz.stanislavcapek.evidencepd;

import com.google.inject.AbstractModule;
import cz.stanislavcapek.evidencepd.swingui.model.EmployeeListModel;
import cz.stanislavcapek.evidencepd.swingui.model.EmployeeListModelProvider;
import cz.stanislavcapek.evidencepd.swingui.view.employee.EmployeeListPanel;
import cz.stanislavcapek.evidencepd.swingui.view.employee.EmployeeListPanelProvider;
import cz.stanislavcapek.evidencepd.swingui.view.shiftplan.LoadShiftPlanPanel;
import cz.stanislavcapek.evidencepd.swingui.view.shiftplan.LoadShiftPlanPanelProvider;
import cz.stanislavcapek.evidencepd.swingui.view.template.WorkAttendanceTemplatePanel;
import cz.stanislavcapek.evidencepd.swingui.view.template.WorkAttendanceTemplatePanelProvider;

public class MainModule extends AbstractModule {

    @Override
    public void configure() {
        bind(LoadShiftPlanPanel.class).toProvider(LoadShiftPlanPanelProvider.class);
        bind(EmployeeListModel.class).toProvider(EmployeeListModelProvider.class);
        bind(EmployeeListPanel.class).toProvider(EmployeeListPanelProvider.class);
        bind(WorkAttendanceTemplatePanel.class).toProvider(WorkAttendanceTemplatePanelProvider.class);
    }

}
