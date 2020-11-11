package cz.stanislavcapek.evidencepd.view.component.evidence;

import cz.stanislavcapek.evidencepd.appconfig.ConfigPaths;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Log4j2
class HistorieEvidenciPanelTest {

    @Test
    void prohledaniSlozkyAVypsaniJmenaSouboru() throws IOException {
        Files.walk(ConfigPaths.EVIDENCE, 1)
                .filter(path -> path.getFileName().toString().matches("^evidence.*\\.json$"))
                .map(Path::getFileName)
                .map(path -> path.toString().split("\\.")[0])
                .forEach(System.out::println);
    }

    @Test
    void zobrazeniAZpracovniVysledku() {
        new HistorieEvidenciPanel().zobrazNabidku().jeVybrano(System.out::println);
    }

    @Test
    void zobrazeniAZpracovaniPriZruseniVolby() {
        final String neboJinyNazev = new HistorieEvidenciPanel()
                .zobrazNabidku()
                .neboJinyNazev(() -> "nebylo nic vybrano");
        System.out.println("neboJinyNazev = " + neboJinyNazev);
    }

    @Test
    void zobrazeniAPremapovani() {
        final Path path = new HistorieEvidenciPanel()
                .zobrazNabidku()
                .map(s -> Paths.get(s + ".json"));
        log.info(path.toString());

    }
}