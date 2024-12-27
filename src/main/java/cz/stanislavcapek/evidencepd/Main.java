package cz.stanislavcapek.evidencepd;

import com.google.inject.Guice;
import com.google.inject.Injector;
import cz.stanislavcapek.evidencepd.ui.MainWindow;

import java.awt.*;

/**
 * An instance of class {@code Main}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public class Main {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new MainModule());

        EventQueue.invokeLater(() -> {
            MainWindow mainWindow = injector.getInstance(MainWindow.class);
            mainWindow.setVisible(true);
        });
    }

}
