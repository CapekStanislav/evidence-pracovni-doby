package cz.stanislavcapek.evidencepd.utils;

/**
 * Instance rozhraní {@code Rounder}
 *
 * @author Stanislav Čapek
 */
public interface Rounder {
    /**
     * Zaokrouhlí desetinné číslo na zadané desetiné místa.
     *
     * @param toRound       číslo k zaokrouhlení
     * @param decimalPlaces počet desetinných míst
     * @return zaokrouhlené číslo
     */
    default double getRoundedDouble(double toRound, int decimalPlaces) {
        decimalPlaces = (int) Math.pow(10, decimalPlaces);
        return (double) Math.round(toRound * decimalPlaces) / decimalPlaces;
    }
}
