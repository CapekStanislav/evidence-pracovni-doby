package cz.stanislavcapek.evidencepd.view.component.utils;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.Color;

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
