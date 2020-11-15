package cz.stanislavcapek.evidencepd.employee;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import cz.stanislavcapek.evidencepd.dao.Dao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * An instance of class {@code EmployeesDao}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public class EmployeesDao implements Dao<List<Employee>> {
    @Override
    public void save(Path path, List<Employee> object) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        Files.createDirectories(path.getParent());
        mapper.writeValue(Files.newBufferedWriter(path), object);

    }

    @Override
    public List<Employee> load(Path path) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final CollectionType collectionType = mapper
                .getTypeFactory()
                .constructCollectionType(List.class, Employee.class);
        return mapper.readValue(Files.newBufferedReader(path),collectionType);
    }
}
