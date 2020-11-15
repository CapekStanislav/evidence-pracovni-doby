package cz.stanislavcapek.evidencepd.shift.servants;

import cz.stanislavcapek.evidencepd.shift.PremiumPayments;
import cz.stanislavcapek.evidencepd.shift.Shift;

/**
 * Instance rozhraní {@code PremiumPaymentsCounter}
 *
 * @author Stanislav Čapek
 */
public interface PremiumPaymentsCounter {

    PremiumPayments calculate(Shift shift);
}
