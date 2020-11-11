package cz.stanislavcapek.evidencepd.smeny;

import lombok.Data;
import lombok.Value;

/**
 * An instance of class {@code Priplatky}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
@Data
public class Priplatky {
    private double nocni;
    private double vikend;
    private double svatek;
    private double prescas;

}
