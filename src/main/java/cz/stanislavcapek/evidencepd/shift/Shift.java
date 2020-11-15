package cz.stanislavcapek.evidencepd.shift;

import lombok.*;

import java.time.LocalDateTime;

/**
 * An instance of class {@code Shift}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Shift {

    private LocalDateTime start;
    private LocalDateTime end;
    private WorkingTime workingHours;
    private PremiumPayments premiumPayments;
    private TypeOfShiftTwelveHours typeOfShiftTwelveHours;

}
