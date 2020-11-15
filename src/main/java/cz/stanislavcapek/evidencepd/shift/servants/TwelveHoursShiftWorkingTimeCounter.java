package cz.stanislavcapek.evidencepd.shift.servants;

import cz.stanislavcapek.evidencepd.shift.WorkingTime;
import cz.stanislavcapek.evidencepd.shift.Shift;
import cz.stanislavcapek.evidencepd.shift.TypeOfShiftTwelveHours;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * An instance of class {@code TwelveHoursShiftWorkingTimeCounter}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public class TwelveHoursShiftWorkingTimeCounter implements WorkingTimeCounter {

    /**
     * @param shift směna pro kterou se pracovní doba počítá
     * @return vypočtená pracovní doba
     * @throws IllegalStateException pro neznámý typ směn {@link TypeOfShiftTwelveHours}
     */
    @Override
    public WorkingTime calulate(Shift shift) {
        final WorkingTime workingTime = shift.getWorkingHours();
        final LocalDateTime start = shift.getStart();
        final LocalDateTime end = shift.getEnd();
        final TypeOfShiftTwelveHours typeOfShiftTwelveHours = shift.getTypeOfShiftTwelveHours();

        final Duration length = Duration.between(start, end).abs();
        final double inHours = length.toMinutes() / 60d;
        workingTime.setLength(inHours);

        switch (typeOfShiftTwelveHours) {
            case TRAINING:
                setWorkingTime(workingTime, 7.5d, 0d, 0d);
                break;
            case HOME_CARE:
            case INABILITY:
            case SICK_DAY:
                setWorkingTime(workingTime, 0d, 12d, 0d);
                break;
            case HOLIDAY:
                setWorkingTime(workingTime, 0, 0, 12);
                break;
            case HALF_HOLIDAY:
                setWorkingTime(workingTime, 6, 0, 6);
                break;
            case NIGHT:
            case DAY:
                setWorkingTime(workingTime, inHours, 0, 0);
                break;
            case NONE:
                setWorkingTime(workingTime, 0d, 0d, 0d);
                break;
            default:
                throw new IllegalStateException("Neznámý typ směny: " + typeOfShiftTwelveHours);
        }

        return workingTime;
    }

    private void setWorkingTime(WorkingTime workingTime, double odprac, double neodprac, double dovol) {
        workingTime.setWorkedOut(odprac);
        workingTime.setNotWorkedOut(neodprac);
        workingTime.setHoliday(dovol);
    }

}
