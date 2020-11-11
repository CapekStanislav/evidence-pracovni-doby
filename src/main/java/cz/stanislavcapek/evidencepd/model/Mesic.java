package cz.stanislavcapek.evidencepd.model;

import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;

/**
 * An instance of enum {@code Mesic}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public enum  Mesic {

    LEDEN("leden", 1),
    UNOR("unor", 2),
    BREZEN("březen", 3),
    DUBEN("duben", 4),
    KVETEN("květen", 5),
    CERVEN("červen", 6),
    CERVENEC("červenec", 7),
    SRPEN("srpen", 8),
    ZARI("září", 9),
    RIJEN("říjen", 10),
    LISTOPAD("listopad", 11),
    PROSINEC("prosinec", 12);

    private String nazev;
    private int cislo;

    Mesic(String nazev, int cislo) {
        this.nazev = nazev;
        this.cislo = cislo;
    }

    public String getNazev() {
        return nazev;
    }

    public int getCislo() {
        return cislo;
    }

    public static Mesic valueOf(int cislo){
        return Arrays.stream(values())
                .filter(mesic -> mesic.cislo==cislo)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public static int getPocetDniVMesici(Mesic mesic, int rok) {
        final LocalDate date = LocalDate.of(rok, mesic.cislo, 1);
        return date.getMonth().length(date.isLeapYear());
    }
}
