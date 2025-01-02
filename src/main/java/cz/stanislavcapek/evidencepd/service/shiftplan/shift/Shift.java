package cz.stanislavcapek.evidencepd.service.shiftplan.shift;

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
    private TwelveHourShiftType twelveHourShiftType;

    public Shift(
            LocalDateTime start,
            LocalDateTime end,
            WorkingTime workingHours,
            PremiumPayments premiumPayments,
            TwelveHourShiftType twelveHourShiftType
    ) {
        this.start = start;
        this.end = end;
        this.workingHours = workingHours;
        this.premiumPayments = premiumPayments;
        this.twelveHourShiftType = twelveHourShiftType;
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

    public TwelveHourShiftType getTypeOfShiftTwelveHours() {
        return twelveHourShiftType;
    }

    public void setTypeOfShiftTwelveHours(TwelveHourShiftType twelveHourShiftType) {
        this.twelveHourShiftType = twelveHourShiftType;
    }
}
