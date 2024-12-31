package cz.stanislavcapek.evidencepd.service.shiftplan.shift;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An instance of class {@code PremiumPayments}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public record PremiumPayments(
        @JsonProperty("night") double night,
        @JsonProperty("weekend") double weekend,
        @JsonProperty("holiday") double holiday,
        @JsonProperty("overtime") double overtime
) {
}
