package cz.stanislavcapek.evidencepd;

import cz.stanislavcapek.evidencepd.view.MainWindow;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.EventQueue;

/**
 * An instance of class {@code Main}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        final ConfigurableApplicationContext ctx = new SpringApplicationBuilder(Main.class)
                .headless(false)
                .run(args);
        EventQueue.invokeLater(() -> {
            final MainWindow bean = ctx.getBean(MainWindow.class);
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ignored) {
            }
            bean.setVisible(true);
        });
    }

}
