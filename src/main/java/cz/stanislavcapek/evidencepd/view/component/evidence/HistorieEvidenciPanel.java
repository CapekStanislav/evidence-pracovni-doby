package cz.stanislavcapek.evidencepd.view.component.evidence;

import cz.stanislavcapek.evidencepd.appconfig.ConfigPaths;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * An instance of class {@code HistorieEvidenciPanel}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public class HistorieEvidenciPanel extends JPanel {

    private boolean vybrano = false;
    private String vyber;

    public HistorieEvidenciPanel() {

        List<String> nazvySouboru;
        try {
            nazvySouboru = nactiNazvySouboru(ConfigPaths.EVIDENCE);
        } catch (IOException e) {
            nazvySouboru = Collections.emptyList();
            e.printStackTrace();
        }

        final JList<String> archivList = new JList<>(nazvySouboru.toArray(String[]::new));
        final JScrollPane jScrollPane = new JScrollPane();
        jScrollPane.setViewportView(archivList);
        this.add(jScrollPane);

        archivList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                vyber = archivList.getSelectedValue();
            }
        });
    }


    /**
     * @return může být null při zrušení nabídky
     */
    public String ziskejNazev() {
        return vybrano ? vyber : null;
    }

    public boolean jeVybrano() {
        return vybrano;
    }

    public void jeVybrano(Consumer<String> consumer) {
        if (vybrano) {
            consumer.accept(vyber);
        }
    }

    public <R> R map(Function<String, ? extends R> function) {
        return function.apply(vybrano ? vyber : "");
    }

    public String neboJinyNazev(Supplier<String> supplier) {
        if (vybrano) {
            return vyber;
        } else {
            return supplier.get();
        }
    }

    public HistorieEvidenciPanel zobrazNabidku() {
        final String titulek = "Výběr z archivu evidencí";
        Object[] moznosti = {"Načíst", "Zrušit"};
        final int volba = JOptionPane.showOptionDialog(
                null,
                this,
                titulek,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                moznosti,
                moznosti[0]
        );

        vybrano = volba == JOptionPane.OK_OPTION && vyber != null;

        return this;
    }

    private List<String> nactiNazvySouboru(Path slozka) throws IOException {
        return Files.walk(slozka, 1)
                .filter(path -> path.getFileName().toString().matches("^evidence-\\d{4}-\\d{1,2}\\.json$"))
                .map(Path::getFileName)
                .map(path -> path.toString().split("\\.")[0])
                .sorted(Comparator.comparing(this::vytvorKlicProPorovnani).reversed())
                .collect(Collectors.toList());
    }

    private int vytvorKlicProPorovnani(String nazev) {
        final String[] split = nazev.split("-");
        final int mesic = Integer.parseInt(split[split.length - 1]);
        final int rok = Integer.parseInt(split[split.length - 2]);
        return rok * 100 + mesic;


    }
}
