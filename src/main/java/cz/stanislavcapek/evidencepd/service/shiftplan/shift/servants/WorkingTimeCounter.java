package cz.stanislavcapek.evidencepd.service.shiftplan.shift.servants;

import cz.stanislavcapek.evidencepd.service.shiftplan.shift.Shift;
import cz.stanislavcapek.evidencepd.service.shiftplan.shift.WorkingTime;

/**
 * An instance of interface {@code WorkingTimeCounter}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public interface WorkingTimeCounter {

    WorkingTime calulate(Shift shift);
}
