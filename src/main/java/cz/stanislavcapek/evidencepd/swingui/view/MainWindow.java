package cz.stanislavcapek.evidencepd.swingui.view;

import com.google.inject.Inject;
import cz.stanislavcapek.evidencepd.swingui.controller.ShiftPlanController;
import cz.stanislavcapek.evidencepd.swingui.view.action.ActionFactory;
import cz.stanislavcapek.evidencepd.swingui.view.employee.EmployeeListPanel;
import cz.stanislavcapek.evidencepd.swingui.view.employee.EmployeesLoader;
import cz.stanislavcapek.evidencepd.swingui.view.shiftplan.LoadShiftPlanPanel;
import cz.stanislavcapek.evidencepd.swingui.view.template.WorkAttendanceTemplatePanel;
import cz.stanislavcapek.evidencepd.swingui.view.workattendance.ShiftPlanLoadAction;
import jiconfont.icons.elusive.Elusive;
import jiconfont.swing.IconFontSwing;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Main application window
 *
 * @author Stanislav Capek
 */
public class MainWindow extends JFrame {
    private static final Logger log = LogManager.getLogger(MainWindow.class);

    private enum Cards {
        LOAD_SHIFT_PLAN,
        EMPLOYEES,
        WORKATTENDANCE_TEMPLATE
    }

    public static final String TITLE = "Správa evidence pracovní doby";
    public static final Dimension WINDOW_DIMENSION = new Dimension(500, 400);

    private final Action closeAction;

    @Inject
    public MainWindow(
            LoadShiftPlanPanel loadShiftPlanPanel,
            ActionFactory actionFactory,
            EmployeesLoader employeesLoader,
            EmployeeListPanel employeeListPanel,
            WorkAttendanceTemplatePanel workAttendanceTemplatePanel,
            ShiftPlanController shiftPlanController
    ) {
        super(TITLE);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.setMinimumSize(WINDOW_DIMENSION);
        this.setPreferredSize(WINDOW_DIMENSION);
        this.setResizable(false);
        this.setLocationRelativeTo(null);

        employeesLoader.tryLoadEmployees();

        JPanel contentPane = new JPanel(new BorderLayout());

        final JPanel cards = new JPanel(new CardLayout());

        cards.add(loadShiftPlanPanel, Cards.LOAD_SHIFT_PLAN.name());
        cards.add(employeeListPanel, Cards.EMPLOYEES.name());
        cards.add(workAttendanceTemplatePanel, Cards.WORKATTENDANCE_TEMPLATE.name());

//        Actions
        IconFontSwing.register(Elusive.getIconFont());
        int iSizeSmall = 12;
        int iSizeLarge = 16;

        closeAction = actionFactory.createCloseApp("Zavřít", "Ukončit program", KeyEvent.VK_Z);

        Action genEvidenceAction = actionFactory.createDisplayCard("Gener. evidence",
                "Generování evidence",
                KeyEvent.VK_G, Cards.LOAD_SHIFT_PLAN.name(), cards);
        genEvidenceAction.putValue(Action.SMALL_ICON, IconFontSwing.buildIcon(Elusive.TIME, iSizeSmall));
        genEvidenceAction.putValue(Action.LARGE_ICON_KEY, IconFontSwing.buildIcon(Elusive.TIME, iSizeLarge));

        Action zobrSeznamAction = actionFactory.createDisplayCard("Seznam zaměstnanců",
                "Zobrazit seznam zaměstnanců",
                KeyEvent.VK_S, Cards.EMPLOYEES.name(), cards);
        zobrSeznamAction.putValue(Action.SMALL_ICON, IconFontSwing.buildIcon(Elusive.ADDRESS_BOOK, iSizeSmall));
        zobrSeznamAction.putValue(Action.LARGE_ICON_KEY, IconFontSwing.buildIcon(Elusive.ADDRESS_BOOK, iSizeLarge));

        Action sablonaAction = actionFactory.createDisplayCard("Šablona plánu",
                "Generování šablony pro zadaný rok",
                KeyEvent.VK_B, Cards.WORKATTENDANCE_TEMPLATE.name(), cards);
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
                    loadShiftPlanPanel.validateLoadedTemplate(shiftPlanLoadAction, evt);

                }
        );
        menuFile.add(nacistItem);

        menuFile.add(actionFactory.createSaveEmployees(
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
}
