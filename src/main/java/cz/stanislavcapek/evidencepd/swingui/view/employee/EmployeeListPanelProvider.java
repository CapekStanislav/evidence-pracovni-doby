package cz.stanislavcapek.evidencepd.swingui.view.employee;

import com.google.inject.Inject;
import com.google.inject.Provider;
import cz.stanislavcapek.evidencepd.swingui.controller.EmployeeController;

public class EmployeeListPanelProvider implements Provider<EmployeeListPanel> {

    private final EmployeeController controller;

    @Inject
    public EmployeeListPanelProvider(EmployeeController controller) {
        this.controller = controller;
    }

    @Override
    public EmployeeListPanel get() {
        return new EmployeeListPanel(controller);
    }
}
