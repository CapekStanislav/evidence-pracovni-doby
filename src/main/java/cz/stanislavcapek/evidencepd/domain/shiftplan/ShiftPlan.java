package cz.stanislavcapek.evidencepd.domain.shiftplan;

import cz.stanislavcapek.evidencepd.domain.employee.Employee;
import cz.stanislavcapek.evidencepd.service.workattendance.WorkAttendance;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public interface ShiftPlan {
    int getYear();

    WorkAttendance getWorkAttendance(int monthNum, int id);

    WorkAttendance getWorkAttendance(Month month, int employeeId);

    Map<Integer, WorkAttendance> getWorkAttendanceByMonth(int monthNum);

    @Nullable
    Map<Integer, WorkAttendance> getWorkAttendanceByMonth(Month month);

    WorkAttendance getWorkAttendanceOvertime(int monthNum, int id);

    @Nullable
    WorkAttendance getWorkAttendanceOvertime(Month month, int employeeId);

    boolean isEmployee(int id);

    boolean isEmployee(int monthNum, int id) throws IllegalArgumentException;

    boolean isEmployee(Month month, int employeeId) throws IllegalArgumentException;

    @Nullable
    Employee getEmployee(int id);

    Set<Integer> getEmployeeIds();

    Set<Integer> getAvailableMonths();
}
