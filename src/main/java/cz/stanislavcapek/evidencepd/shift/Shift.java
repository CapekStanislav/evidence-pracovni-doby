package cz.stanislavcapek.evidencepd.shift;

import java.time.LocalDateTime;

/**
 * An instance of class {@code Shift}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public class Shift {

    private LocalDateTime start;
    private LocalDateTime end;
    private WorkingTime workingHours;
    private PremiumPayments premiumPayments;
    private TypeOfShiftTwelveHours typeOfShiftTwelveHours;

    public Shift(
            LocalDateTime start,
            LocalDateTime end,
            WorkingTime workingHours,
            PremiumPayments premiumPayments,
            TypeOfShiftTwelveHours typeOfShiftTwelveHours
    ) {
        this.start = start;
        this.end = end;
        this.workingHours = workingHours;
        this.premiumPayments = premiumPayments;
        this.typeOfShiftTwelveHours = typeOfShiftTwelveHours;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public WorkingTime getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(WorkingTime workingHours) {
        this.workingHours = workingHours;
    }

    public PremiumPayments getPremiumPayments() {
        return premiumPayments;
    }

    public void setPremiumPayments(PremiumPayments premiumPayments) {
        this.premiumPayments = premiumPayments;
    }

    public TypeOfShiftTwelveHours getTypeOfShiftTwelveHours() {
        return typeOfShiftTwelveHours;
    }

    public void setTypeOfShiftTwelveHours(TypeOfShiftTwelveHours typeOfShiftTwelveHours) {
        this.typeOfShiftTwelveHours = typeOfShiftTwelveHours;
    }
}
