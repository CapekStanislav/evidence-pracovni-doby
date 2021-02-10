package cz.stanislavcapek.evidencepd.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConstraintTest {

    @Test
    void whenMoreThen10ThenTrue() {
        final Constraint<Integer> intConstraint = new Constraint<>(
                integer -> integer > 10,
                integer -> new IllegalArgumentException(String.valueOf(integer))
        );

        assertTrue(intConstraint.check(11));
    }

    @Test
    void whenLessThen10ThenFalse() {
        final Constraint<Integer> intConstraint = new Constraint<>(
                integer -> integer > 10,
                integer -> new IllegalArgumentException(String.valueOf(integer))
        );

        assertFalse(intConstraint.check(5));
    }

    @Test
    void whenMoreThen10ThenThrowException() {
        final Constraint<Integer> intConstraint = new Constraint<>(
                integer -> integer <= 10,
                integer -> new IllegalArgumentException(String.valueOf(integer))
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> intConstraint.orThrow(19)
                );
    }

    @Test
    void whenLessThen10ThenDoesNotThrowException() {
        final Constraint<Integer> intConstraint = new Constraint<>(
                integer -> integer < 10,
                integer -> new IllegalArgumentException(String.valueOf(integer))
        );

       assertDoesNotThrow(() -> intConstraint.orThrow(5));
    }
}