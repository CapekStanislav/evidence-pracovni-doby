/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.stanislavcapek.evidencepd.swingui.model;

import cz.stanislavcapek.evidencepd.domain.employee.Employee;
import cz.stanislavcapek.evidencepd.service.employee.EmployeeService;

import javax.annotation.Nullable;
import javax.swing.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Utility třída pro práci se seznamem zaměstnanců. Jedná se o návrhový vzor Singlton, takže jde vytvořit pouze jednu
 * instanci, která je pak na požádání poskytnuta přes metodu getInstance().
 *
 * @author Stanislav Čapek
 */
public class EmployeeListModel extends AbstractListModel<Employee> {
    private final List<Employee> employeeList;
    private final EmployeeService service;

    EmployeeListModel(EmployeeService service) {
        this.service = service;
        this.employeeList = new ArrayList<>();
    }

    /**
     * Metoda pro přidání zaměstnance (strážníka) do seznamu. V případě pokusu o přidání strážníka se stejným služebním číslem (ID)
     * metoda vrací {@code false}.
     *
     * @param employee Employee
     * @return boolean <br>
     * {@code true} - přidání proběhlo <br>
     * {@code false} - přidání neproběhlo (již existuje strážník se stejným ID)
     * @see Employee
     */
    public boolean addEmployee(Employee employee) {
        int index = employeeList.size();
        boolean result = false;
        if (searchById(employee.getId()) == null) {
            result = employeeList.add(employee);
            sortById();
            fireIntervalAdded(this, index, index);
        }
        return result;
    }

    /**
     * Metoda pro odebrání zaměstnance (strážníka) ze seznamu.
     *
     * @param employee Employee
     */
    public void removeEmployee(Employee employee) {
        int index = employeeList.indexOf(employee);
        employeeList.remove(employee);
        sortById();
        fireIntervalRemoved(this, index, index);
    }

    public void updateEmployee(int id, @Nullable String firstName, @Nullable String lastName) {
        Employee employee = searchById(id);
        int index = employeeList.indexOf(employee);
        if (employee == null) {
            return;
        }

        if (firstName != null) {
            employee.setFirstName(firstName);
        }

        if (lastName != null) {
            employee.setLastName(lastName);
        }

        fireContentsChanged(this, index, index);
    }

    /**
     * Metoda vymaže SEZNAM zaměstnanců. Pozor, jedná se o jedinou instanci, dojde tedy k vymazání všech odkazů
     * na jednotlivé zaměstnance.
     *
     * @return {@code true} - došlo k vymazaní
     */
    public void clearList() {
        int index1 = employeeList.size() - 1;
        employeeList.clear();
        if (index1 >= 0) {
            fireIntervalRemoved(this, 0, index1);
        }
    }

    /**
     * Vyhledání zaměstnance (strážníka) v seznamu podle jeho ID (služební číslo).
     *
     * @param id identifikátor zaměstnance
     * @return {@link Employee} - jestliže je nalezen<br>
     * {@code null} - jestliže není nalezen žádný strážník
     */
    public Employee searchById(int id) {
        return employeeList.stream()
                .filter(employee -> employee.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Zjistí jestli se v seznamu nachází zaměstnanec s odpovídajícím ID.
     *
     * @param id identifikátor zaměstnance
     * @return {@code true}  pokud je zaměstnanec nalezen, jinak {@code false}
     */
    public boolean containsEmployee(int id) {
        return employeeList.stream()
                .anyMatch(employee -> employee.getId() == id);
    }

    /**
     * Returns the length of the list.
     *
     * @return the length of the list
     */
    @Override
    public int getSize() {
        return employeeList.size();
    }

    /**
     * Returns the value at the specified index.
     *
     * @param index the requested index
     * @return the value at <code>index</code>
     */
    @Override
    public Employee getElementAt(int index) {
        return employeeList.get(index);
    }

    /**
     * Vrátí kopii aktuální stavu seznamu zaměstnanců jako seznam {@link Employee}
     *
     * @return
     */
    public List<Employee> getEmployeeList() {
        return List.copyOf(employeeList);
    }

    /**
     * Metoda, která seřadí zaměstnance podle jejich služebního čísla (ID) od nejmenšího po největší.
     */
    private void sortById() {
        employeeList.sort(Comparator.comparing(Employee::getId));
    }

    public boolean isSaved() {
        return service.isSaved(employeeList);
    }

    public List<Employee> load(Path location) {
        return service.load(location);

    }

    public List<Employee> load() {
        return service.load();
    }

    public void initEmployeeListModel(List<Employee> list) {
        employeeList.clear();
        employeeList.addAll(list);
    }

    public void save(Path location) {
        service.save(location, List.copyOf(employeeList));
    }

    public void save() {
        service.save(List.copyOf(employeeList));
    }
}
