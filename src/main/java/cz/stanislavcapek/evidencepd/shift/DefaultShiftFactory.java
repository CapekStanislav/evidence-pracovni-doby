package cz.stanislavcapek.evidencepd.shift;

import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Instance třídy {@code TovárnaNaSměny}
 *
 * @author Stanislav Čapek
 */
@EqualsAndHashCode
public class DefaultShiftFactory implements ShiftFactory {


    private static final LocalTime SEVEN_HOUR = LocalTime.of(7, 0);
    private static final LocalTime TWELVE_HOUR = LocalTime.of(19, 0);

    private LocalDate date;

    /**
     * Defaultní konstruktor továrny, který nastaví továrnu na 1.1. aktuálního roku
     */
    public DefaultShiftFactory() {
        this(LocalDate.now().withMonth(1).withDayOfMonth(1));
    }

    /**
     * Konstruktor, který nastaví hned při vzniku nastaví na požadované datum.
     * Nezáleží na dnu v měsíci. Důležítý je rok a měsíc.
     *
     * @param date požadované datum ({@code období})
     */
    private DefaultShiftFactory(LocalDate date) {
        this.date = date.withDayOfMonth(1);
    }


    /**
     * Nastaví továrnu na nový měsíc. Pokud nebyl před voláním této metody
     * nastaven rok metodou {@link ShiftFactory#setYear(int)} použije se
     * aktuální rok.
     *
     * @param month nový měsíc (1-12)
     */
    @Override
    public void setMonth(int month) {
        this.date = date.withMonth(month);
    }

    /**
     * Získá aktuálně nastavený měsíc
     *
     * @return číslo měsíce (1-12)
     */
    @Override
    public int getMonth() {
        return date.getMonthValue();
    }

    /**
     * Nastaví továrnu na nový rok.
     *
     * @param year nový rok
     */
    @Override
    public void setYear(int year) {
        this.date = LocalDate.of(year, 1, 1);
    }

    /**
     * Získá aktuálně nastavený rok.
     *
     * @return nastavený rok
     */
    @Override
    public int getYear() {
        return date.getYear();
    }

    /**
     * Nastaví továrnu na nové období, přičemž {@code nezáleží} na zadaném dnu.
     *
     * @param period nové období (měsíc a rok)
     */
    @Override
    public void setPeriod(LocalDate period) {
        date = period;
    }


    /**
     * Vytvoří novou instanci {@link Shift} dle zadaného datumu, která bude
     * mít defaultní začátek a délku.
     *
     * @param date datum začátku směny
     * @return nová směna
     */
    @Override
    public Shift createShift(LocalDate date) {
        return createShift(date, TypeOfShiftTwelveHours.DAY);
    }

    /**
     * Vytvoří novou instanci {@link Shift} dle zadaného datumu a typu směny.
     * Délka a záčátek směny se odvíjí od {@link TypeOfShiftTwelveHours}.
     *
     * @param date                   datum začátku směny
     * @param typeOfShiftTwelveHours typ požadované směny
     * @return nová směna
     */
    @Override
    public Shift createShift(LocalDate date, TypeOfShiftTwelveHours typeOfShiftTwelveHours) {
        LocalDateTime start;
        LocalDateTime end;
        switch (typeOfShiftTwelveHours) {
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
                final Shift shift = new Shift(
                        startAndEnd,
                        startAndEnd,
                        new WorkingTime(),
                        new PremiumPayments(),
                        TypeOfShiftTwelveHours.NONE
                );
                return shift;
            default:
                throw new RuntimeException("Chyba při vytváření směny. Neznámý typ směny.");
        }
        return createShift(start, end, typeOfShiftTwelveHours);
    }

    @Override
    public Shift createShift(LocalDateTime start, LocalDateTime end, TypeOfShiftTwelveHours type) {
        return new Shift(
                start,
                end,
                new WorkingTime(),
                new PremiumPayments(),
                type
        );
    }

    @Override
    public Shift createShift(LocalDate date, double length) {
        LocalDateTime start = date.atTime(SEVEN_HOUR);
        int hours = (int) length;
        int minutes = (int) ((length - hours) * 60);
        final LocalDateTime end = start.plusHours(hours).plusMinutes(minutes);
        return createShift(start, end, TypeOfShiftTwelveHours.DAY);
    }

}
