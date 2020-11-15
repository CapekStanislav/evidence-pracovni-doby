package cz.stanislavcapek.evidencepd.shift;

import lombok.Data;

/**
 * An instance of class {@code PremiumPayments}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
@Data
public class PremiumPayments {
    private double night;
    private double weekend;
    private double holiday;
    private double overtime;

}
