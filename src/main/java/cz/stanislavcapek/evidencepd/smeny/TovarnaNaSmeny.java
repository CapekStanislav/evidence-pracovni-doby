package cz.stanislavcapek.evidencepd.smeny;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Instance rozhraní {@code ITovárnaNaSměny} představují továrnu na {@link Smena}.
 * Továrnu jde nastavit, aby vytvářela instance v určitém roce a měsíci. Není-li
 * továrna přenastavena použije se první měsíc aktuálního roku.
 *
 * @author Stanislav Čapek
 */
public interface TovarnaNaSmeny {
    /**
     * Vytvoří novou instanci {@link Smena} dle zadaného datumu, která bude
     * mít defaultní začátek a délku.
     *
     * @param datum datum začátku směny
     * @return nová směna
     */
    Smena vytvorSmenu(LocalDate datum);

    /**
     * Vytvoří novou instanci {@link Smena} dle zadaného datumu a typu směny.
     * Délka a záčátek směny se odvíjí od {@link TypSmeny}.
     *
     * @param datum    datum začátku směny
     * @param typSmeny typ požadované směny
     * @return nová směna
     */
    Smena vytvorSmenu(LocalDate datum, TypSmeny typSmeny);


    /**
     * Vytvoří novou instanci {@link Smena} dle zadaného začátku, konce a
     * typu.
     *
     * @param zacatek začátek směny
     * @param konec   konec směny
     * @param typ     {@link TypSmeny} důležitý pro další výpočty
     * @return nová směna
     */
    Smena vytvorSmenu(LocalDateTime zacatek, LocalDateTime konec, TypSmeny typ);

    /**
     * Vytvoří novou instanci {@link Smena} dle zadaného začátku se zadanou
     * délkou v hodinách.
     * @param datum začátek směny
     * @param delka délka směny v hodinách
     * @return nová směna
     */
    Smena vytvorSmenu(LocalDate datum ,double delka);

    /**
     * Nastaví továrnu na nový měsíc. Pokud nebyl před voláním této metody
     * nastaven rok metodou {@link TovarnaNaSmeny#nastavRok(int)} použije se
     * aktuální rok.
     *
     * @param mesic nový měsíc (1-12)
     */
    void nastavMesic(int mesic);

    /**
     * Získá aktuálně nastavený měsíc
     *
     * @return
     */
    int ziskejMesic();

    /**
     * Nastaví továrnu na nový rok.
     *
     * @param rok nový rok
     */
    void nastavRok(int rok);

    /**
     * Získá aktuálně nastavený rok.
     *
     * @return nastavený rok
     */
    int ziskejRok();

    /**
     * Nastaví továrnu na nové období, přičemž {@code nezáleží} na zadaném dnu.
     *
     * @param obdobi nové období (měsíc a rok)
     */
    void nastavObdobi(LocalDate obdobi);
}
