package cz.stanislavcapek.evidencepd.zamestnanec;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import cz.stanislavcapek.evidencepd.dao.Dao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * An instance of class {@code ZamestnanciDao}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public class ZamestnanciDao implements Dao<List<Zamestnanec>> {
    @Override
    public void uloz(Path cesta, List<Zamestnanec> objekt) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        Files.createDirectories(cesta.getParent());
        mapper.writeValue(Files.newBufferedWriter(cesta), objekt);

    }

    @Override
    public List<Zamestnanec> nacti(Path cesta) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final CollectionType collectionType = mapper
                .getTypeFactory()
                .constructCollectionType(List.class, Zamestnanec.class);
        return mapper.readValue(Files.newBufferedReader(cesta),collectionType);
    }
}
