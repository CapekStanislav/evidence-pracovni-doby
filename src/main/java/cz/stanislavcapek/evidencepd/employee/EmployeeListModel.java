/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.stanislavcapek.evidencepd.employee;

import javax.swing.AbstractListModel;
import java.util.*;

/**
 * Utility třída pro práci se seznamem zaměstnanců. Jedná se o návrhový vzor Singlton, takže jde vytvořit pouze jednu
 * instanci, která je pak na požádání poskytnuta přes metodu getInstance().
 *
 * @author Stanislav Čapek
 */
public class EmployeeListModel extends AbstractListModel {
    private static EmployeeListModel instance;
    private final List<Employee> employeeList;

    /**
     * Privátní konstruktor bez parametru. Návrhový model Singlton.
     */
    private EmployeeListModel() {
        this.employeeList = new ArrayList<>();
    }

    /**
     * Metoda pro získání instance EmployeeListModel.
     *
     * @return EmployeeListModel
     */
    public static EmployeeListModel getInstance() {
        if (instance == null) {
            instance = new EmployeeListModel();
        }
        return instance;
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
     * @return {@code true} - došlo k odebrání
     */
    public boolean removeEmployee(Employee employee) {
        int index = employeeList.indexOf(employee);
        boolean result = employeeList.remove(employee);
        sortById();
        fireIntervalRemoved(this, index, index);
        return result;
    }

    /**
     * Metoda vymaže SEZNAM zaměstnanců. Pozor, jedná se o jedinou instanci, dojde tedy k vymazání všech odkazů
     * na jednotlivé zaměstnance.
     *
     * @return {@code true} - došlo k vymazaní
     */
    public boolean clearList() {
        int index1 = employeeList.size() - 1;
        boolean result = employeeList.removeAll(employeeList);
        if (index1 >= 0) {
            fireIntervalRemoved(this, 0, index1);
        }
        return result;
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
                .findFirst().orElse(null);
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
     * Upozorní {@link javax.swing.event.ListDataListener} na změnu v seznamu.
     */
    public void fireModelChanged() {
        super.fireContentsChanged(this, 0, 0);
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
     * Vrátí kopii aktuálního stavu seznamu zaměstnanců jako pole {@link Employee}.
     *
     * @return kopie aktuálního stavu seznamu
     */
    public Employee[] toArray() {
        return employeeList.toArray(new Employee[getSize()]);
    }

    /**
     * Vrátí kopii aktuální stavu seznamu zaměstnanců jako seznam {@link Employee}
     *
     * @return
     */
    public List<Employee> getEmployeeList() {
        return new ArrayList<>(employeeList);
    }

    /**
     * Metoda, která seřadí zaměstnance podle jejich služebního čísla (ID) od nejmenšího po největší.
     */
    private void sortById() {
        employeeList.sort(Comparator.comparing(Employee::getId));
    }
}
