package cz.stanislavcapek.evidencepd.holiday;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Instance třídy {@code Holidays}
 *
 * @author Stanislav Čapek
 */
@EqualsAndHashCode
@ToString
public class Holidays {

    private static final HolidaysDao HOLIDAYS_DAO = new HolidaysDao();

    private final List<Holiday> holidayList;
    private final int year;

    private Holidays(List<Holiday> holidayList, int year) {
        this.holidayList = holidayList;
        this.year = year;
    }

    /**
     * Vrátí instance svátků v zadaném roce.
     *
     * @param year kalendářní rok
     * @return instance svátků
     */
    public static Holidays getInstance(int year) {
        final Optional<List<Holiday>> optionalHolidayList = HOLIDAYS_DAO.getByYear(year);
        return optionalHolidayList
                .map(svatekList -> new Holidays(svatekList, year))
                .orElseGet(() -> new Holidays(Collections.emptyList(), year));
    }

    public int getYear() {
        return year;
    }

    public List<LocalDate> getDatesOfHolidays() {
        return holidayList.stream()
                .map(Holiday::getDate)
                .collect(Collectors.toList());
    }

}
