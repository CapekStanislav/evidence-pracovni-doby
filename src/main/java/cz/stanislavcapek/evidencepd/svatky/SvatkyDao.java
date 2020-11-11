package cz.stanislavcapek.evidencepd.svatky;

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
 * Instance třídy {@code SvatkyDao}
 *
 * @author Stanislav Čapek
 */
public final class SvatkyDao implements Dao<List<Svatek>> {

    private static final Map<Integer, List<Svatek>> SVATKY_MAP = new TreeMap<>();
    private static final String NAME_OF_FILE = "svatky";
    private static final String EXTENSION_OF_FILE = ".json";

    public SvatkyDao() {
    }

    @Override
    public void uloz(Path cesta, List<Svatek> objekt) throws IOException {

        final ObjectMapper mapper = new ObjectMapper();
        zkontrolujExistenciSlozkyAVytvor(cesta);
        mapper.writeValue(Files.newBufferedWriter(cesta), objekt);
    }

    private void zkontrolujExistenciSlozkyAVytvor(Path cesta) throws IOException {
        Files.createDirectories(cesta.getParent());
    }

    @Override
    public List<Svatek> nacti(Path cesta) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final CollectionType type = mapper.getTypeFactory().constructCollectionType(List.class, Svatek.class);
        return mapper.readValue(Files.newBufferedReader(cesta), type);
    }

    public Optional<List<Svatek>> getPodleRoku(int rok) {
        if (!SVATKY_MAP.containsKey(rok)) {
            final Path file = Path.of(
                    ConfigPaths.SVATKY.toString(), NAME_OF_FILE + String.valueOf(rok) + EXTENSION_OF_FILE
            );

            // načtení svátků ze souboru
            try {
                final List<Svatek> list = nacti(file);
                SVATKY_MAP.put(rok, list);
                return Optional.of(list);
            } catch (IOException e) {

                // při neúspěchu -> načtení z URL
                try {
                    final List<Svatek> svatkyFromUrl = getSvatkyFromUrl(rok);
                    SVATKY_MAP.put(rok, svatkyFromUrl);
                    uloz(file, svatkyFromUrl);
                    return Optional.of(svatkyFromUrl);
                } catch (IOException ioException) {
                    return Optional.empty();
                }
            }
        }
        return Optional.ofNullable(SVATKY_MAP.get(rok));
    }

    /**
     * Načte svátky z url
     *
     * @param rok rok pro který zjišťujeme svátky
     */
    private List<Svatek> getSvatkyFromUrl(int rok) throws IOException {
        final Document document;

        final String url = String.format("http://svatky.centrum.cz/svatky/statni-svatky/%d/", rok);
        document = Jsoup.connect(url).get();
        final Elements elements = document.select("#list-names");
        final Elements tds = elements.select("td");

        List<Svatek> svatekList = new ArrayList<>();
        for (int i = 0; i < tds.size() - 1; i += 2) {
            String s = tds.get(i).text().replaceAll("\\.", "");
            final String[] split = s.split(" ");
            int den = Integer.parseInt(split[0]);
            int mesic = Integer.parseInt(split[1]);
            svatekList.add(
                    new Svatek(
                            LocalDate.of(rok, mesic, den),
                            tds.get(i + 1).text()
                    )
            );
        }
        return svatekList;
    }
}
