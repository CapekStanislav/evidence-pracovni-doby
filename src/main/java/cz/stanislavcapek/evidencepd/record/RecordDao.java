package cz.stanislavcapek.evidencepd.record;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import cz.stanislavcapek.evidencepd.dao.Dao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * An instance of class {@code RecordDao}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public class RecordDao implements Dao<List<Record>> {

    @Override
    public void save(Path path, List<Record> object) throws IOException {
        final ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
        Files.createDirectories(path.getParent());
        mapper.writeValue(Files.newBufferedWriter(path), object);
    }

    @Override
    public List<Record> load(Path path) throws IOException {
        final ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());

        final CollectionLikeType collectionLikeType = mapper.getTypeFactory()
                .constructCollectionLikeType(List.class, DefaultRecord.class);

        return mapper.readValue(Files.newBufferedReader(path), collectionLikeType);
    }
}
