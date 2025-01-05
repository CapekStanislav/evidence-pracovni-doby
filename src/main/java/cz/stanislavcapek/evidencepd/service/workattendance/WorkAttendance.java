package cz.stanislavcapek.evidencepd.service.workattendance;

import cz.stanislavcapek.evidencepd.domain.employee.Employee;
import cz.stanislavcapek.evidencepd.model.Month;
import cz.stanislavcapek.evidencepd.service.shiftplan.WorkingTimeFund;
import cz.stanislavcapek.evidencepd.service.shiftplan.shift.Shift;

import java.util.Map;

/**
 * An instance of interface {@code WorkAttendance}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public interface WorkAttendance {

    Employee getEmployee();

    Month getMonth();

    int getYear();

    WorkingTimeFund.TypeOfWeeklyWorkingTime getTypeOfWeeklyWorkingTime();

    double getLastMonth();

    Map<Integer, Shift> getShifts();

}
