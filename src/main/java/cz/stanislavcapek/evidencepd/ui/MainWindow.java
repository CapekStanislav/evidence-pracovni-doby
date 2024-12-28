package cz.stanislavcapek.evidencepd.ui;

import com.google.inject.Inject;
import cz.stanislavcapek.evidencepd.employee.Employee;
import cz.stanislavcapek.evidencepd.employee.EmployeeListModel;
import cz.stanislavcapek.evidencepd.employee.EmployeeService;
import cz.stanislavcapek.evidencepd.ui.action.ActionFactory;
import cz.stanislavcapek.evidencepd.ui.component.WorkAttendanceLoadPanel;
import cz.stanislavcapek.evidencepd.ui.component.employee.EmployeeListPanel;
import cz.stanislavcapek.evidencepd.ui.component.template.WorkAttendanceTemplatePanel;
import cz.stanislavcapek.evidencepd.ui.component.workattendance.ShiftPlanLoadAction;
import cz.stanislavcapek.evidencepd.ui.controller.ShiftPlanController;
import jiconfont.icons.elusive.Elusive;
import jiconfont.swing.IconFontSwing;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;

/**
 * Main application window
 *
 * @author Stanislav Capek
 */
public class MainWindow extends JFrame {
    public static final String TITLE = "Správa evidence pracovní doby";

    private final Action closeAction;
    private final EmployeeService employeeService;
    private final EmployeeListModel employeeListModel;

    @Inject
    public MainWindow(
            WorkAttendanceLoadPanel workAttendanceLoadPanel,
            ActionFactory actionFactory,
            EmployeeService employeeService,
            EmployeeListModel employeeListModel,
            EmployeeListPanel employeeListPanel,
            WorkAttendanceTemplatePanel workAttendanceTemplatePanel,
            ShiftPlanController shiftPlanController
    ) {
        super(TITLE);
        this.employeeService = employeeService;
        this.employeeListModel = employeeListModel;
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        int height = 400;
        int width = 500;
        Dimension size = new Dimension(width, height);
        this.setMinimumSize(size);
        this.setPreferredSize(size);
        this.setResizable(false);
        this.setLocationRelativeTo(null);

        List<Employee> employeeList = employeeService.load();
        if (employeeList.isEmpty()) {
            findEmployeeListFile();
        }
        initEmployeeListModel(employeeList);

        JPanel contentPane = new JPanel(new BorderLayout());

        final JPanel cards = new JPanel(new CardLayout());

        final String evidenceTitle = "evidence";
        cards.add(workAttendanceLoadPanel, evidenceTitle);
        final String listTitle = "seznam";
        cards.add(employeeListPanel, listTitle);
        final String templateTitle = "šablona";
        cards.add(workAttendanceTemplatePanel, templateTitle);

//        Actions
        IconFontSwing.register(Elusive.getIconFont());
        int iSizeSmall = 12;
        int iSizeLarge = 16;

        closeAction = actionFactory.createCloseApp("Zavřít", "Ukončit program", KeyEvent.VK_Z);

        Action genEvidenceAction = actionFactory.createShowAgend("Gener. evidence",
                "Generování evidence",
                KeyEvent.VK_G, evidenceTitle, cards);
        genEvidenceAction.putValue(Action.SMALL_ICON, IconFontSwing.buildIcon(Elusive.TIME, iSizeSmall));
        genEvidenceAction.putValue(Action.LARGE_ICON_KEY, IconFontSwing.buildIcon(Elusive.TIME, iSizeLarge));

        Action zobrSeznamAction = actionFactory.createShowAgend("Seznam zaměstnanců",
                "Zobrazit seznam zaměstnanců",
                KeyEvent.VK_S, listTitle, cards);
        zobrSeznamAction.putValue(Action.SMALL_ICON, IconFontSwing.buildIcon(Elusive.ADDRESS_BOOK, iSizeSmall));
        zobrSeznamAction.putValue(Action.LARGE_ICON_KEY, IconFontSwing.buildIcon(Elusive.ADDRESS_BOOK, iSizeLarge));

        Action sablonaAction = actionFactory.createShowAgend("Šablona plánu",
                "Generování šablony pro zadaný rok",
                KeyEvent.VK_B, templateTitle, cards);
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

        final ShiftPlanLoadAction shiftPlanLoadAction = new ShiftPlanLoadAction("Načíst šablonu", shiftPlanController);
        final JMenuItem nacistItem = new JMenuItem(shiftPlanLoadAction);
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
                    workAttendanceLoadPanel.validateLoadedTemplate(shiftPlanLoadAction, evt);

                }

        );
        menuFile.add(nacistItem);

        menuFile.add(actionFactory.createSaveEmployees(this,
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
                final List<Employee> employeeList = employeeService.load(selectedFile.toPath());
                if (employeeList.isEmpty()) {
                    findEmployeeListFile();
                } else {
                    initEmployeeListModel(employeeList);
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


}
