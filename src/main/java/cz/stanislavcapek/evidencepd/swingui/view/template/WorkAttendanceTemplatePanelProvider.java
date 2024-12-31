package cz.stanislavcapek.evidencepd.swingui.view.template;

import com.google.inject.Inject;
import com.google.inject.Provider;
import cz.stanislavcapek.evidencepd.swingui.controller.ShiftPlanController;

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
