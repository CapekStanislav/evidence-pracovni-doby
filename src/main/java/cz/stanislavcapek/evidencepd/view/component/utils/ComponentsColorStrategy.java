package cz.stanislavcapek.evidencepd.view.component.utils;

import javax.swing.JComponent;
import java.awt.Color;

/**
 * An instance of class {@code ComponentsColorStrategy}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public class ComponentsColorStrategy {

    public static boolean colorComponent(boolean isValid, JComponent component) {
        if (isValid) {
            component.setBackground(Color.white);
            return true;
        } else {
            component.setBackground(Color.YELLOW);
            return false;
        }
    }

}
