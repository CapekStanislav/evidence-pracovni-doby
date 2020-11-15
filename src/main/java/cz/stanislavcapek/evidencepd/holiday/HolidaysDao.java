package cz.stanislavcapek.evidencepd.holiday;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import cz.stanislavcapek.evidencepd.appconfig.ConfigPaths;
import cz.stanislavcapek.evidencepd.dao.Dao;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;

/**
 * Instance třídy {@code HolidaysDao}
 *
 * @author Stanislav Čapek
 */
public final class HolidaysDao implements Dao<List<Holiday>> {

    private static final Map<Integer, List<Holiday>> HOLIDAY_MAP = new TreeMap<>();
    private static final String NAME_OF_FILE = "svatky";
    private static final String EXTENSION_OF_FILE = ".json";

    public HolidaysDao() {
    }

    @Override
    public void save(Path path, List<Holiday> object) throws IOException {

        final ObjectMapper mapper = new ObjectMapper();
        createNonExistingDirectories(path);
        mapper.writeValue(Files.newBufferedWriter(path), object);
    }

    private void createNonExistingDirectories(Path cesta) throws IOException {
        Files.createDirectories(cesta.getParent());
    }

    @Override
    public List<Holiday> load(Path path) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final CollectionType type = mapper.getTypeFactory().constructCollectionType(List.class, Holiday.class);
        return mapper.readValue(Files.newBufferedReader(path), type);
    }

    public Optional<List<Holiday>> getByYear(int year) {
        if (!HOLIDAY_MAP.containsKey(year)) {
            final Path file = Path.of(
                    ConfigPaths.HOLIDAYS_PATH.toString(), NAME_OF_FILE + year + EXTENSION_OF_FILE
            );

            // loading from file
            try {
                final List<Holiday> list = load(file);
                HOLIDAY_MAP.put(year, list);
                return Optional.of(list);
            } catch (IOException e) {

                // in case of failure - get from URL
                try {
                    final List<Holiday> holidaysFromUrl = getHolidaysFromUrl(year);
                    HOLIDAY_MAP.put(year, holidaysFromUrl);
                    save(file, holidaysFromUrl);
                    return Optional.of(holidaysFromUrl);
                } catch (IOException ioException) {
                    return Optional.empty();
                }
            }
        }
        return Optional.ofNullable(HOLIDAY_MAP.get(year));
    }

    /**
     * Načte svátky z url
     *
     * @param year rok pro který zjišťujeme svátky
     */
    private List<Holiday> getHolidaysFromUrl(int year) throws IOException {
        final Document document;

        final String url = String.format("http://svatky.centrum.cz/svatky/statni-svatky/%d/", year);
        document = Jsoup.connect(url).get();
        final Elements elements = document.select("#list-names");
        final Elements tds = elements.select("td");

        List<Holiday> holidayList = new ArrayList<>();
        for (int i = 0; i < tds.size() - 1; i += 2) {
            String s = tds.get(i).text().replaceAll("\\.", "");
            final String[] split = s.split(" ");
            int den = Integer.parseInt(split[0]);
            int mesic = Integer.parseInt(split[1]);
            holidayList.add(
                    new Holiday(
                            LocalDate.of(year, mesic, den),
                            tds.get(i + 1).text()
                    )
            );
        }
        return holidayList;
    }
}
