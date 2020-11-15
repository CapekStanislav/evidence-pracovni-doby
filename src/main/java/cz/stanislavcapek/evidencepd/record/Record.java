package cz.stanislavcapek.evidencepd.record;

import cz.stanislavcapek.evidencepd.model.Month;
import cz.stanislavcapek.evidencepd.model.WorkingTimeFund;
import cz.stanislavcapek.evidencepd.employee.Employee;
import cz.stanislavcapek.evidencepd.shift.Shift;

import java.util.Map;

/**
 * An instance of interface {@code Record}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public interface Record {

    Employee getEmployee();

    Month getMonth();

    int getYear();

    WorkingTimeFund.TypeOfWeeklyWorkingTime getTypeOfWeeklyWorkingTime();

    double getLastMonth();

    Map<Integer, Shift> getShifts();
}
