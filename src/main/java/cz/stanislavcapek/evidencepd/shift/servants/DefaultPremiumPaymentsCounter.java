package cz.stanislavcapek.evidencepd.shift.servants;


import cz.stanislavcapek.evidencepd.holiday.Holidays;
import cz.stanislavcapek.evidencepd.shift.PremiumPayments;
import cz.stanislavcapek.evidencepd.shift.Shift;

import java.time.*;
import java.util.List;

/**
 * Instance třídy {@code PříplatkySlužebníkImpl}
 *
 * @author Stanislav Čapek
 */
public class DefaultPremiumPaymentsCounter implements PremiumPaymentsCounter {

    // TODO: 14.11.2020 otestovat správné počítání příplatků
    @Override
    public PremiumPayments calculate(Shift shift) {
        final PremiumPayments premiumPayments = shift.getPremiumPayments();
        premiumPayments.setNight(getNightHoursV2(shift));
        premiumPayments.setWeekend(getWeekendHours(shift));
        premiumPayments.setHoliday(getHolidayHours(shift));
        return premiumPayments;
    }

    /**
     * Metoda vrátí počet odpracovaných hodin v nočních hodinách (od 22 do 6).
     *
     * @param shift
     * @return počet hodiny
     * @deprecated contains bug, not used
     */
    private double getNightHours(Shift shift) {
        final LocalTime start = shift.getStart().toLocalTime();
        final LocalTime konec = shift.getEnd().toLocalTime();
        final LocalTime six = LocalTime.of(6, 0);
        final LocalTime twentytwo = LocalTime.of(22, 0);
        final LocalTime mid = LocalTime.MAX;
        Duration duration = Duration.ZERO;

        // if over midnight
        if (start.isAfter(konec)) {
            if (!Duration.between(twentytwo, start).isNegative()) {
                final Duration toMid = Duration.between(start, mid).plusNanos(1);
                duration = duration.plus(toMid);
            } else {
                duration = duration.plusHours(2);
            }

            final Duration between = Duration.between(six, konec);
            if (between.isNegative() || between.isZero()) {
                final Duration fromMid = Duration.between(LocalTime.MIDNIGHT, konec);
                duration = duration.plus(fromMid);
            } else {
                duration = duration.plusHours(6);
            }

        } else {
            // else NOT over midnight
            // start
            if (start.getHour() < six.getHour()) {
                final Duration toSix = Duration.between(start, six);
                duration = duration.plus(toSix);
            } else if (start.getHour() >= twentytwo.getHour()) {
                final Duration toMid = Duration.between(start, mid).plusNanos(1);
                duration = duration.plus(toMid);
            }

            // end
            if (konec.getHour() <= six.getHour()) {
                final Duration fromMid = Duration.between(LocalTime.MIDNIGHT, konec);
                duration = duration.plus(fromMid);
            } else if (konec.getHour() > twentytwo.getHour()) {
                final Duration fromTwentyTwo = Duration.between(twentytwo, konec);
                duration = duration.plus(fromTwentyTwo);
            }
        }


        // limit max hours by shift's length

        final Duration length = Duration.ofMinutes((long) (shift.getWorkingHours().getWorkedOut() * 60));
        duration = duration.compareTo(length) > 0 ? length : duration;
        return duration.toMinutes() / 60.0;
    }

    private double getNightHoursV2(Shift shift) {
        final LocalDateTime start = shift.getStart();
        final LocalDateTime end = shift.getEnd();
        Duration duration = Duration.ZERO;

        if (start.isAfter(end)) {
            throw new IllegalStateException("Začátek směny nemůže být po konci směny");
        }

        LocalDateTime temp = start;
        while (temp.compareTo(end) < 0) {
            final int hour = temp.getHour();
            if (hour >= 22 || hour < 6) {
                duration = duration.plusMinutes(1);
            }

            temp = temp.plusMinutes(1);
        }

        return duration.toMinutes() / 60d;
    }

    /**
     * Metoda vrací počet odpracovaných hodin o víkendu (sobota, neděle).
     *
     * @param shift
     * @return desetinné číslo představující délku služby v hodinách
     */
    private double getWeekendHours(Shift shift) {
        LocalDateTime start = shift.getStart();
        LocalDateTime end = shift.getEnd();

        double hours = 0.0;
        DayOfWeek day = start.getDayOfWeek();
        if (day == DayOfWeek.FRIDAY || day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            int comparison = start.toLocalDate().compareTo(end.toLocalDate());
            Duration toMidnight = Duration.between(start, LocalDateTime.of(start.toLocalDate(), LocalTime.MAX));
            Duration afterMidnight = Duration.between(LocalDateTime.of(end.toLocalDate(), LocalTime.MIDNIGHT), end);
            switch (day) {
                case FRIDAY:
                    if (comparison < 0) {
                        hours += (afterMidnight.toMinutes()) / 60.0;
                    }
                    break;
                case SATURDAY:
                    hours += shift.getWorkingHours().getWorkedOut();
                    break;
                case SUNDAY:
                    if (comparison < 0) {
                        hours += (toMidnight.toMinutes() + 1) / 60.0;
                    } else {
                        hours += shift.getWorkingHours().getWorkedOut();
                    }
                    break;
            }
        }
        return hours;
    }

    private double getHolidayHours(Shift shift) {
        final LocalDateTime start = shift.getStart();
        final LocalDateTime end = shift.getEnd();

        final LocalDate startLocalDate = start.toLocalDate();
        final LocalDate endLocalDate = end.toLocalDate();
        if (startLocalDate.isEqual(endLocalDate)) {
            // is over day
            if (isHoliday(startLocalDate)) {
                return shift.getWorkingHours().getWorkedOut();
            }
        } else {
            // is over night
            Duration count = Duration.ZERO;
            final LocalTime midnightTo = LocalTime.MAX;
            final LocalTime midnightFrom = LocalTime.MIN;
            if (isHoliday(startLocalDate)) {
                final Duration durToMidnight = Duration.between(
                        start,
                        LocalDateTime.of(startLocalDate, midnightTo)
                );
                count = count.plus(durToMidnight).plusNanos(1);
            }
            if (isHoliday(endLocalDate)) {
                final Duration durFromMidnight = Duration.between(
                        LocalDateTime.of(endLocalDate, midnightFrom),
                        end
                );
                count = count.plus(durFromMidnight);
            }
            return count.toMinutes() / 60d;
        }
        return 0;
    }

    private boolean isHoliday(LocalDate day) {
        final List<LocalDate> ofHolidays = Holidays.getInstance(day.getYear()).getDatesOfHolidays();
        return ofHolidays.stream().anyMatch(day::isEqual);
    }
}
