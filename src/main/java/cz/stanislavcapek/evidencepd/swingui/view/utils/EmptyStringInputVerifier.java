package cz.stanislavcapek.evidencepd.swingui.view.utils;

import javax.swing.*;
import java.awt.*;

/**
 * Validátor textového řetězce. Při neplatném vstupu obarví element na žluto.
 */
public class EmptyStringInputVerifier extends InputVerifier {

    @Override
    public boolean verify(JComponent input) {
        if (input instanceof JTextField) {
            return !((JTextField) input).getText().equals("");
        }
        return false;
    }

    @Override
    public boolean shouldYieldFocus(JComponent source, JComponent target) {
        if (verify(source)) {
            source.setBackground(Color.WHITE);
            return true;
        } else {
            source.setBackground(Color.YELLOW);
            return false;
        }
    }
}
