package cz.stanislavcapek.evidencepd.view;

import cz.stanislavcapek.evidencepd.appconfig.ConfigPaths;
import cz.stanislavcapek.evidencepd.employee.Employee;
import cz.stanislavcapek.evidencepd.employee.EmployeeListModel;
import cz.stanislavcapek.evidencepd.employee.EmployeesDao;
import cz.stanislavcapek.evidencepd.view.component.TemplateLoaderAction;
import cz.stanislavcapek.evidencepd.view.component.WorkAttendanceLoadPanel;
import cz.stanislavcapek.evidencepd.view.component.WorkAttendanceTemplatePanel;
import cz.stanislavcapek.evidencepd.view.component.EmployeeListPanel;
import jiconfont.icons.elusive.Elusive;
import jiconfont.swing.IconFontSwing;
import org.springframework.stereotype.Component;

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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Hlavní okno aplikace.
 *
 * @author Stanislav Čapek
 */
@Component
public class MainWindow extends JFrame {
    public static final String TITLE = "Správa evidence pracovní doby";
    private final Path employeeListFile = Paths.get("seznamZamestnancu.json");
    private final Path employeeListFilePath = ConfigPaths.EMPLOYEES_PATH.resolve(employeeListFile);
    private final Action closeAction;
    private final EmployeesDao employeesDao = new EmployeesDao();

    /**
     * Konstruktor bez parametru
     */
    public MainWindow() {
        super(TITLE);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        int height = 400;
        int width = 500;
        Dimension size = new Dimension(width, height);
        this.setMinimumSize(size);
        this.setPreferredSize(size);
        this.setResizable(false);
        this.setLocationRelativeTo(null);

        try {
            List<Employee> employeeList = employeesDao.load(employeeListFilePath);
            initEmployeeListModel(employeeList);
        } catch (RuntimeException | IOException e) {
            findEmployeeListFile();
        }

        JPanel contentPane = new JPanel(new BorderLayout());

        final WorkAttendanceLoadPanel workAttendanceLoadPanel = new WorkAttendanceLoadPanel();
        final JPanel zamestnancuTab = new EmployeeListPanel();
        final JPanel sablonaRokTab = new WorkAttendanceTemplatePanel();

        final JPanel cards = new JPanel(new CardLayout());

        final String evidenceString = "evidence";
        cards.add(workAttendanceLoadPanel, evidenceString);
        final String seznamString = "seznam";
        cards.add(zamestnancuTab, seznamString);
        final String sablonaString = "sablona";
        cards.add(sablonaRokTab, sablonaString);

//        Actions
        IconFontSwing.register(Elusive.getIconFont());
        int iSizeSmall = 12;
        int iSizeLarge = 16;

        closeAction = new CloseAction("Zavřít", "Ukončit program", KeyEvent.VK_Z);

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

        final TemplateLoaderAction templateLoaderAction = new TemplateLoaderAction("Načíst šablonu");
        final JMenuItem nacistItem = new JMenuItem(templateLoaderAction);
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
                    workAttendanceLoadPanel.validateLoadedTemplate(templateLoaderAction, evt);

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
     * Načte do instance {@link EmployeeListModel} nově načtené zaměstnance
     *
     * @param list seznam zaměstnanců
     */
    private void initEmployeeListModel(List<Employee> list) {
        EmployeeListModel employeeListModel = EmployeeListModel.getInstance();
        employeeListModel.clearList();
        list.forEach(employeeListModel::addEmployee);
    }

    /**
     * Umožní uživateli nalézt soubor obsahující seznam zaměstnanců ručně.
     */
    private void findEmployeeListFile() {

        if (showListNotFoundDialog() == JOptionPane.YES_OPTION) {
            final JFileChooser chooser = getChooserForJsonFiles();

            if (chooser.showOpenDialog(this) == JOptionPane.YES_OPTION) {
                final File selectedFile = chooser.getSelectedFile();
                try {
                    final List<Employee> employeeList = employeesDao.load(selectedFile.toPath());
                    initEmployeeListModel(employeeList);
                } catch (IOException e) {
                    findEmployeeListFile();
                    e.printStackTrace();
                }
            }

        }

    }

    private JFileChooser getChooserForJsonFiles() {
        JFileChooser chooser = new JFileChooser();
        final FileNameExtensionFilter jsonFilter = new FileNameExtensionFilter("JSON (*.json)", "json");
        chooser.setFileFilter(jsonFilter);
        chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        chooser.setApproveButtonText("otevřít");
        return chooser;
    }

    private int showListNotFoundDialog() {
        Object[] option = {"Ano", "Pokračovat"};
        final String message = "Nepodařilo se nalézt seznam zaměstnanců. \n \n" +
                "Vyhledat seznam ručně?";
        final String title = "Chyba při načtení souboru";

        return JOptionPane.showOptionDialog(
                this,
                message,
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE,
                null,
                option,
                option[0]);
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
                    employeesDao.save(employeeListFilePath, EmployeeListModel.getInstance().getEmployeeList());
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
                final int volba = fileChooser.showSaveDialog(MainWindow.this);
                if (volba == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();

                    final String suffix = ".json";
                    if (!selectedFile.getName().toLowerCase().contains(suffix)) {
                        selectedFile = new File(selectedFile.getAbsolutePath() + suffix);
                    }

                    int overwrite = 0;
                    if (selectedFile.exists()) {
                        overwrite = JOptionPane.showConfirmDialog(MainWindow.this,
                                "Soubor již existuje! Chcete ho přepsat?",
                                "Existujicí soubor",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE);
                    }

                    if (overwrite == 0) {
                        try {
                            employeesDao.save(
                                    Paths.get(selectedFile.toURI()),
                                    EmployeeListModel.getInstance().getEmployeeList()
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
            JOptionPane.showMessageDialog(MainWindow.this,
                    "Nepodařilo se uložit soubor.",
                    "Chyba při ukládání", JOptionPane.ERROR_MESSAGE);
        }
    }


}
