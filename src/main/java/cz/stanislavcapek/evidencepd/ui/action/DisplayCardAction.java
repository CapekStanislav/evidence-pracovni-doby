package cz.stanislavcapek.evidencepd.ui.action;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class DisplayCardAction extends AbstractAction {

    private final JPanel parent;

    public DisplayCardAction(String name, String desc, int mnemonic, String command, JPanel parent) {
        super(name);
        this.parent = parent;
        putValue(Action.SHORT_DESCRIPTION, desc);
        putValue(Action.MNEMONIC_KEY, mnemonic);
        putValue(Action.ACTION_COMMAND_KEY, command);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ((CardLayout) parent.getLayout()).show(parent, e.getActionCommand());
    }
}
