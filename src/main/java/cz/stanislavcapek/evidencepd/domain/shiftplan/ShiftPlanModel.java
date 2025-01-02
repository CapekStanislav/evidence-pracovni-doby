package cz.stanislavcapek.evidencepd.domain.shiftplan;

import cz.stanislavcapek.evidencepd.domain.employee.Employee;
import cz.stanislavcapek.evidencepd.service.shiftplan.ShiftPlan;
import cz.stanislavcapek.evidencepd.service.workattendance.WorkAttendance;

import java.util.Map;
import java.util.Set;

public class ShiftPlanModel implements ShiftPlan {

    @Override
    public int getYear() {
        return 0;
    }

    @Override
    public WorkAttendance getWorkAttendance(int monthNum, int id) {
        return null;
    }

    @Override
    public Map<Integer, WorkAttendance> getWorkAttendanceByMonth(int monthNum) {
        return Map.of();
    }

    @Override
    public WorkAttendance getWorkAttendanceOvertime(int monthNum, int id) {
        return null;
    }

    @Override
    public boolean isEmployee(int id) {
        return false;
    }

    @Override
    public boolean isEmployee(int id, int monthNum) throws IllegalArgumentException {
        return false;
    }

    @Override
    public Employee getEmployee(int id) {
        return null;
    }

    @Override
    public Set<Integer> getEmployeeIds() {
        return Set.of();
    }

    @Override
    public Set<Integer> getAvailableMonths() {
        return Set.of();
    }
}
