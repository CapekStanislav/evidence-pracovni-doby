package cz.stanislavcapek.evidencepd.view;

import cz.stanislavcapek.evidencepd.appconfig.ConfigPaths;
import cz.stanislavcapek.evidencepd.zamestnanec.ZamestnanciDao;
import cz.stanislavcapek.evidencepd.zamestnanec.ZamestnanciListModel;
import cz.stanislavcapek.evidencepd.zamestnanec.Zamestnanec;
import cz.stanislavcapek.evidencepd.view.component.GenEvidenceTab;
import cz.stanislavcapek.evidencepd.view.component.NacistSablonuAction;
import cz.stanislavcapek.evidencepd.view.component.SablonaRokTab;
import cz.stanislavcapek.evidencepd.view.component.ZobrazeniSeznamuZamestnancuTab;
import jiconfont.icons.elusive.Elusive;
import jiconfont.swing.IconFontSwing;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Hlavní okno aplikace.
 *
 * @author Stanislav Čapek
 */
public class HlavniOkno extends JFrame {
    private final Path initFile = Paths.get("seznamZamestnancu.json");
    private final Path zamestnanciDir = ConfigPaths.ZAMESTNANCI.resolve(initFile);
    private final Action closeAction;
    private final ZamestnanciDao zamestnanciDao = new ZamestnanciDao();

