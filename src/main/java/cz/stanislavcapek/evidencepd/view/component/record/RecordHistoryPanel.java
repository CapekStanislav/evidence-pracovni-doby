package cz.stanislavcapek.evidencepd.view.component.record;

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
 * An instance of class {@code RecordHistoryPanel}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public class RecordHistoryPanel extends JPanel {

    private boolean isChosen = false;
    private String selection;

    public RecordHistoryPanel() {

        List<String> fileNames;
        try {
            fileNames = loadFileNames(ConfigPaths.RECORDS_PATH);
        } catch (IOException e) {
            fileNames = Collections.emptyList();
       }

        final JList<String> recordList = new JList<>(fileNames.toArray(String[]::new));
        final JScrollPane jScrollPane = new JScrollPane();
        jScrollPane.setViewportView(recordList);
        this.add(jScrollPane);

        recordList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selection = recordList.getSelectedValue();
            }
        });
    }


    /**
     * @return může být null při zrušení nabídky
     */
    public String getRecordName() {
        return isChosen ? selection : null;
    }

    public boolean isChosen() {
        return isChosen;
    }

    public void isChosen(Consumer<String> consumer) {
        if (isChosen) {
            consumer.accept(selection);
        }
    }

    public <R> R map(Function<String, ? extends R> function) {
        return function.apply(isChosen ? selection : "");
    }

    public String orElseGet(Supplier<String> supplier) {
        if (isChosen) {
            return selection;
        } else {
            return supplier.get();
        }
    }

    public RecordHistoryPanel showListDialog() {
        final String title = "Výběr z archivu evidencí";
        Object[] options = {"Načíst", "Zrušit"};
        final int choice = JOptionPane.showOptionDialog(
                null,
                this,
                title,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        isChosen = choice == JOptionPane.OK_OPTION && selection != null;

        return this;
    }

    private List<String> loadFileNames(Path dir) throws IOException {
        return Files.walk(dir, 1)
                .filter(path -> path.getFileName().toString().matches("^evidence-\\d{4}-\\d{1,2}\\.json$"))
                .map(Path::getFileName)
                .map(path -> path.toString().split("\\.")[0])
                .sorted(Comparator.comparing(this::keyExtractor).reversed())
                .collect(Collectors.toList());
    }

    private int keyExtractor(String name) {
        final String[] split = name.split("-");
        final int month = Integer.parseInt(split[split.length - 1]);
        final int year = Integer.parseInt(split[split.length - 2]);
        return year * 100 + month;
    }
}
