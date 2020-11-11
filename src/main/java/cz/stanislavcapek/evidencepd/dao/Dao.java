package cz.stanislavcapek.evidencepd.dao;

import java.io.IOException;
import java.nio.file.Path;

/**
 * An instance of interface {@code Dao}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public interface Dao <T>{

    void uloz(Path cesta,T objekt) throws IOException;

    T nacti(Path cesta) throws IOException;

}
