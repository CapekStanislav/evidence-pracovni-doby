package cz.stanislavcapek.evidencepd.service.shiftplan.shift;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * This class serves as a default factory for creating {@link Shift} instances.
 *
 * @author Stanislav Čapek
 */
public class DefaultShiftFactory implements ShiftFactory {

    private static final LocalTime SEVEN_HOUR = LocalTime.of(7, 0);
    private static final LocalTime TWELVE_HOUR = LocalTime.of(19, 0);

    private LocalDate date;

    /**
     * Default constructor, initializing the factory to the first day of the current year.
     */
    public DefaultShiftFactory() {
        this(LocalDate.now().withMonth(1).withDayOfMonth(1));
    }

    /**
     * Constructor that sets the factory to the specified date.
     * Only the year and month are considered; the day is set to the first of the month.
     *
     * @param date the desired date
     */
    private DefaultShiftFactory(LocalDate date) {
        this.date = date.withDayOfMonth(1);
    }

    /**
     * Sets the factory to a new month. If the year has not been previously set using the {@link #setYear} method,
     * the current year will be used.
     *
     * @param month the new month (1-12)
     */
    @Override
    public void setMonth(int month) {
        this.date = date.withMonth(month);
    }

    /**
     * Gets the currently set month.
     *
     * @return the month number
     */
    @Override
    public int getMonth() {
        return date.getMonthValue();
    }

    /**
     * Sets the factory to a new year.
     *
     * @param year the new year
     */
    @Override
    public void setYear(int year) {
        this.date = LocalDate.of(year, 1, 1);
    }

    /**
     * Gets the currently set year.
     *
     * @return the set year
     */
    @Override
    public int getYear() {
        return date.getYear();
    }

    /**
     * Sets the factory to a new period, ignoring the specific day of the month.
     *
     * @param period the new period (month and year)
     */
    @Override
    public void setPeriod(LocalDate period) {
        date = period;
    }

    /**
     * Creates a new {@link Shift} instance for the given date with default start and duration.
     *
     * @param date the start date of the shift
     * @return the new shift
     */
    @Override
    public Shift createShift(LocalDate date) {
        return createShift(date, TwelveHourShiftType.DAY);
    }

    /**
     * Creates a new {@link Shift} instance based on the given date and shift type.
     * The duration and start time of the shift are determined by the `TwelveHourShiftType`.
     *
     * @param date                the start date of the shift
     * @param twelveHourShiftType the type of shift
     * @return the new shift
     */
    @Override
    public Shift createShift(LocalDate date, TwelveHourShiftType twelveHourShiftType) {
        LocalDateTime start;
        LocalDateTime end;
        switch (twelveHourShiftType) {
            case DAY:
            case HOLIDAY:
            case SICK_DAY:
            case INABILITY:
            case HOME_CARE:
            case HALF_HOLIDAY:
                start = LocalDateTime.of(date, SEVEN_HOUR);
                end = start.plusHours(12);
                break;
            case NIGHT:
                start = LocalDateTime.of(date, TWELVE_HOUR);
                end = start.plusHours(12);
                break;
            case TRAINING:
                start = LocalDateTime.of(date, SEVEN_HOUR);
                end = start.plusHours(7).plusMinutes(30);
                break;

            case NONE:
                final LocalDateTime startAndEnd = LocalDateTime.of(date, LocalTime.MIN);
                return new Shift(
                        startAndEnd,
                        startAndEnd,
                        new WorkingTime(0, 0, 0, 0),
                        new PremiumPayments(0, 0, 0, 0),
                        TwelveHourShiftType.NONE
                );
            default:
                throw new RuntimeException("Error creating shift. Unknown shift type.");
        }
        return createShift(start, end, twelveHourShiftType);
    }

    @Override
    public Shift createShift(LocalDateTime start, LocalDateTime end, TwelveHourShiftType type) {
        return new Shift(
                start,
                end,
                new WorkingTime(0, 0, 0, 0),
                new PremiumPayments(0, 0, 0, 0),
                type
        );
    }

    @Override
    public Shift createShift(LocalDate date, double length) {
        LocalDateTime start = date.atTime(SEVEN_HOUR);
        int hours = (int) length;
        int minutes = (int) ((length - hours) * 60);
        final LocalDateTime end = start.plusHours(hours).plusMinutes(minutes);
        return createShift(start, end, TwelveHourShiftType.DAY);
    }
}