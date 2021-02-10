package cz.stanislavcapek.evidencepd.shiftplan;

import cz.stanislavcapek.evidencepd.utils.Constraint;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MonthNumberConstraintTest {

    public static final int VALID_MONTH_NUMBER = 2;
    public static final int INVALID_MONTH_NUMBER = 20;
    private static final Constraint<Integer> MONTH_CONSTRAINT = MonthNumberConstraint.getInstance();

    @Test
    void whenMonthNumberIsValidThenTrue() {
        assertTrue(MONTH_CONSTRAINT.check(VALID_MONTH_NUMBER));
    }

    @Test
    void whenMonthNumberIsNotValidThenFalse() {
        assertFalse(MONTH_CONSTRAINT.check(INVALID_MONTH_NUMBER));
    }

    @Test
    void whenMonthNumberIsNotValidThenThrowException() {
        assertThrows(InvalidMonthNumberException.class, () -> MONTH_CONSTRAINT.orThrow(INVALID_MONTH_NUMBER));
    }

    @Test
    void whenMonthNumberIsValidThenDoesNotThrowException() {
        assertDoesNotThrow(() -> MONTH_CONSTRAINT.orThrow(VALID_MONTH_NUMBER));
    }
}