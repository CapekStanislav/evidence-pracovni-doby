package cz.stanislavcapek.evidencepd.smeny.sluzebnici;

import cz.stanislavcapek.evidencepd.smeny.PracovniDoba;
import cz.stanislavcapek.evidencepd.smeny.Smena;
import cz.stanislavcapek.evidencepd.smeny.TypSmeny;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * An instance of class {@code DvanactiHodSmenyPocitadloPracovniDoby}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public class DvanactiHodSmenyPocitadloPracovniDoby implements PocitadloPracovniDoby {

    /**
     * @param smena směna pro kterou se pracovní doba počítá
     * @return vypočtená pracovní doba
     * @throws IllegalStateException pro neznámý typ směn {@link TypSmeny}
     */
    @Override
    public PracovniDoba vypoctiPracovniDobu(Smena smena) {
        final PracovniDoba pracovniDoba = smena.getPracovniDoba();
        final LocalDateTime zacatek = smena.getZacatek();
        final LocalDateTime konec = smena.getKonec();
        final TypSmeny typSmeny = smena.getTypSmeny();

        final Duration delka = Duration.between(zacatek, konec).abs();
        final double vHodinach = delka.toMinutes() / 60d;
        pracovniDoba.setDelka(vHodinach);

        switch (typSmeny) {
            case SKOLENI:
                nastavPracovniDobu(pracovniDoba, 7.5d, 0d, 0d);
                break;
            case OSETROVANI:
            case NESCHOPNOST:
            case ZDRAV_VOLNO:
                nastavPracovniDobu(pracovniDoba, 0d, 12d, 0d);
                break;
            case DOVOLENA:
                nastavPracovniDobu(pracovniDoba, 0, 0, 12);
                break;
            case PULDENNI_DOVOLENA:
                nastavPracovniDobu(pracovniDoba, 6, 0, 6);
                break;
            case NOCNI:
            case DENNI:
                nastavPracovniDobu(pracovniDoba, vHodinach, 0, 0);
                break;
            case ZADNA:
                nastavPracovniDobu(pracovniDoba, 0d, 0d, 0d);
                break;
            default:
                throw new IllegalStateException("Neznámý typ směny: " + typSmeny);
        }

        return pracovniDoba;
    }

    private void nastavPracovniDobu(PracovniDoba pracovniDoba, double odprac, double neodprac, double dovol) {
        pracovniDoba.setOdpracovano(odprac);
        pracovniDoba.setNeodpracovano(neodprac);
        pracovniDoba.setDovolena(dovol);
    }

}
