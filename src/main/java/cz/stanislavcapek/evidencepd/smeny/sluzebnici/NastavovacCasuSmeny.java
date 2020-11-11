package cz.stanislavcapek.evidencepd.smeny.sluzebnici;

import cz.stanislavcapek.evidencepd.smeny.Smena;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * An instance of class {@code NastavovacCasuSmeny}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public final class NastavovacCasuSmeny {

    public enum TypCasu {
        /**
         * Označení začátku směny
         */
        ZACATEK,
        /**
         * Označení konce směny
         */
        KONEC
    }

    /**
     * @param smena směna, kde se má nastvit datum
     * @param cas   textové vyjádření času
     * @param typ   {@link TypCasu}
     * @return pozměněnná směna
     */
    public static Smena nastavCas(Smena smena, Object cas, TypCasu typ) {

        LocalTime time = prevedNaLocalTime(cas);

        if (time != null) {
            LocalDateTime zacatek;
            LocalDateTime konec;

            zacatek = typ == TypCasu.ZACATEK ? smena.getZacatek().with(time) : smena.getZacatek();

            konec = typ == TypCasu.KONEC ? smena.getZacatek().with(time) : smena.getKonec();
            konec = zacatek.toLocalTime().isAfter(konec.toLocalTime()) &&
                    zacatek.toLocalDate().isEqual(konec.toLocalDate()) ? konec.plusDays(1) : konec;
            konec = zacatek.toLocalTime().isBefore(konec.toLocalTime()) &&
                    zacatek.toLocalDate().isBefore(konec.toLocalDate()) ? konec.minusDays(1) : konec;

            nastavSmeneDatumy(smena, zacatek, konec);
        }
        return smena;
    }

    private static LocalTime prevedNaLocalTime(Object cas) {
        LocalTime time = null;
        try {
            final int hour = Integer.parseInt(cas.toString());
            time = LocalTime.of(hour, 0);
        } catch (NumberFormatException ignore) {
        }

        try {
            time = LocalTime.parse(cas.toString(), DateTimeFormatter.ofPattern("H:mm"));
        } catch (DateTimeParseException ignore) {
        }
        return time;
    }

    private static void nastavSmeneDatumy(Smena smena, LocalDateTime zacatek, LocalDateTime konec) {
        smena.setZacatek(zacatek);
        smena.setKonec(konec);
    }

}
