package cz.stanislavcapek.evidencepd.zamestnanec;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

/**
 * An instance of class {@code Zamestnanec}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Zamestnanec {

    @Setter(value = AccessLevel.NONE)
    private int id;
    private String jmeno;
    @EqualsAndHashCode.Exclude
    private String prijmeni;

    @JsonIgnore
    public String getCeleJmeno() {
        return String.format("%s %s", jmeno, prijmeni);
    }

    @JsonIgnore
    public String getZkratku() {
        return (jmeno.substring(0, 1) + prijmeni.substring(0, 2)).toUpperCase();
    }
}
