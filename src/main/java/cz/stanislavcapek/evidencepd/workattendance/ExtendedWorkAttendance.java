package cz.stanislavcapek.evidencepd.workattendance;

import cz.stanislavcapek.evidencepd.employee.Employee;
import cz.stanislavcapek.evidencepd.model.Month;
import cz.stanislavcapek.evidencepd.model.WorkingTimeFund;
import cz.stanislavcapek.evidencepd.shift.Shift;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * An instance of class {@code ExtendedWorkAttendance}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
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
}
