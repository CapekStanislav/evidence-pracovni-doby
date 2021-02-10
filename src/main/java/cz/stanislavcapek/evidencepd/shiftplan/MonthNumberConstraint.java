package cz.stanislavcapek.evidencepd.shiftplan;

import cz.stanislavcapek.evidencepd.model.Month;
import cz.stanislavcapek.evidencepd.utils.Constraint;

/**
 * An instance of class {@code MonthNumberConstraint}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public class MonthNumberConstraint extends Constraint<Integer> {

    private static MonthNumberConstraint instance;

    private MonthNumberConstraint() {
        super(
                Month::isValidMonth,
                InvalidMonthNumberException::new
        );
    }

    public static MonthNumberConstraint getInstance() {
        if (instance == null) {
            instance = new MonthNumberConstraint();
        }
        return instance;
    }

}


