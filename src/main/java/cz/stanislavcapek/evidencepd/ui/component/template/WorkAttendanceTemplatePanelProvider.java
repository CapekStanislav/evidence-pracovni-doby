package cz.stanislavcapek.evidencepd.ui.component.template;

import com.google.inject.Inject;
import com.google.inject.Provider;
import cz.stanislavcapek.evidencepd.ui.controller.TemplateController;

public class WorkAttendanceTemplatePanelProvider implements Provider<WorkAttendanceTemplatePanel> {
    private final TemplateController controller;

    @Inject
    public WorkAttendanceTemplatePanelProvider(TemplateController controller) {
        this.controller = controller;
    }

    @Override
    public WorkAttendanceTemplatePanel get() {
        return new WorkAttendanceTemplatePanel(controller);
    }
}
