package cz.stanislavcapek.evidencepd.view.component.record;

import cz.stanislavcapek.evidencepd.appconfig.ConfigPaths;
import cz.stanislavcapek.evidencepd.dao.Dao;
import cz.stanislavcapek.evidencepd.workattendance.WorkAttendance;
import cz.stanislavcapek.evidencepd.workattendance.WorkAttendanceDao;
import cz.stanislavcapek.evidencepd.workattendance.DefaultWorkAttendance;
import cz.stanislavcapek.evidencepd.shiftplan.ShiftPlan;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Instance třídy {@code WorkAttendanceWindow}
 *
 * @author Stanislav Čapek
 */
public class WorkAttendanceWindow extends JFrame {
    private static final String TITLE = "Evidence pracovní doby";

    private JMenuItem mniSave;
    private JMenuItem mniSaveAs;
    private JMenuItem mniLoad;
    private JMenuItem mniPrint;
    private JMenuItem mniQuit;
    private final List<WorkAttendancePanel> pnlEmployeeList = new ArrayList<>();
    private WorkAttendancePanel currentPanel;
    private final JScrollPane contentPane = new JScrollPane();
    private final String nameFormat = "%s-%s-%s.%s";
    private final String recordStr = "evidence";
    private final String overtimeStr = "prescasy";
    private final String suffix = "json";
    private final JMenu menuEmployees = new JMenu("Zaměstnanci");
    private boolean isSaved = false;

    public WorkAttendanceWindow(ShiftPlan shiftPlan, int month) {

        pnlEmployeeList.clear();
        pnlEmployeeList.addAll(
                shiftPlan.getEmployeeIds()
                        .stream()
                        .filter(id -> shiftPlan.isEmployee(id, month))
                        .map(id -> getEvidencePanel(shiftPlan, month, id))
                        .collect(Collectors.toList())
        );

        initClass();
    }

    public WorkAttendanceWindow(String fileName) {
        loadStateFromFile(fileName);
        initClass();
    }

    private void initClass() {
        this.setTitle(TITLE);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        // menu
        final JMenuBar menubar = createMenuBar();
        updateEmployeesMenu(menuEmployees, pnlEmployeeList);
        menubar.add(menuEmployees);
        this.setJMenuBar(menubar);

        updateViewPort(pnlEmployeeList);

        this.setContentPane(contentPane);
        this.pack();

        initListeners();
    }

    private void updateViewPort(List<WorkAttendancePanel> panels) {
        if (!panels.isEmpty()) {
            final int index = 0;
            setTitleName(panels.get(index).getShiftRecord().getEmployee().getFullName());
            currentPanel = panels.get(index);
        }
        contentPane.setViewportView(currentPanel);
    }

    private WorkAttendancePanel getEvidencePanel(ShiftPlan shiftPlan, int month, int id) {
        return new WorkAttendancePanel(
                shiftPlan.getRecord(month, id),
                shiftPlan.getRecordOvertime(month, id));
    }

    private void setTitleName(String text) {
        this.setTitle(TITLE + " " + text);
    }

