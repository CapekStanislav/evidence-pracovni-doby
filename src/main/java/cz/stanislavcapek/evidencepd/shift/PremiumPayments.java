package cz.stanislavcapek.evidencepd.shift;

/**
 * An instance of class {@code PremiumPayments}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public record PremiumPayments(double night,
                              double weekend,
                              double holiday,
                              double overtime
) {
}
