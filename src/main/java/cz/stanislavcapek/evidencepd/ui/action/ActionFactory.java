package cz.stanislavcapek.evidencepd.ui.action;

import com.google.inject.Inject;
import cz.stanislavcapek.evidencepd.employee.EmployeeListModel;
import cz.stanislavcapek.evidencepd.employee.EmployeeService;
import cz.stanislavcapek.evidencepd.ui.MainWindow;

import javax.swing.*;

public class ActionFactory {

    private final EmployeeService employeeService;
    private final EmployeeListModel employeeListModel;

    @Inject
    public ActionFactory(EmployeeService employeeService, EmployeeListModel employeeListModel) {
        this.employeeService = employeeService;
        this.employeeListModel = employeeListModel;
    }

    public SaveEmployeeListAction createSaveEmployees(MainWindow window, String name, String desc, int mnemonic) {
        return new SaveEmployeeListAction(window, name, desc, mnemonic, employeeService, employeeListModel);
    }

    public DisplayCardAction createDisplayCard(String name, String desc, int mnemonic, String command, JPanel content) {
        return new DisplayCardAction(name, desc, mnemonic, command, content);
    }

    public CloseAppAction createCloseApp(String name, String desc, int mnemonic) {
        return new CloseAppAction(name, desc, mnemonic);
    }
}
