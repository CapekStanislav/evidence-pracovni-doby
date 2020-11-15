package cz.stanislavcapek.evidencepd.model;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * An instance of class {@code WorkingTimeFund}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public class WorkingTimeFund {

    public enum TypeOfWeeklyWorkingTime {
        STANDARD(40),
        MULTISHIFT_CONTINUOUS(37.5),
        WORSENED_CONDITIONS(37.5),
        TWOSHIFT(38.75);

        private double fund;

        TypeOfWeeklyWorkingTime(double fund) {
            this.fund = fund;
        }
    }

    /**
     * Metoda, která vypočte fond pracovní doby na daný měsíc pro 7.5 hodinové směny (37.5h / týden).
     *
     * @param date obdobi (rok a měsíc)
     * @return pracovní fond na měsíc
     */
    public static double calculateWorkingTimeFund(LocalDate date) {
        return calculateWorkingTimeFund(date, TypeOfWeeklyWorkingTime.MULTISHIFT_CONTINUOUS);
    }

    /**
     * Metoda, která vypočte fond pracovní doby na daný měsíc dle zadaného druhu.
     *
     * @param date období (rok a měsíc)
     * @param type týdenní fond týdenní pracovní doby
     * @return pracovní fond na měsíc
     */
    public static double calculateWorkingTimeFund(LocalDate date, TypeOfWeeklyWorkingTime type) {

        double fund;
        int weekend = 0;
        int numOfDaysInMonth = date.lengthOfMonth();
        LocalDate tempDate = date;
        final double shiftLength = type.fund / 5;

        for (int i = 0; i < numOfDaysInMonth; i++) {
            if (tempDate.getDayOfWeek() == DayOfWeek.SATURDAY || tempDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                weekend++;
            }
            tempDate = tempDate.plusDays(1);
        }
        fund = (numOfDaysInMonth - weekend) * shiftLength;

        return fund;
    }
}
