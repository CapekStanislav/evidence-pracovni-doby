package cz.stanislavcapek.evidencepd.app;

import cz.stanislavcapek.evidencepd.view.HlavniOkno;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * An instance of class {@code Main}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public class Main {

    private static void vytvorAZobrazGUI() {
        // JFrame
        JFrame okno = new HlavniOkno();

        // zobrazení okna
        okno.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            }
            vytvorAZobrazGUI();
        });
    }

}
