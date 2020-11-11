package cz.stanislavcapek.evidencepd.utils;

/**
 * Instance rozhraní {@code Zaokrouhlovac}
 *
 * @author Stanislav Čapek
 */
public interface Zaokrouhlovac {
    /**
     * Zaokrouhlí desetinné číslo na zadané desetiné místa.
     *
     * @param kZaokrouhleni číslo k zaokrouhlení
     * @param desMista      počet desetinných míst
     * @return zaokrouhlené číslo
     */
    default double getRoundedDouble(double kZaokrouhleni, int desMista) {
        desMista = (int) Math.pow(10, desMista);
        return (double) Math.round(kZaokrouhleni * desMista) / desMista;
    }

//    /**
//     * Zaokrouhlí desetinné číslo na zadané desetiné místa.
//     *
//     * @param kZaokrouhleni číslo k zaokrouhlení
//     * @param desMista      počet desetinných míst
//     * @return zaokrouhlené číslo
//     */
//    double getRoudendDouble(double kZaokrouhleni, int desMista);
}
