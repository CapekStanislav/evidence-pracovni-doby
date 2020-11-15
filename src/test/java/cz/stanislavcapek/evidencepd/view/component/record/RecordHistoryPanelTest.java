package cz.stanislavcapek.evidencepd.view.component.record;

import cz.stanislavcapek.evidencepd.appconfig.ConfigPaths;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Log4j2
class RecordHistoryPanelTest {

    @Test
    void prohledaniSlozkyAVypsaniJmenaSouboru() throws IOException {
        Files.walk(ConfigPaths.RECORDS_PATH, 1)
                .filter(path -> path.getFileName().toString().matches("^evidence.*\\.json$"))
                .map(Path::getFileName)
                .map(path -> path.toString().split("\\.")[0])
                .forEach(System.out::println);
    }

    @Test
    void zobrazeniAZpracovniVysledku() {
        new RecordHistoryPanel().showListDialog().isChosen(System.out::println);
    }

    @Test
    void zobrazeniAZpracovaniPriZruseniVolby() {
        final String neboJinyNazev = new RecordHistoryPanel()
                .showListDialog()
                .orElseGet(() -> "nebylo nic vybrano");
        System.out.println("neboJinyNazev = " + neboJinyNazev);
    }

    @Test
    void zobrazeniAPremapovani() {
        final Path path = new RecordHistoryPanel()
                .showListDialog()
                .map(s -> Paths.get(s + ".json"));
        log.info(path.toString());

    }
}