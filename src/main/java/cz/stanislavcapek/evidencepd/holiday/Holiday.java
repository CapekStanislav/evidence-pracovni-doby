package cz.stanislavcapek.evidencepd.holiday;

import java.time.LocalDate;

/**
 * Instance třídy {@code Holiday}
 *
 * @author Stanislav Čapek
 */
public record Holiday(LocalDate date, String name) {
}
