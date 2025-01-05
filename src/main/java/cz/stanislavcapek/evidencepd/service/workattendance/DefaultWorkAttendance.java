package cz.stanislavcapek.evidencepd.service.workattendance;

import cz.stanislavcapek.evidencepd.domain.employee.Employee;
import cz.stanislavcapek.evidencepd.model.Month;
import cz.stanislavcapek.evidencepd.service.shiftplan.WorkingTimeFund;
import cz.stanislavcapek.evidencepd.service.shiftplan.shift.Shift;

import java.util.Map;

/**
 * An instance of class {@code WorkAttendance}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public class DefaultWorkAttendance implements WorkAttendance {

    private Employee employee;
    private Month month;
    private int year;
    private WorkingTimeFund.TypeOfWeeklyWorkingTime typeOfWeeklyWorkingTime;
    private double lastMonth;
    private Map<Integer, Shift> shifts;

    public DefaultWorkAttendance(Employee employee, Month month, int year, WorkingTimeFund.
            TypeOfWeeklyWorkingTime typeOfWeeklyWorkingTime, double lastMonth, Map<Integer, Shift> shifts) {
        this.employee = employee;
        this.month = month;
        this.year = year;
        this.typeOfWeeklyWorkingTime = typeOfWeeklyWorkingTime;
        this.lastMonth = lastMonth;
        this.shifts = shifts;
    }

    public DefaultWorkAttendance(WorkAttendance workAttendance) {
        this.employee = workAttendance.getEmployee();
        this.month = workAttendance.getMonth();
        this.year = workAttendance.getYear();
        this.typeOfWeeklyWorkingTime = workAttendance.getTypeOfWeeklyWorkingTime();
        this.lastMonth = workAttendance.getLastMonth();
        this.shifts = workAttendance.getShifts();
    }

    @Override
    public Employee getEmployee() {
        return employee;
    }

    @Override
    public Month getMonth() {
        return month;
    }

    @Override
    public int getYear() {
        return year;
    }

    @Override
    public WorkingTimeFund.TypeOfWeeklyWorkingTime getTypeOfWeeklyWorkingTime() {
        return typeOfWeeklyWorkingTime;
    }

    @Override
    public double getLastMonth() {
        return lastMonth;
    }

    @Override
    public Map<Integer, Shift> getShifts() {
        return shifts;
    }

    public void setShifts(Map<Integer, Shift> shifts) {
        this.shifts = shifts;
    }
}
