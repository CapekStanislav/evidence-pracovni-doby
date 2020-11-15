package cz.stanislavcapek.evidencepd.holiday;

import lombok.Value;

import java.time.LocalDate;

/**
 * Instance třídy {@code Holiday}
 *
 * @author Stanislav Čapek
 */
@Value
public class Holiday {
    public LocalDate date;
    public String name;
}
