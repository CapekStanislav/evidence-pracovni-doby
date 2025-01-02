package cz.stanislavcapek.evidencepd.service.shiftplan.shift;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * The {@link ShiftFactory} interface is a factory for creating {@link Shift} objects.
 * The factory can be configured to create shifts within a specific year and month.
 * If not configured, the factory will default to the first month of the current year.
 *
 * @author Stanislav Čapek
 */
public interface ShiftFactory {
    /**
     * Creates a new {@link Shift} instance starting on the given date with default start time and duration.
     *
     * @param date the start date of the shift
     * @return the new shift
     */
    Shift createShift(LocalDate date);

    /**
     * Creates a new {@link Shift} instance based on the given date and shift type.
     * The shift's duration and start time are determined by the `TwelveHourShiftType`.
     *
     * @param date                the start date of the shift
     * @param twelveHourShiftType the type of shift
     * @return the new shift
     */
    Shift createShift(LocalDate date, TwelveHourShiftType twelveHourShiftType);

    /**
     * Creates a new {@link Shift} instance based on the given start and end times and type.
     *
     * @param start the start of the shift
     * @param end   the end of the shift
     * @param type  the `TwelveHourShiftType`, important for further calculations
     * @return the new shift
     */
    Shift createShift(LocalDateTime start, LocalDateTime end, TwelveHourShiftType type);

    /**
     * Creates a new {@link Shift} instance starting on the given date with a specified duration in hours.
     *
     * @param date   the start date of the shift
     * @param length the duration of the shift in hours
     * @return the new shift
     */
    Shift createShift(LocalDate date, double length);

    /**
     * Sets the factory to a new month. If the year has not been set using the `setYear(int)` method,
     * the current year will be used.
     *
     * @param month the new month (1-12)
     */
    void setMonth(int month);

    /**
     * Gets the currently set month.
     *
     * @return the month number
     */
    int getMonth();

    /**
     * Sets the factory to a new year.
     *
     * @param year the new year
     */
    void setYear(int year);

    /**
     * Gets the currently set year.
     *
     * @return the set year
     */
    int getYear();

    /**
     * Sets the factory to a new period, ignoring the specific day.
     *
     * @param period the new period (month and year)
     */
    void setPeriod(LocalDate period);
}