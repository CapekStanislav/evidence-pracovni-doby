package cz.stanislavcapek.evidencepd.employee;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeListModelTest {

    private static final Employee employee1 = new Employee(1, "First", "Employee");
    private static final Employee employee2 = new Employee(2, "Second", "Employee2");
    private static EmployeeListModel instance;

    @BeforeAll
    static void beforeAll() {
        instance = EmployeeListModel.getInstance();
        fillEmployeeListModel(instance);
    }

    @Test
    void whenEmployeeFoundByIdThenReturnEmployee() {
        final Employee foundEmployee = instance.searchById(1);
        assertEquals(employee1, foundEmployee);

    }

    @Test
    void whenEmployeeNotFoundByIdThenReturnNull() {
        final Employee nullEmployee = instance.searchById(3);
        assertNull(nullEmployee);

    }

    @Test
    void whenContainsEmployeeThenTrue() {
        final boolean result = instance.containsEmployee(1);
        assertTrue(result);

    }

    @Test
    void whenNotContainsEmployeeThenFalse() {
        final boolean result = instance.containsEmployee(3);
        assertFalse(result);
    }

    private static void fillEmployeeListModel(EmployeeListModel model) {
        model.addEmployee(employee1);
        model.addEmployee(employee2);
    }
}