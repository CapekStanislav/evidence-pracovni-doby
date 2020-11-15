package cz.stanislavcapek.evidencepd.view.component.utils;

import javax.swing.JOptionPane;

/**
 * An instance of class {@code InfoWindow}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public class InfoWindow {

    private InfoWindow() {
    }

    public static void showInfo (String message) {
        showInfo(message,"Informace");
    }

    public static void showInfo (String message, String title) {
        JOptionPane.showMessageDialog(
                null,
                message,
                title,
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void showError(String message) {
        showError(message, "Chyba");
    }

    private static void showError(String message, String title) {
        JOptionPane.showMessageDialog(
                null,
                message,
                title,
                JOptionPane.ERROR_MESSAGE
        );
    }
}
