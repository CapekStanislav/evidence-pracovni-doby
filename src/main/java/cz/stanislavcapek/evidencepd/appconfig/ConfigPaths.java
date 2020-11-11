package cz.stanislavcapek.evidencepd.appconfig;

import lombok.Value;

import java.nio.file.Path;

/**
 * An instance of class {@code ConfigPaths}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public class ConfigPaths {

    public static final Path DAT = Path.of("dat");
    public static final Path SVATKY = Path.of("dat/svatky");
    public static final Path EVIDENCE = Path.of("dat/evidence");
    public static final Path ZAMESTNANCI = Path.of("dat/zamestnanci");

    private ConfigPaths() {
    }

    public static Path getDAT() {
        return DAT;
    }

    public static Path getSVATKY() {
        return SVATKY;
    }

    public static Path getEVIDENCE() {
        return EVIDENCE;
    }

    public static Path getZAMESTNANCI() {
        return ZAMESTNANCI;
    }
}
