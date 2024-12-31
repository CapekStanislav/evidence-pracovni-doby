package cz.stanislavcapek.evidencepd.ui.controller;

import com.google.inject.Inject;
import cz.stanislavcapek.evidencepd.employee.Employee;
import cz.stanislavcapek.evidencepd.employee.EmployeeListModel;
import cz.stanislavcapek.evidencepd.ui.component.employee.EmployeeSizeChangedListeners;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class EmployeeController {

    private final EmployeeListModel model;
    private final List<EmployeeSizeChangedListeners> sizeChangedListeners = new ArrayList<>();

    @Inject
    public EmployeeController(EmployeeListModel model) {
        this.model = model;
    }

    public void addSizeChangedListener(EmployeeSizeChangedListeners listener) {
        sizeChangedListeners.add(listener);
    }

    public EmployeeListModel getModel() {
        return model;
    }

    public boolean addEmployee(Employee employee) {
        boolean added = model.addEmployee(employee);
        fireModelSizeChanged();
        return added;
    }

    public void removeEmployee(Employee employee) {
        model.removeEmployee(employee);
        fireModelSizeChanged();
    }

    public void updateEmployee(int id, @Nullable String firstName, @Nullable String lastName) {
        model.updateEmployee(id, firstName, lastName);
    }

    public int getEmployeeCount() {
        return model.getSize();
    }

    public Employee getEmployeeAt(int index) {
        return model.getElementAt(index);
    }

    public void loadModel(Path location) {
        model.load(location);
        fireModelSizeChanged();
    }

    public void saveModel() {
        model.save();
    }

    public void saveModel(Path location) {
        model.save(location);
    }

    public boolean isModelSaved() {
        return model.isSaved();
    }

    private void fireModelSizeChanged() {
        sizeChangedListeners.forEach(l -> l.sizeChanged(model.getSize()));
    }
}
