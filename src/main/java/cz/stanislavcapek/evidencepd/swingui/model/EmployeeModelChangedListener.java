package cz.stanislavcapek.evidencepd.swingui.model;

import cz.stanislavcapek.evidencepd.domain.employee.Employee;

import java.util.List;

@FunctionalInterface
public interface EmployeeModelChangedListener {
    void modelChanged(List<Employee> employees);
}
