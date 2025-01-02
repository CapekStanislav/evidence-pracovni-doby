package cz.stanislavcapek.evidencepd.service.shiftplan;

import cz.stanislavcapek.evidencepd.domain.employee.Employee;
import cz.stanislavcapek.evidencepd.service.workattendance.WorkAttendance;

import java.util.Map;
import java.util.Set;

public interface ShiftPlan {
    int getYear();

    WorkAttendance getWorkAttendance(int monthNum, int id);

    Map<Integer, WorkAttendance> getWorkAttendanceByMonth(int monthNum);

    WorkAttendance getWorkAttendanceOvertime(int monthNum, int id);

    boolean isEmployee(int id);

    boolean isEmployee(int id, int monthNum) throws IllegalArgumentException;

    Employee getEmployee(int id);

    Set<Integer> getEmployeeIds();

    Set<Integer> getAvailableMonths();
}
