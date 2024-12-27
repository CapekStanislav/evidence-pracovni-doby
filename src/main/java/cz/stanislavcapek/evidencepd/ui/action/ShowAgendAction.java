package cz.stanislavcapek.evidencepd.ui.action;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ShowAgendAction extends AbstractAction {

    private final JPanel content;

    public ShowAgendAction(String name, String desc, int mnemonic, String command, JPanel content) {
        super(name);
        this.content = content;
        putValue(Action.SHORT_DESCRIPTION, desc);
        putValue(Action.MNEMONIC_KEY, mnemonic);
        putValue(Action.ACTION_COMMAND_KEY, command);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ((CardLayout) content.getLayout()).show(content, e.getActionCommand());
    }
}
