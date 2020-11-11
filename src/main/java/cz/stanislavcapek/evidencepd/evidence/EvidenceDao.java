package cz.stanislavcapek.evidencepd.evidence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import cz.stanislavcapek.evidencepd.dao.Dao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * An instance of class {@code EvidenceDao}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public class EvidenceDao implements Dao<List<Evidence>> {

    @Override
    public void uloz(Path cesta, List<Evidence> objekt) throws IOException {
        final ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
        Files.createDirectories(cesta.getParent());
        mapper.writeValue(Files.newBufferedWriter(cesta), objekt);
    }

    @Override
    public List<Evidence> nacti(Path cesta) throws IOException {
        final ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());

        final CollectionLikeType collectionLikeType = mapper.getTypeFactory()
                .constructCollectionLikeType(List.class, ZakladniEvidence.class);

        final List<Evidence> evidenceList = mapper
                .readValue(Files.newBufferedReader(cesta), collectionLikeType);
        return evidenceList;
    }
}
