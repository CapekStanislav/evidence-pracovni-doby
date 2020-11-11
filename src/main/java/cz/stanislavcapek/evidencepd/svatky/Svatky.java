package cz.stanislavcapek.evidencepd.svatky;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Instance třídy {@code Svatky}
 *
 * @author Stanislav Čapek
 */
@EqualsAndHashCode
@ToString
public class Svatky {
    // TODO: 01.02.2020 doddělat komentáře

    private static final SvatkyDao SVATKY_DAO = new SvatkyDao();


    //    private final Map<Integer, String> svatkyMap;
    private final List<Svatek> listSvatek;
    private final int rok;

    private Svatky(List<Svatek> listSvatek, int rok) {
        this.listSvatek = listSvatek;
        this.rok = rok;
    }

    /**
     * Vrátí instance svátků v zadaném roce.
     *
     * @param year kalendářní rok
     * @return instance svátků
     */
    public static Svatky getInstance(int year) {
        final Optional<List<Svatek>> optionalSvatekList = SVATKY_DAO.getPodleRoku(year);
        return optionalSvatekList
                .map(svatekList -> new Svatky(svatekList, year))
                .orElseGet(() -> new Svatky(Collections.emptyList(), year)
                );
    }

    public int getRok() {
        return rok;
    }

    public List<LocalDate> getDatumySvatku() {
        return listSvatek.stream()
                .map(Svatek::getDatum)
                .collect(Collectors.toList());
    }

}
