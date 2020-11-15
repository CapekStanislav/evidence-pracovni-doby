package cz.stanislavcapek.evidencepd.shift;

import lombok.Data;

/**
 * An instance of class {@code WorkingTime}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
@Data
public class WorkingTime {

    /**
     * Celková délka směny
     */
    private double length;
    /**
     * Odpracované hodiny ve smyslu fyzické přítomnosti na pracovišti.
     * Započitatelné do pracovní doby
     */
    private double workedOut;
    /**
     * Neodpracované hodiny ve smyslu hodin započitatelných do pracovní
     * doby, které nebyly fyzicky odpracované (např. neschopnost)
     */
    private double notWorkedOut;

    /**
     * Hodiny, které se považují za dovolenou
     */
    private double holiday;

}
