package cz.stanislavcapek.evidencepd.appconfig;

import java.nio.file.Path;

/**
 * An instance of class {@code ConfigPaths}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public class ConfigPaths {

    public static final Path DATA_PATH = Path.of("data");
    public static final Path HOLIDAYS_PATH = DATA_PATH.resolve("svatky");
    public static final Path RECORDS_PATH = DATA_PATH.resolve("evidence");
    public static final Path EMPLOYEES_PATH = DATA_PATH.resolve("zamestnanci");
    public static final Path LOG_PATH = DATA_PATH.resolve("logs");

    private ConfigPaths() {
    }

    public static Path getDataPath() {
        return DATA_PATH;
    }

    public static Path getHolidaysPath() {
        return HOLIDAYS_PATH;
    }

    public static Path getRecordsPath() {
        return RECORDS_PATH;
    }

    public static Path getEmployeesPath() {
        return EMPLOYEES_PATH;
    }

    public static Path getLogPath() {
        return LOG_PATH;
    }
}
