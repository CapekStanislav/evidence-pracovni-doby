package cz.stanislavcapek.evidencepd.smeny;

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
public class ZakladniTovarnaNaSmeny implements TovarnaNaSmeny {


    private static final LocalTime SEDM_HODIN = LocalTime.of(7, 0);
    private static final LocalTime DEVATENACT_HODIN = LocalTime.of(19, 0);

    private LocalDate datum;

    /**
     * Defaultní konstruktor továrny, který nastaví továrnu na 1.1. aktuálního roku
     */
    public ZakladniTovarnaNaSmeny() {
        this(LocalDate.now().withMonth(1).withDayOfMonth(1));
    }

    /**
     * Konstruktor, který nastaví hned při vzniku nastaví na požadované datum.
     * Nezáleží na dnu v měsíci. Důležítý je rok a měsíc.
     *
     * @param datum požadované datum ({@code období})
     */
    private ZakladniTovarnaNaSmeny(LocalDate datum) {
        this.datum = datum.withDayOfMonth(1);
    }


    /**
     * Nastaví továrnu na nový měsíc. Pokud nebyl před voláním této metody
     * nastaven rok metodou {@link TovarnaNaSmeny#nastavRok(int)} použije se
     * aktuální rok.
     *
     * @param mesic nový měsíc (1-12)
     */
    @Override
    public void nastavMesic(int mesic) {
        this.datum = datum.withMonth(mesic);
    }

    /**
     * Získá aktuálně nastavený měsíc
     *
     * @return číslo měsíce (1-12)
     */
    @Override
    public int ziskejMesic() {
        return datum.getMonthValue();
    }

    /**
     * Nastaví továrnu na nový rok.
     *
     * @param rok nový rok
     */
    @Override
    public void nastavRok(int rok) {
        this.datum = LocalDate.of(rok, 1, 1);
    }

    /**
     * Získá aktuálně nastavený rok.
     *
     * @return nastavený rok
     */
    @Override
    public int ziskejRok() {
        return datum.getYear();
    }

    /**
     * Nastaví továrnu na nové období, přičemž {@code nezáleží} na zadaném dnu.
     *
     * @param obdobi nové období (měsíc a rok)
     */
    @Override
    public void nastavObdobi(LocalDate obdobi) {
        datum = obdobi;
    }


    /**
     * Vytvoří novou instanci {@link Smena} dle zadaného datumu, která bude
     * mít defaultní začátek a délku.
     *
     * @param datum datum začátku směny
     * @return nová směna
     */
    @Override
    public Smena vytvorSmenu(LocalDate datum) {
        return vytvorSmenu(datum, TypSmeny.DENNI);
    }

    /**
     * Vytvoří novou instanci {@link Smena} dle zadaného datumu a typu směny.
     * Délka a záčátek směny se odvíjí od {@link TypSmeny}.
     *
     * @param datum    datum začátku směny
     * @param typSmeny typ požadované směny
     * @return nová směna
     */
    @Override
    public Smena vytvorSmenu(LocalDate datum, TypSmeny typSmeny) {
        LocalDateTime zacatek;
        LocalDateTime konec;
        switch (typSmeny) {
            case DENNI:
            case DOVOLENA:
            case ZDRAV_VOLNO:
            case NESCHOPNOST:
            case OSETROVANI:
            case PULDENNI_DOVOLENA:
                zacatek = LocalDateTime.of(datum, SEDM_HODIN);
                konec = zacatek.plusHours(12);
                break;
            case NOCNI:
                zacatek = LocalDateTime.of(datum, DEVATENACT_HODIN);
                konec = zacatek.plusHours(12);
                break;
            case SKOLENI:
                zacatek = LocalDateTime.of(datum, SEDM_HODIN);
                konec = zacatek.plusHours(7).plusMinutes(30);
                break;

            case ZADNA:
                final LocalDateTime zacatekIKonec = LocalDateTime.of(datum, LocalTime.MIN);
                final Smena smena = new Smena(
                        zacatekIKonec,
                        zacatekIKonec,
                        new PracovniDoba(),
                        new Priplatky(),
                        TypSmeny.ZADNA
                );
                return smena;
            default:
                throw new RuntimeException("Chyba při vytváření směny. Neznámý typ směny.");
        }
        return vytvorSmenu(zacatek, konec, typSmeny);
    }

    @Override
    public Smena vytvorSmenu(LocalDateTime zacatek, LocalDateTime konec, TypSmeny typ) {
        return new Smena(
                zacatek,
                konec,
                new PracovniDoba(),
                new Priplatky(),
                typ
        );
    }

    @Override
    public Smena vytvorSmenu(LocalDate datum, double delka) {
        LocalDateTime zacatek = datum.atTime(SEDM_HODIN);
        int hodiny = (int) delka;
        int minuty = (int) ((delka - hodiny) * 60);
        final LocalDateTime konec = zacatek.plusHours(hodiny).plusMinutes(minuty);
        return vytvorSmenu(zacatek, konec, TypSmeny.DENNI);
    }

}
