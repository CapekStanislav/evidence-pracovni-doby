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

    private static final List<Employee> EMPLOYEES = new ArrayList<>();
    private static final Path TEST_DIR = Paths.get("src/test/resources/zamestnanci");

    @BeforeAll
    static void beforeAll() {
        final Employee z1 = new Employee(1, "Petr", "Novák");
        final Employee z2 = new Employee(2, "Ondra", "Malý");
        final Employee z3 = new Employee(3, "Markéta", "Pospíšilová");
        final Employee z4 = new Employee(4, "Lenka", "Drahá");
        EMPLOYEES.add(z1);
        EMPLOYEES.add(z2);
        EMPLOYEES.add(z3);
        EMPLOYEES.add(z4);
    }

    @Test
    void ulozeniSeznamuZamestnancu() {
        final Dao<List<Employee>> dao = new EmployeesDao();
        final Path path = Paths.get(TEST_DIR.toString(), "ulozeniZamestnanci.json");

        try {
            dao.save(path, EMPLOYEES);
            assert true;
        } catch (IOException e) {
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    void nacteniSeznamuZamestnancu() {
        final Dao<List<Employee>> dao = new EmployeesDao();
        final Path path = Paths.get(TEST_DIR.toString(), "nacteniZamestnanci.json");

        try {
            final List<Employee> employeeList = dao.load(path);
            Assertions.assertEquals(4, employeeList.size());
            assert true;
        } catch (IOException e) {
            e.printStackTrace();
            assert false;
        }
    }
}