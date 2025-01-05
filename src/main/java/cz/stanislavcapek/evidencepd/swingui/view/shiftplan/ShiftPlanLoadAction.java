package cz.stanislavcapek.evidencepd.swingui.view.shiftplan;

import cz.stanislavcapek.evidencepd.swingui.controller.ShiftPlanController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class ShiftPlanLoadAction extends AbstractAction {

    private final ShiftPlanController controller;
    // TODO: 01.03.2020 add icons

    public ShiftPlanLoadAction(String name, ShiftPlanController controller) {
        super(name);
        this.controller = controller;
        putValue(
                Action.SHORT_DESCRIPTION,
                "Vyhledejte a načtěte plán služeb"
        );
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_O);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JComponent source = (JComponent) e.getSource();
        controller.selectShiftPlanFile(source);
    }
}
