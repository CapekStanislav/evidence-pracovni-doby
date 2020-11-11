package cz.stanislavcapek.evidencepd.smeny;

import lombok.*;

import java.time.LocalDateTime;

/**
 * An instance of class {@code Smena}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Smena {

    private LocalDateTime zacatek;
    private LocalDateTime konec;
    private PracovniDoba pracovniDoba;
    private Priplatky priplatky;
    private TypSmeny typSmeny;

}
