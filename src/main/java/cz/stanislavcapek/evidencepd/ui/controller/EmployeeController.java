package cz.stanislavcapek.evidencepd.ui.controller;

import com.google.inject.Inject;
import cz.stanislavcapek.evidencepd.employee.Employee;
import cz.stanislavcapek.evidencepd.employee.EmployeeListModel;
import cz.stanislavcapek.evidencepd.employee.EmployeeService;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.List;

public class EmployeeController {

    private final EmployeeListModel employeeListModel;
    private final EmployeeService employeeService;

    @Inject
    public EmployeeController(EmployeeListModel employeeListModel, EmployeeService employeeService) {
        this.employeeListModel = employeeListModel;
        this.employeeService = employeeService;
    }

    public EmployeeListModel getEmployeeListModel() {
        return employeeListModel;
    }

    public void loadFromFile(Path file) {
        List<Employee> employees = employeeService.load(file);
        employeeListModel.clearList();
        employees.forEach(employeeListModel::addEmployee);
    }

    public boolean addEmployee(Employee employee) {
        return employeeListModel.addEmployee(employee);
    }

    public int getEmployeeCount() {
        return employeeListModel.getSize();
    }

    public Employee getEmployeeAt(int index) {
        return employeeListModel.getElementAt(index);
    }

    public void removeEmployee(Employee employee) {
        employeeListModel.removeEmployee(employee);
    }

    public void updateEmployee(int id, @Nullable String firstName, @Nullable String lastName) {
        Employee employee = employeeListModel.searchById(id);
        if (employee == null) {
            return;
        }

        if (firstName != null) {
            employee.setFirstName(firstName);
        }

        if (lastName != null) {
            employee.setLastName(lastName);
        }

        employeeListModel.fireModelChanged();
    }
}
