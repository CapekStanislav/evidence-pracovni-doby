package cz.stanislavcapek.evidencepd.ui.component;

import com.google.inject.Provider;
import cz.stanislavcapek.evidencepd.employee.EmployeeListModel;
import cz.stanislavcapek.evidencepd.employee.EmployeeService;

public class EmployeeListPanelProvider implements Provider<EmployeeListPanel> {

    private final EmployeeService employeeService;
    private final EmployeeListModel employeeListModel;

    public EmployeeListPanelProvider(EmployeeService employeeService, EmployeeListModel employeeListModel) {
        this.employeeService = employeeService;
        this.employeeListModel = employeeListModel;
    }

    @Override
    public EmployeeListPanel get() {
        return new EmployeeListPanel(employeeService, employeeListModel);
    }
}
