package cz.stanislavcapek.evidencepd.shift.servants;

import cz.stanislavcapek.evidencepd.shift.WorkingTime;
import cz.stanislavcapek.evidencepd.shift.Shift;

/**
 * An instance of interface {@code WorkingTimeCounter}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public interface WorkingTimeCounter {

    WorkingTime calulate(Shift shift);
}
