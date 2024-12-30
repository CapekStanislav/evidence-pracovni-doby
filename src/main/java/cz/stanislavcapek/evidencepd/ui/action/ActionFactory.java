package cz.stanislavcapek.evidencepd.ui.action;

import com.google.inject.Inject;
import cz.stanislavcapek.evidencepd.employee.EmployeeListModel;

import javax.swing.*;

public class ActionFactory {

    private final EmployeeListModel employeeListModel;

    @Inject
    public ActionFactory(EmployeeListModel employeeListModel) {
        this.employeeListModel = employeeListModel;
    }

    public SaveEmployeeListAction createSaveEmployees(String name, String desc, int mnemonic) {
        return new SaveEmployeeListAction(name, desc, mnemonic, employeeListModel);
    }

    public DisplayCardAction createDisplayCard(String name, String desc, int mnemonic, String command, JPanel content) {
        return new DisplayCardAction(name, desc, mnemonic, command, content);
    }

    public CloseAppAction createCloseApp(String name, String desc, int mnemonic) {
        return new CloseAppAction(name, desc, mnemonic);
    }
}
