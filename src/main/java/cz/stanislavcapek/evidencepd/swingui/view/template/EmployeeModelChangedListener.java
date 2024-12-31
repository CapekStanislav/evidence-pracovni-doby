package cz.stanislavcapek.evidencepd.swingui.view.template;

import cz.stanislavcapek.evidencepd.domain.employee.Employee;

import java.util.List;

@FunctionalInterface
public interface EmployeeModelChangedListener {
    void modelChanged(List<Employee> employees);
}
