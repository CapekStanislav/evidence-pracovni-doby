package cz.stanislavcapek.evidencepd.employee;

import cz.stanislavcapek.evidencepd.dao.Dao;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


class EmployeesDaoTest {

    private static final List<Employee> zamestnanci = new ArrayList<>();
    private final Path testDir = Paths.get("src/test/resources/zamestnanci");

    @BeforeAll
    static void beforeAll() {
        final Employee z1 = new Employee(1, "Petr", "Novák");
        final Employee z2 = new Employee(2, "Ondra", "Malý");
        final Employee z3 = new Employee(3, "Markéta", "Pospíšilová");
        final Employee z4 = new Employee(4, "Lenka", "Drahá");
        zamestnanci.add(z1);
        zamestnanci.add(z2);
        zamestnanci.add(z3);
        zamestnanci.add(z4);
    }

    @Test
    void ulozeniSeznamuZamestnancu() {
        final Dao<List<Employee>> dao = new EmployeesDao();
        final Path cesta = Paths.get(testDir.toString(), "ulozeniZamestnanci.json");

        try {
            dao.save(cesta, zamestnanci);
            assert true;
        } catch (IOException e) {
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    void nacteniSeznamuZamestnancu() {
        final Dao<List<Employee>> dao = new EmployeesDao();
        final Path cesta = Paths.get(testDir.toString(), "nacteniZamestnanci.json");

        try {
            final List<Employee> employeeList = dao.load(cesta);
            Assertions.assertEquals(4, employeeList.size());
            assert true;
        } catch (IOException e) {
            e.printStackTrace();
            assert false;
        }
    }
}