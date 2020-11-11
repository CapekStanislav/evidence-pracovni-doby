package cz.stanislavcapek.evidencepd.smeny.sluzebnici;


import cz.stanislavcapek.evidencepd.smeny.Priplatky;
import cz.stanislavcapek.evidencepd.smeny.Smena;
import cz.stanislavcapek.evidencepd.svatky.Svatky;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Instance třídy {@code PříplatkySlužebníkImpl}
 *
 * @author Stanislav Čapek
 */
public class ZakladniPocitatdloPriplatku implements PocitadloPriplatku {

    // FIXME: 13.10.2020 špatně se počítají příplatky - neodpovídají noční hodiny
    // TODO: 13.10.2020 otestovat hodiny u svátků a víkendů taktéž
    @Override
    public Priplatky vypoctiPriplatky(Smena smena) {
        final Priplatky priplatky = smena.getPriplatky();
        priplatky.setNocni(getHodinNocniV2(smena));
        priplatky.setVikend(getHodinyVikendove(smena));
        priplatky.setSvatek(getHodinySvatku(smena));
        return priplatky;
    }

    /**
     * Metoda vrátí počet odpracovaných hodin v nočních hodinách (od 22 do 6).
     *
     * @param smena
     * @return počet hodiny
     */
    private double getHodinyNocni(Smena smena) {
        final LocalTime zacatek = smena.getZacatek().toLocalTime();
        final LocalTime konec = smena.getKonec().toLocalTime();
        final LocalTime six = LocalTime.of(6, 0);
        final LocalTime twentytwo = LocalTime.of(22, 0);
        final LocalTime mid = LocalTime.MAX;
        Duration duration = Duration.ZERO;

        // if over midnight
        if (zacatek.isAfter(konec)) {
            if (!Duration.between(twentytwo, zacatek).isNegative()) {
                final Duration toMid = Duration.between(zacatek, mid).plusNanos(1);
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
            if (zacatek.getHour() < six.getHour()) {
                final Duration toSix = Duration.between(zacatek, six);
                duration = duration.plus(toSix);
            } else if (zacatek.getHour() >= twentytwo.getHour()) {
                final Duration toMid = Duration.between(zacatek, mid).plusNanos(1);
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

        final Duration length = Duration.ofMinutes((long) (smena.getPracovniDoba().getOdpracovano() * 60));
        duration = duration.compareTo(length) > 0 ? length : duration;
        return duration.toMinutes() / 60.0;
    }

    private double getHodinNocniV2(Smena smena) {
        final LocalDateTime zacatek = smena.getZacatek();
        final LocalDateTime konec = smena.getKonec();
        Duration duration = Duration.ZERO;
        Duration minutes = Duration.ZERO;

        final LocalTime sest = LocalTime.of(6, 0);
        final LocalTime dvacetdva = LocalTime.of(22, 0);

        if (zacatek.isAfter(konec)) {
            throw new IllegalStateException("Začátek směny nemůže být po konci směny");
        }

        LocalDateTime temp = zacatek;
        while (temp.compareTo(konec) < 0) {
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
     * @param smena
     * @return desetinné číslo představující délku služby v hodinách
     */
    private double getHodinyVikendove(Smena smena) {
        LocalDateTime zacatek = smena.getZacatek();
        LocalDateTime konec = smena.getKonec();

        double hodiny = 0.0;
        DayOfWeek den = zacatek.getDayOfWeek();
        if (den == DayOfWeek.FRIDAY || den == DayOfWeek.SATURDAY || den == DayOfWeek.SUNDAY) {
            int porovnani = zacatek.toLocalDate().compareTo(konec.toLocalDate());
            Duration doPulnoci = Duration.between(zacatek, LocalDateTime.of(zacatek.toLocalDate(), LocalTime.MAX));
            Duration odPulnoci = Duration.between(LocalDateTime.of(konec.toLocalDate(), LocalTime.MIDNIGHT), konec);
            switch (den) {
                case FRIDAY:
                    if (porovnani < 0) {
                        hodiny += (odPulnoci.toMinutes()) / 60.0;
                    }
                    break;
                case SATURDAY:
                    hodiny += smena.getPracovniDoba().getOdpracovano();
                    break;
                case SUNDAY:
                    if (porovnani < 0) {
                        hodiny += (doPulnoci.toMinutes() + 1) / 60.0;
                    } else {
                        hodiny += smena.getPracovniDoba().getOdpracovano();
                    }
                    break;
            }
        }
        return hodiny;
    }

    private double getHodinySvatku(Smena smena) {
        final LocalDateTime zacatek = smena.getZacatek();
        final LocalDateTime konec = smena.getKonec();

        final LocalDate zacLocDat = zacatek.toLocalDate();
        final LocalDate konLocDat = konec.toLocalDate();
        if (zacLocDat.isEqual(konLocDat)) {
            // je to přes den
            if (isDatumSvatek(zacLocDat)) {
                return smena.getPracovniDoba().getOdpracovano();
            }
        } else {
            // je to přes noc
            Duration count = Duration.ZERO;
            final LocalTime midnightTo = LocalTime.MAX;
            final LocalTime midnightFrom = LocalTime.MIN;
            if (isDatumSvatek(zacLocDat)) {
                final Duration durToMidnight = Duration.between(
                        zacatek,
                        LocalDateTime.of(zacLocDat, midnightTo)
                );
                count = count.plus(durToMidnight).plusNanos(1);
            }
            if (isDatumSvatek(konLocDat)) {
                final Duration durFromMidnight = Duration.between(
                        LocalDateTime.of(konLocDat, midnightFrom),
                        konec
                );
                count = count.plus(durFromMidnight);
            }
            return count.toMinutes() / 60d;
        }
        return 0;
    }

    private boolean isDatumSvatek(LocalDate datum) {
        final List<LocalDate> datumySvatku = Svatky.getInstance(datum.getYear()).getDatumySvatku();
        return datumySvatku.stream().anyMatch(date -> date.isEqual(datum));
    }
}
