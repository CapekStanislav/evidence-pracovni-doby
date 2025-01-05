package cz.stanislavcapek.evidencepd.domain.shiftplan;

import cz.stanislavcapek.evidencepd.domain.employee.Employee;
import cz.stanislavcapek.evidencepd.service.workattendance.WorkAttendance;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultShiftPlan implements ShiftPlan {
    private final int year;
    private final Map<Month, Map<Integer, WorkAttendance>> shiftsInYear;

    public DefaultShiftPlan(int year, Map<Month, Map<Integer, WorkAttendance>> shiftsInYear) {
        this.year = year;
        this.shiftsInYear = shiftsInYear;
    }

    @Override
    public int getYear() {
        return year;
    }

    @Override
    public WorkAttendance getWorkAttendance(int monthNum, int id) {
        return getWorkAttendance(
                getMonth(monthNum),
                id
        );
    }

    @Override
    public WorkAttendance getWorkAttendance(Month month, int employeeId) {
        return shiftsInYear.getOrDefault(month, Map.of())
                .getOrDefault(employeeId, null);
    }

    @Override
    public Map<Integer, WorkAttendance> getWorkAttendanceByMonth(int monthNum) {
        return getWorkAttendanceByMonth(getMonth(monthNum));
    }

    @Nullable
    @Override
    public Map<Integer, WorkAttendance> getWorkAttendanceByMonth(Month month) {
        return shiftsInYear.getOrDefault(month, null);
    }

    @Override
    public WorkAttendance getWorkAttendanceOvertime(int monthNum, int id) {
        return getWorkAttendanceOvertime(getMonth(monthNum), id);
    }

    @Override
    public WorkAttendance getWorkAttendanceOvertime(Month month, int employeeId) {
        return shiftsInYear.getOrDefault(month, Map.of())
                .getOrDefault(employeeId, null);
    }

    @Override
    public boolean isEmployee(int id) {
        return getEmployeeIds().contains(id);
    }

    @Override
    public boolean isEmployee(int monthNum, int id) throws IllegalArgumentException {
        return isEmployee(getMonth(monthNum), id);
    }

    @Override
    public boolean isEmployee(Month month, int employeeId) throws IllegalArgumentException {
        WorkAttendance workAttendance = shiftsInYear.getOrDefault(month, Map.of()).get(employeeId);
        return workAttendance != null
                && workAttendance.getEmployee() != null
                && workAttendance.getEmployee().getId() == employeeId;
    }

    @Override
    public Employee getEmployee(int id) {
        return shiftsInYear.values().stream()
                .flatMap(map -> map.values().stream())
                .filter(workAttendance -> workAttendance.getEmployee().getId() == id)
                .findFirst()
                .orElseThrow().getEmployee();
    }

    @Override
    public Set<Integer> getEmployeeIds() {
        return shiftsInYear.values().stream()
                .flatMap(map -> map.values().stream())
                .map(WorkAttendance::getEmployee)
                .map(Employee::getId)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Integer> getAvailableMonths() {
        return shiftsInYear.keySet().stream()
                .map(Month::getOrder)
                .collect(Collectors.toSet());
    }

    private static Month getMonth(int monthNum) {
        return Arrays.stream(Month.values())
                .filter(month -> month.getOrder() == monthNum)
                .findFirst()
                .orElseThrow();
    }
}