    private void initListeners() {
        mniLoad.addActionListener(e -> loadStateFromFile());
        mniSave.addActionListener(e -> saveStateToFile());
        mniSaveAs.addActionListener(e -> saveAs());
        mniPrint.addActionListener(e -> print());
        mniQuit.addActionListener(e -> setVisible(false));

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                showNotSaveDialog(e);
            }
        });
    }

    private void showNotSaveDialog(WindowEvent e) {
        if (!isSaved) {
            int volba = JOptionPane.showConfirmDialog(
                    WorkAttendanceWindow.this,
                    "Práce nebyla uložena. Chcete jí nyní uložit?",
                    "Ukončení bez uložení",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (volba == JOptionPane.YES_OPTION) {
                saveStateToFile();
                e.getWindow().dispose();
            } else if (volba == JOptionPane.NO_OPTION) {
                e.getWindow().dispose();
            }
        } else {
            e.getWindow().dispose();
        }
    }

    /**
     * @param fileName název souboru uložené evidence. Při vložení více názvů se bere
     *                 vždy jen první. Vynechání názvů souboru zobrazí nabídku uložených
     *                 evidencí.
     */
    private void loadStateFromFile(String... fileName) {

        if (fileName.length > 0) {
            final LocalDate date = parseDateFromFileName(fileName[0]);
            loadState(date);
        } else {
            final RecordHistoryPanel pnlRecordHistory = new RecordHistoryPanel().showListDialog();
            if (pnlRecordHistory.isChosen()) {
                final LocalDate date = pnlRecordHistory.map(this::parseDateFromFileName);
                loadState(date);
                showDoneMessage(
                        String.format("Načtení evidencí %s/%s", date.getMonthValue(), date.getYear())
                );
            }
        }
    }

    /**
     * @param date
     */
    private void loadState(LocalDate date) {
        Dao<List<WorkAttendance>> io = new WorkAttendanceDao();
        final int year = date.getYear();
        final int month = date.getMonthValue();

        final Path shiftsFile = Paths.get(String.format(nameFormat, recordStr, year, month, suffix));
        final Path overtimesFile = Paths.get(String.format(nameFormat, overtimeStr, year, month, suffix));

        List<WorkAttendance> workAttendanceList = null;

        try {
            workAttendanceList = io.load(ConfigPaths.RECORDS_PATH.resolve(shiftsFile));
        } catch (IOException e) {
            showErrorMessage(
                    String.format("Nepodařilo se nalézt požadovaný soubor evidence " +
                            "nebo je soubor poškozen: %s/%s", month, year)
            );
            this.dispose();
        }

        List<WorkAttendance> overtimeList;
        try {
            overtimeList = io.load(ConfigPaths.RECORDS_PATH.resolve(overtimesFile));
        } catch (IOException e) {
            showErrorMessage(
                    String.format("Nepodařilo se nalézt požadovaný soubor přesčasů " +
                            "nebo je soubor poškozen: %s/%s", month, year)

            );
            overtimeList = createEmptyOvertimeList(workAttendanceList);
        }

        pnlEmployeeList.clear();
        for (int i = 0; i < workAttendanceList.size(); i++) {
            final WorkAttendance workAttendance = workAttendanceList.get(i);
            final WorkAttendance prescasy = overtimeList.get(i);
            pnlEmployeeList.add(
                    new WorkAttendancePanel(workAttendance, prescasy)
            );
        }
        updateEmployeesMenu(menuEmployees, pnlEmployeeList);
        updateViewPort(pnlEmployeeList);
        isSaved = false;
    }

    private List<WorkAttendance> createEmptyOvertimeList(List<WorkAttendance> workAttendanceList) {
        List<WorkAttendance> list = new ArrayList<>();

        for (final WorkAttendance workAttendance : workAttendanceList) {
            list.add(
                    new DefaultWorkAttendance(
                            workAttendance.getEmployee(),
                            workAttendance.getMonth(),
                            workAttendance.getYear(),
                            workAttendance.getTypeOfWeeklyWorkingTime(),
                            workAttendance.getLastMonth(),
                            new TreeMap<>()
                    )
            );
        }
        return list;
    }

    private LocalDate parseDateFromFileName(String fileName) {
        final String[] split = fileName.split("-");
        int year = Integer.parseInt(split[1]);
        int month = Integer.parseInt(split[2]);
        return LocalDate.of(year, month, 1);
    }

    private void saveStateToFile() {
        final Dao<List<WorkAttendance>> io = new WorkAttendanceDao();

        final int year = currentPanel.getShiftRecord().getYear();
        final int month = currentPanel.getShiftRecord().getMonth().getNumber();

        final Path shiftsFile = Paths.get(String.format(nameFormat, recordStr, year, month, suffix));
        final Path overtimesFile = Paths.get(String.format(nameFormat, overtimeStr, year, month, suffix));

        final List<WorkAttendance> workAttendanceList = this.pnlEmployeeList.stream()
                .map(WorkAttendancePanel::getShiftRecord)
                .collect(Collectors.toList());

        final List<WorkAttendance> prescasyList = this.pnlEmployeeList.stream()
                .map(WorkAttendancePanel::getOvertimesRecord)
                .collect(Collectors.toList());

        try {
            io.save(ConfigPaths.RECORDS_PATH.resolve(shiftsFile), workAttendanceList);
        } catch (IOException e) {
            showErrorMessage("Nastala neočekávaná chyba při ukládání směn");
            e.printStackTrace();
        }
        try {
            io.save(ConfigPaths.RECORDS_PATH.resolve(overtimesFile), prescasyList);
        } catch (IOException e) {
            showErrorMessage("Nastala neočekávaná chyba při ukládání přesčasů");
            e.printStackTrace();
        }

        isSaved = true;
        showDoneMessage(String.format("WorkAttendance %s/%s byly úspěšně uloženy", month, year));
    }

    private void print() {
        if (currentPanel == null) {
            showErrorMessage("Není co tisknout");
        }
        try {
            if (currentPanel.printDocument()) {
                showDoneMessage("Dokument odeslán k tisku.");
            }
        } catch (Exception ex) {
            showErrorMessage("Během tisku došlo k neočekávané chybě");
            ex.printStackTrace();
        }
    }

    private void saveAs() {
        if (currentPanel == null) {
            showErrorMessage("Není co ukládat");
        }
        try {
            if (currentPanel.saveDocument()) {
                showDoneMessage("Soubor uložen.");
            }
        } catch (Exception ex) {
            showErrorMessage("Během ukládání došlo k neočekávané chybě.");
            ex.printStackTrace();
        }
    }

    /**
     * Vytvoří menu bar pro evidenci - uložení, tisk a ukončení
     *
     * @return vytvořené menu
     */
    private JMenuBar createMenuBar() {
        final JMenuBar jMenuBar = new JMenuBar();
        final JMenu souborMenu = new JMenu("Soubor");
        mniLoad = new JMenuItem("Načíst");
        mniSave = new JMenuItem("Uložit");
        mniSaveAs = new JMenuItem("Uložit jako...");
        mniPrint = new JMenuItem("Tisk");
        mniQuit = new JMenuItem("Ukončit");
        souborMenu.add(mniLoad);
        souborMenu.add(mniSave);
        souborMenu.add(mniSaveAs);
        souborMenu.add(mniPrint);
        souborMenu.add(new JSeparator());
        souborMenu.add(mniQuit);
        jMenuBar.add(souborMenu);

        return jMenuBar;
    }

    private void updateEmployeesMenu(JMenu menu, List<WorkAttendancePanel> workAttendancePanels) {
        menu.removeAll();
        workAttendancePanels.forEach(workAttendancePanel -> {
            workAttendancePanel.addPropertyChangeListener("dataChange", evt -> isSaved = false);

            final String fullName = workAttendancePanel.getShiftRecord().getEmployee().getFullName();

            final JMenuItem mniEmployee = new JMenuItem(fullName);
            mniEmployee.addActionListener(e -> {
                setTitleName(fullName);
                this.currentPanel = workAttendancePanel;
                this.contentPane.setViewportView(workAttendancePanel);
            });

            menu.add(mniEmployee);
        });
    }

    private void showErrorMessage(String text) {
        JOptionPane.showMessageDialog(this, text, "Chyba", JOptionPane.ERROR_MESSAGE);
    }

    private void showDoneMessage(String text) {
        JOptionPane.showMessageDialog(
                this,
                text,
                "Úloha úspěšně dokončena",
                JOptionPane.PLAIN_MESSAGE
        );
    }

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            final Dao<XSSFWorkbook> io = new XlsxDao();
//
//            XSSFWorkbook workbook = null;
//            try {
//                workbook = io.load(Path.of("test_4_straznici.xlsx"));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            ShiftPlan plan = new ShiftPlan(workbook, MULTISHIFT_CONTINUOUS);
//            new WorkAttendanceWindow(plan, 1).setVisible(true);
//        });
//    }

}
