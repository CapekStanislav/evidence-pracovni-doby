package cz.stanislavcapek.evidencepd.ui.component.template;

import com.google.inject.Inject;
import com.google.inject.Provider;
import cz.stanislavcapek.evidencepd.ui.controller.ShiftPlanController;

public class WorkAttendanceTemplatePanelProvider implements Provider<WorkAttendanceTemplatePanel> {
    private final ShiftPlanController controller;

    @Inject
    public WorkAttendanceTemplatePanelProvider(ShiftPlanController controller) {
        this.controller = controller;
    }

    @Override
    public WorkAttendanceTemplatePanel get() {
        return new WorkAttendanceTemplatePanel(controller);
    }
}
