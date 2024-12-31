package cz.stanislavcapek.evidencepd.ui.action;

import cz.stanislavcapek.evidencepd.ui.controller.EmployeeController;
import jiconfont.icons.elusive.Elusive;
import jiconfont.swing.IconFontSwing;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Paths;

public class SaveEmployeeListAction extends AbstractAction {

    private final EmployeeController controller;

    SaveEmployeeListAction(
            String name,
            String desc,
            int mnemonic,
            EmployeeController controller
    ) {
        super(name);
        this.controller = controller;
        IconFontSwing.register(Elusive.getIconFont());
        Elusive save = Elusive.DOWNLOAD_ALT;
        Icon saveIconSmall = IconFontSwing.buildIcon(save, 12);
        Icon savIconLarge = IconFontSwing.buildIcon(save, 16);
        putValue(Action.SHORT_DESCRIPTION, desc);
        putValue(MNEMONIC_KEY, mnemonic);
        putValue(SMALL_ICON, saveIconSmall);
        putValue(LARGE_ICON_KEY, savIconLarge);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JComponent source = (JComponent) e.getSource();
        final JFileChooser fileChooser = new JFileChooser();
        final String userDir = System.getProperty("user.dir");
        final File pathToUserDir = new File(userDir);
        fileChooser.setCurrentDirectory(pathToUserDir);
        fileChooser.setFileFilter(new FileNameExtensionFilter("JSON", "json"));
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        final File preFile = new File("seznamZamestnancu.json");
        fileChooser.setSelectedFile(preFile);

        boolean done = false;
        while (!done) {
            final int choice = fileChooser.showSaveDialog(source);
            if (choice == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();

                final String suffix = ".json";
                if (!selectedFile.getName().toLowerCase().contains(suffix)) {
                    selectedFile = new File(selectedFile.getAbsolutePath() + suffix);
                }

                int overwrite = 0;
                if (selectedFile.exists()) {
                    overwrite = JOptionPane.showConfirmDialog(source,
                            "Soubor již existuje! Chcete ho přepsat?",
                            "Existujicí soubor",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);
                }

                if (overwrite == 0) {
                    try {
                        controller.saveModel(Paths.get(selectedFile.toURI()));
                        done = true;
                    } catch (Exception ex) {
                        showErrorMessageDialog(source);
                    }
                }

            } else {
                done = true;
            }
        }
    }

    private void showErrorMessageDialog(JComponent source) {
        JOptionPane.showMessageDialog(source,
                "Nepodařilo se uložit soubor.",
                "Chyba při ukládání", JOptionPane.ERROR_MESSAGE);
    }
}
