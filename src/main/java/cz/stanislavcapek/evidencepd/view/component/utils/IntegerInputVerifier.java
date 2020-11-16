package cz.stanislavcapek.evidencepd.view.component.utils;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;
import java.awt.Color;

/**
 * An instance of class {@code IntegerInputVerifier} validate Integer input.
 * If is not valid, it colors it to yellow.
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public class IntegerInputVerifier extends InputVerifier {


    @Override
    public boolean verify(JComponent input) {
        if (input instanceof JTextField) {
            try {
                final String text = ((JTextField) input).getText();
                Integer.parseInt(text);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean shouldYieldFocus(JComponent source, JComponent target) {
        if (verify(source)) {
            source.setBackground(Color.white);
            return true;
        } else {
            source.setBackground(Color.YELLOW);
            return false;
        }
    }
}
