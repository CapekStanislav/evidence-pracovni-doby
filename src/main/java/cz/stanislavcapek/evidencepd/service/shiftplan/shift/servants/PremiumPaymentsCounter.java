package cz.stanislavcapek.evidencepd.service.shiftplan.shift.servants;

import cz.stanislavcapek.evidencepd.service.shiftplan.shift.PremiumPayments;
import cz.stanislavcapek.evidencepd.service.shiftplan.shift.Shift;

/**
 * Instance rozhraní {@code PremiumPaymentsCounter}
 *
 * @author Stanislav Čapek
 */
public interface PremiumPaymentsCounter {

    PremiumPayments calculate(Shift shift);
}
