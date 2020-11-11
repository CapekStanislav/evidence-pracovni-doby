package cz.stanislavcapek.evidencepd.model;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * An instance of class {@code FondPracovniDoby}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public class FondPracovniDoby {

    public enum DruhTydenniPracDoby {
        STANDARDNI(40),
        VICESMENNY_NEPRETRZITY(37.5),
        ZTIZENE_PODMINKY(37.5),
        DVOUSMENNY(38.75);

        private double fond;

        DruhTydenniPracDoby(double fond) {
            this.fond = fond;
        }
    }

    /**
     * Metoda, která vypočte fond pracovní doby na daný měsíc pro 7.5 hodinové směny (37.5h / týden).
     *
     * @param datum obdobi (rok a měsíc)
     * @return pracovní fond na měsíc
     */
    public static double vypoctiFondPracovniDoby(LocalDate datum) {
        return vypoctiFondPracovniDoby(datum, DruhTydenniPracDoby.VICESMENNY_NEPRETRZITY);
    }

    /**
     * Metoda, která vypočte fond pracovní doby na daný měsíc dle zadaného druhu.
     *
     * @param datum období (rok a měsíc)
     * @param druh  týdenní fond týdenní pracovní doby
     * @return pracovní fond na měsíc
     */
    public static double vypoctiFondPracovniDoby(LocalDate datum, DruhTydenniPracDoby druh) {

        double fond;
        int vikendy = 0;
        int pocetDniVMesici = datum.lengthOfMonth();
        LocalDate tempDatum = datum;
        final double delkaSmeny = druh.fond / 5;

        for (int i = 0; i < pocetDniVMesici; i++) {
            if (tempDatum.getDayOfWeek() == DayOfWeek.SATURDAY || tempDatum.getDayOfWeek() == DayOfWeek.SUNDAY) {
                vikendy++;
            }
            tempDatum = tempDatum.plusDays(1);
        }
        fond = (pocetDniVMesici - vikendy) * delkaSmeny;

        return fond;
    }
}
