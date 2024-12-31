package cz.stanislavcapek.evidencepd.ui.component.template;

import cz.stanislavcapek.evidencepd.employee.Employee;

import java.util.List;

@FunctionalInterface
public interface EmployeeModelChangedListener {
    void modelChanged(List<Employee> employees);
}
