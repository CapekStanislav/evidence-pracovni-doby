package cz.stanislavcapek.evidencepd.workattendance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import cz.stanislavcapek.evidencepd.dao.Dao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * An instance of class {@code WorkAttendanceDao}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public class WorkAttendanceDao implements Dao<List<WorkAttendance>> {

    @Override
    public void save(Path path, List<WorkAttendance> object) throws IOException {
        final ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
        Files.createDirectories(path.getParent());
        mapper.writeValue(Files.newBufferedWriter(path), object);
    }

    @Override
    public List<WorkAttendance> load(Path path) throws IOException {
        final ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());

        final CollectionLikeType collectionLikeType = mapper.getTypeFactory()
                .constructCollectionLikeType(List.class, DefaultWorkAttendance.class);

        return mapper.readValue(Files.newBufferedReader(path), collectionLikeType);
    }
}