    /**
     * Konstruktor bez parametru
     */
    public HlavniOkno() {
        super("Správa evidence pracovní doby");
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        int vyska = 400;
        int sirka = 500;
        Dimension size = new Dimension(sirka, vyska);
        this.setMinimumSize(size);
        this.setPreferredSize(size);
        this.setResizable(false);
        this.setLocationRelativeTo(null);

        try {
            List<Zamestnanec> seznam = zamestnanciDao.nacti(zamestnanciDir);
            pridejDoSeznamuStrazniku(seznam);
        } catch (RuntimeException | IOException e) {
            naleztSouborSeznamZamestnancu();
        }

        JPanel contentPane = new JPanel(new BorderLayout());

        final GenEvidenceTab genEvidenceTab = new GenEvidenceTab();
        final JPanel zamestnancuTab = new ZobrazeniSeznamuZamestnancuTab();
        final JPanel sablonaRokTab = new SablonaRokTab();

        final JPanel cards = new JPanel(new CardLayout());

        final String evidenceString = "evidence";
        cards.add(genEvidenceTab, evidenceString);
        final String seznamString = "seznam";
        cards.add(zamestnancuTab, seznamString);
        final String sablonaString = "sablona";
        cards.add(sablonaRokTab, sablonaString);

//        Actions
        IconFontSwing.register(Elusive.getIconFont());
        int iSizeSmall = 12;
        int iSizeLarge = 16;

        closeAction = new CloseAction("Zavřít", "Vypnout program", KeyEvent.VK_Z);

        Action genEvidenceAction = new ZobrazeniAgendyAction("Gener. evidence",
                "Generování evidence",
                KeyEvent.VK_G, evidenceString, cards);
        genEvidenceAction.putValue(Action.SMALL_ICON, IconFontSwing.buildIcon(Elusive.TIME, iSizeSmall));
        genEvidenceAction.putValue(Action.LARGE_ICON_KEY, IconFontSwing.buildIcon(Elusive.TIME, iSizeLarge));

        Action zobrSeznamAction = new ZobrazeniAgendyAction("Seznam zaměstnanců",
                "Zobrazit seznam zaměstnanců",
                KeyEvent.VK_S, seznamString, cards);
        zobrSeznamAction.putValue(Action.SMALL_ICON, IconFontSwing.buildIcon(Elusive.ADDRESS_BOOK, iSizeSmall));
        zobrSeznamAction.putValue(Action.LARGE_ICON_KEY, IconFontSwing.buildIcon(Elusive.ADDRESS_BOOK, iSizeLarge));

        Action sablonaAction = new ZobrazeniAgendyAction("Šablona plánu",
                "Generování šablony pro zadaný rok",
                KeyEvent.VK_B, sablonaString, cards);
        sablonaAction.putValue(Action.SMALL_ICON, IconFontSwing.buildIcon(Elusive.FILE_NEW, iSizeSmall));
        sablonaAction.putValue(Action.LARGE_ICON_KEY, IconFontSwing.buildIcon(Elusive.FILE_NEW, iSizeLarge));

        JToolBar toolBar = new JToolBar("Agendy");
        toolBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        toolBar.add(genEvidenceAction);
        toolBar.addSeparator();
        toolBar.add(zobrSeznamAction);
        toolBar.addSeparator();
        toolBar.add(sablonaAction);

        contentPane.add(toolBar, BorderLayout.PAGE_START);
        contentPane.add(cards, BorderLayout.CENTER);
        this.setContentPane(contentPane);

//        MENU
        JMenuBar menuBar = new JMenuBar();

        JMenu menuFile = new JMenu("Soubor");

        final NacistSablonuAction nacistSablonuAction = new NacistSablonuAction("Načíst šablonu");
        final JMenuItem nacistItem = new JMenuItem(nacistSablonuAction);
        nacistItem.addPropertyChangeListener(
                "loaded",
                evt -> {
                    genEvidenceAction.actionPerformed(
                            new ActionEvent(
                                    genEvidenceAction,
                                    ActionEvent.ACTION_PERFORMED,
                                    genEvidenceAction.getValue(Action.ACTION_COMMAND_KEY).toString()
                            )
                    );
                    genEvidenceTab.validaceNacteniSablony(nacistSablonuAction, evt);

                }

        );
        menuFile.add(nacistItem);

        menuFile.add(new UlozeniSeznamuAction(
                "Uložit seznam",
                "Uložit seznam zaměstnanců",
                KeyEvent.VK_S));
        menuFile.addSeparator();
        menuFile.add(closeAction);


        JMenu menuAgenda = new JMenu("Agenda");
        menuAgenda.add(genEvidenceAction);
        menuAgenda.add(zobrSeznamAction);
        menuAgenda.add(sablonaAction);


        menuBar.add(menuFile);
        menuBar.add(menuAgenda);
        this.setJMenuBar(menuBar);


        // nastavení okna
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                closeAction.actionPerformed(new ActionEvent(e.getSource(),
                        ActionEvent.ACTION_PERFORMED,
                        "WindowClosing"));
            }

        });

        this.pack();
    }

    /**
     * Načte do instance {@link ZamestnanciListModel} nově načtené zaměstnance
     *
     * @param seznam seznam zaměstnanců
     */
    private void pridejDoSeznamuStrazniku(List<Zamestnanec> seznam) {
        ZamestnanciListModel zamestnanciListModel = ZamestnanciListModel.getInstance();
        for (Zamestnanec s :
                seznam) {
            zamestnanciListModel.pridejZamestnance(s);
        }
    }

    /**
     * Umožní uživateli nalézt soubor obsahující seznam zaměstnanců ručně.
     */
    private void naleztSouborSeznamZamestnancu() {
        // TODO: 13.10.2020 rozhodně předělat implementaci a rozdělit na více kroků
        int volba = 0;
        while (volba == 0) {
            Object[] anoNe = {"Ano", "Ne"};
            volba = JOptionPane.showOptionDialog(this, "Nepodařilo se nalézt seznam " +
                            "strážníků. \n" +
                            "Soubor buď neexistuje nebo je prázdný.\n \n" +
                            "Vyhledat seznam ručně?",
                    "Chyba při načtení souboru",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE,
                    null,
                    anoNe,
                    anoNe[0]);

            if (volba == JOptionPane.YES_OPTION) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileFilter(new FileNameExtensionFilter("JSON (*.json)", "json"));
                chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
                volba = chooser.showDialog(this, "otevřít");

                if (volba == JFileChooser.APPROVE_OPTION) {
                    try {
                        Path file = Paths.get(chooser.getSelectedFile().getPath());
                        List<Zamestnanec> seznam;
                        if ((seznam = zamestnanciDao.nacti(file)) != null) {
                            pridejDoSeznamuStrazniku(seznam);
                            volba = 1;
                        } else {
                            volba = 0;
                        }
                    } catch (Exception ex) {
                        volba = 0;
                    }
                } else {
                    volba = 1;
                }
            }
        }
    }

    /**
     * AKCE - zobrazení agendy pomocí ToolBaru
     */
    private static class ZobrazeniAgendyAction extends AbstractAction {

        private final JPanel content;

        ZobrazeniAgendyAction(String name, String popis, int mnemonic, String command, JPanel content) {
            super(name);
            this.content = content;
            putValue(Action.SHORT_DESCRIPTION, popis);
            putValue(Action.MNEMONIC_KEY, mnemonic);
            putValue(Action.ACTION_COMMAND_KEY, command);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ((CardLayout) content.getLayout()).show(content, e.getActionCommand());
        }
    }

    /**
     * AKCE - uzavření okna
     */
    private class CloseAction extends AbstractAction {

        CloseAction(String name, String popis, int mnemonic) {
            super(name);
            IconFontSwing.register(Elusive.getIconFont());
            final Elusive closeIcon = Elusive.OFF;
            Icon closeSmall = IconFontSwing.buildIcon(closeIcon, 16);
            Icon closeLarge = IconFontSwing.buildIcon(closeIcon, 24);

            putValue(Action.SHORT_DESCRIPTION, popis);
            putValue(Action.MNEMONIC_KEY, mnemonic);
            putValue(Action.SMALL_ICON, closeSmall);
            putValue(Action.LARGE_ICON_KEY, closeLarge);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object[] anoNe = {"Ano", "Ne"};
            int odpoved = JOptionPane.showOptionDialog(null,
                    "Opravdu si přejete ukončit program? \n \n",
                    "Ukončit program?",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, anoNe, anoNe[1]);
            if (odpoved == JOptionPane.OK_OPTION) {
                try {
                    zamestnanciDao.uloz(zamestnanciDir, ZamestnanciListModel.getInstance().getSeznam());
                    System.exit(0);
                } catch (IOException e1) {
                    System.exit(0);
                    e1.printStackTrace();
                }
            }
        }
    }

    private class UlozeniSeznamuAction extends AbstractAction {

        UlozeniSeznamuAction(String name, String popis, int mnemonic) {
            super(name);
            IconFontSwing.register(Elusive.getIconFont());
            Elusive save = Elusive.DOWNLOAD_ALT;
            Icon saveIconSmall = IconFontSwing.buildIcon(save, 12);
            Icon savIconLarge = IconFontSwing.buildIcon(save, 16);
            putValue(Action.SHORT_DESCRIPTION, popis);
            putValue(MNEMONIC_KEY, mnemonic);
            putValue(SMALL_ICON, saveIconSmall);
            putValue(LARGE_ICON_KEY, savIconLarge);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final JFileChooser fileChooser = new JFileChooser();
            final String userDir = System.getProperty("user.dir");
            final File pathToUserDir = new File(userDir);
            fileChooser.setCurrentDirectory(pathToUserDir);
            fileChooser.setFileFilter(new FileNameExtensionFilter("JSON", "json"));
            fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
            final File preFile = new File("seznam zamestancu");
            fileChooser.setSelectedFile(preFile);

            boolean hotovo = false;
            while (!hotovo) {
                final int volba = fileChooser.showSaveDialog(HlavniOkno.this);
                if (volba == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();

                    final String suffix = ".json";
                    if (!selectedFile.getName().toLowerCase().contains(suffix)) {
                        selectedFile = new File(selectedFile.getAbsolutePath() + suffix);
                    }

                    int overwrite = 0;
                    if (selectedFile.exists()) {
                        overwrite = JOptionPane.showConfirmDialog(HlavniOkno.this,
                                "Soubor již existuje! Chcete ho přepsat?",
                                "Existujicí soubor",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE);
                    }

                    if (overwrite == 0) {
                        try {
                            zamestnanciDao.uloz(
                                    Paths.get(selectedFile.toURI()),
                                    ZamestnanciListModel.getInstance().getSeznam()
                            );
                            hotovo = true;
                        } catch (Exception ex) {
                            showErrorMessageDialog();
                        }
                    }

                } else {
                    hotovo = true;
                }
            }
        }

        private void showErrorMessageDialog() {
            JOptionPane.showMessageDialog(HlavniOkno.this,
                    "Nepodařilo se uložit soubor.",
                    "Chyba při ukládání", JOptionPane.ERROR_MESSAGE);
        }
    }


}
