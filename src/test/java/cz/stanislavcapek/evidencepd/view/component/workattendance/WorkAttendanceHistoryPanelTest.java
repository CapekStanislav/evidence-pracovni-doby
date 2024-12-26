package cz.stanislavcapek.evidencepd.view.component.workattendance;

import cz.stanislavcapek.evidencepd.appconfig.ConfigPaths;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class WorkAttendanceHistoryPanelTest {

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
        new WorkAttendanceHistoryPanel().showListDialog().ifChosen(System.out::println);
    }

    @Test
    void zobrazeniAZpracovaniPriZruseniVolby() {
        final String neboJinyNazev = new WorkAttendanceHistoryPanel()
                .showListDialog()
                .orElseGet(() -> "nebylo nic vybrano");
    }

    @Test
    void zobrazeniAPremapovani() {
        final Path path = new WorkAttendanceHistoryPanel()
                .showListDialog()
                .map(s -> Paths.get(s + ".json"));
    }
}