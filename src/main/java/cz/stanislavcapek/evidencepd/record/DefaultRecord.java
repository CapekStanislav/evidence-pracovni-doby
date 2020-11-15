package cz.stanislavcapek.evidencepd.record;

import cz.stanislavcapek.evidencepd.model.Month;
import cz.stanislavcapek.evidencepd.model.WorkingTimeFund;
import cz.stanislavcapek.evidencepd.shift.Shift;
import cz.stanislavcapek.evidencepd.employee.Employee;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * An instance of class {@code Record}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DefaultRecord implements Record {

    private Employee Employee;
    private Month month;
    private int year;
    private WorkingTimeFund.TypeOfWeeklyWorkingTime typeOfWeeklyWorkingTime;
    private double lastMonth;
    private Map<Integer, Shift> shifts;


}
