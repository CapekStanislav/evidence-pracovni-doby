package cz.stanislavcapek.evidencepd.employee;

import com.google.inject.Inject;
import cz.stanislavcapek.evidencepd.appconfig.ConfigPaths;
import cz.stanislavcapek.evidencepd.employee.exception.LoadEmployeesFromFileException;
import cz.stanislavcapek.evidencepd.employee.exception.SaveEmployeesFromFileException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class EmployeeService {

    private static final Logger log = LogManager.getLogger(EmployeeService.class);
    private final Path employeeListFile = Paths.get("seznamZamestnancu.json");
    private final Path employeeListFilePath = ConfigPaths.EMPLOYEES_PATH.resolve(employeeListFile);

    private final EmployeesDao employeesDao;

    @Inject
    public EmployeeService(EmployeesDao employeesDao) {
        this.employeesDao = employeesDao;
    }

    public List<Employee> load() {
        return load(employeeListFilePath);
    }

    public List<Employee> load(Path specificFile) {
        try {
            return employeesDao.load(specificFile);
        } catch (IOException e) {
            throw new LoadEmployeesFromFileException(specificFile, e);
        }
    }

    public void save(List<Employee> employees) {
        save(employeeListFilePath, employees);
    }

    public void save(Path specificFile, List<Employee> employees) {
        try {
            employeesDao.save(specificFile, employees);
        } catch (IOException e) {
            throw new SaveEmployeesFromFileException(specificFile, e);
        }
    }

}
