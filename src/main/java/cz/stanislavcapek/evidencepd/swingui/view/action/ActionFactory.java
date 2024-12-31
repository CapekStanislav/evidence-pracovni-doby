package cz.stanislavcapek.evidencepd.swingui.view.action;

import com.google.inject.Inject;
import cz.stanislavcapek.evidencepd.swingui.controller.EmployeeController;

import javax.swing.*;

public class ActionFactory {

    private final EmployeeController employeeController;

    @Inject
    public ActionFactory(EmployeeController employeeController) {
        this.employeeController = employeeController;
    }

    public SaveEmployeeListAction createSaveEmployees(String name, String desc, int mnemonic) {
        return new SaveEmployeeListAction(name, desc, mnemonic, employeeController);
    }

    public DisplayCardAction createDisplayCard(String name, String desc, int mnemonic, String command, JPanel content) {
        return new DisplayCardAction(name, desc, mnemonic, command, content);
    }

    public CloseAppAction createCloseApp(String name, String desc, int mnemonic) {
        return new CloseAppAction(name, desc, mnemonic, employeeController);
    }
}
