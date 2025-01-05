package cz.stanislavcapek.evidencepd.service.workattendance;

import cz.stanislavcapek.evidencepd.domain.employee.Employee;
import cz.stanislavcapek.evidencepd.model.Month;
import cz.stanislavcapek.evidencepd.service.shiftplan.WorkingTimeFund;
import cz.stanislavcapek.evidencepd.service.shiftplan.shift.Shift;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * An instance of class {@code ExtendedWorkAttendance}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public class ExtendedWorkAttendance implements WorkAttendanceWithOvertimes {

    private Employee employee;
    private Month month;
    private int year;
    private WorkingTimeFund.TypeOfWeeklyWorkingTime typeOfWeeklyWorkingTime;
    private double lastMonth;
    private Map<Integer, Shift> shifts;
    private List<Shift> overtimes;

    public ExtendedWorkAttendance(WorkAttendance workAttendance) {
        this.employee = workAttendance.getEmployee();
        this.month = workAttendance.getMonth();
        this.year = workAttendance.getYear();
        this.typeOfWeeklyWorkingTime = workAttendance.getTypeOfWeeklyWorkingTime();
        this.lastMonth = workAttendance.getLastMonth();
        this.shifts = workAttendance.getShifts();
        this.overtimes = new ArrayList<>();
    }


    @Override
    public List<Shift> getOvertimes() {
        return overtimes;
    }

    @Override
    public Employee getEmployee() {
        return employee;
    }

    @Override
    public Month getMonth() {
        return null;
    }

    @Override
    public int getYear() {
        return 0;
    }

    @Override
    public WorkingTimeFund.TypeOfWeeklyWorkingTime getTypeOfWeeklyWorkingTime() {
        return null;
    }

    @Override
    public double getLastMonth() {
        return lastMonth;
    }

    @Override
    public Map<Integer, Shift> getShifts() {
        return shifts;
    }

    public void setOvertimes(List<Shift> overtimes) {
        this.overtimes = overtimes;
    }
}
