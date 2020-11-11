package cz.stanislavcapek.evidencepd.smeny;

import lombok.Data;

/**
 * An instance of class {@code PracovniDoba}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
@Data
public class PracovniDoba {

    /**
     * Celková délka směny
     */
    private double delka;
    /**
     * Odpracované hodiny ve smyslu fyzické přítomnosti na pracovišti.
     * Započitatelné do pracovní doby
     */
    private double odpracovano;
    /**
     * Neodpracované hodiny ve smyslu hodin započitatelných do pracovní
     * doby, které nebyly fyzicky odpracované (např. neschopnost)
     */
    private double neodpracovano;

    /**
     * Hodiny, které se považují za dovolenou
     */
    private double dovolena;

}
