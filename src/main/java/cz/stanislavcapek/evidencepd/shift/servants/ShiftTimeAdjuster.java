package cz.stanislavcapek.evidencepd.shift.servants;

import cz.stanislavcapek.evidencepd.shift.Shift;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * An instance of class {@code ShiftTimeAdjuster}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public final class ShiftTimeAdjuster {

    public enum TimeType {
        /**
         * Označení začátku směny
         */
        START,
        /**
         * Označení konce směny
         */
        END
    }

    /**
     * @param shift      směna, kde se má nastvit datum
     * @param timeObject textové vyjádření času
     * @param typ        {@link TimeType}
     * @return pozměněnná směna
     */
    public static Shift adjustTime(Shift shift, Object timeObject, TimeType typ) {

        LocalTime localTime = convertToLocalTime(timeObject);

        if (localTime != null) {
            LocalDateTime start;
            LocalDateTime end;

            start = typ == TimeType.START ? shift.getStart().with(localTime) : shift.getStart();

            end = typ == TimeType.END ? shift.getStart().with(localTime) : shift.getEnd();
            end = start.toLocalTime().isAfter(end.toLocalTime()) &&
                    start.toLocalDate().isEqual(end.toLocalDate()) ? end.plusDays(1) : end;
            end = start.toLocalTime().isBefore(end.toLocalTime()) &&
                    start.toLocalDate().isBefore(end.toLocalDate()) ? end.minusDays(1) : end;

            adjustShiftsTime(shift, start, end);
        }
        return shift;
    }

    private static LocalTime convertToLocalTime(Object timeObject) {
        LocalTime localTime = null;
        try {
            final int hour = Integer.parseInt(timeObject.toString());
            localTime = LocalTime.of(hour, 0);
        } catch (NumberFormatException ignore) {
        }

        try {
            localTime = LocalTime.parse(timeObject.toString(), DateTimeFormatter.ofPattern("H:mm"));
        } catch (DateTimeParseException ignore) {
        }
        return localTime;
    }

    private static void adjustShiftsTime(Shift shift, LocalDateTime start, LocalDateTime end) {
        shift.setStart(start);
        shift.setEnd(end);
    }

}
