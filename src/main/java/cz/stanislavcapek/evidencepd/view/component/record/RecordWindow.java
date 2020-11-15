package cz.stanislavcapek.evidencepd.view.component.record;

import cz.stanislavcapek.evidencepd.appconfig.ConfigPaths;
import cz.stanislavcapek.evidencepd.dao.Dao;
import cz.stanislavcapek.evidencepd.record.Record;
import cz.stanislavcapek.evidencepd.record.RecordDao;
import cz.stanislavcapek.evidencepd.record.DefaultRecord;
import cz.stanislavcapek.evidencepd.shiftplan.ShfitPlan;
import cz.stanislavcapek.evidencepd.shiftplan.XlsxDao;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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

import static cz.stanislavcapek.evidencepd.model.WorkingTimeFund.TypeOfWeeklyWorkingTime.MULTISHIFT_CONTINUOUS;

/**
 * Instance třídy {@code RecordWindow}
 *
 * @author Stanislav Čapek
 */
public class RecordWindow extends JFrame {
    private static final String TITLE = "Evidence pracovní doby";

    private JMenuItem mniSave;
    private JMenuItem mniSaveAs;
    private JMenuItem mniLoad;
    private JMenuItem mniPrint;
    private JMenuItem mniQuit;
    private final List<RecordPanel> pnlEmployeeList = new ArrayList<>();
    private RecordPanel currentPanel;
    private final JScrollPane contentPane = new JScrollPane();
    private final String nameFormat = "%s-%s-%s.%s";
    private final String recordStr = "evidence";
    private final String overtimeStr = "prescasy";
    private final String suffix = "json";
    private final JMenu menuEmployees = new JMenu("Zaměstnanci");
    private boolean isSaved = false;

    public RecordWindow(ShfitPlan shfitPlan, int month) {

        pnlEmployeeList.clear();
        pnlEmployeeList.addAll(
                shfitPlan.getEmployeeIds()
                        .stream()
                        .filter(id -> shfitPlan.isEmployee(id, month))
                        .map(id -> getEvidencePanel(shfitPlan, month, id))
                        .collect(Collectors.toList())
        );

        initClass();
    }

    public RecordWindow(String fileName) {
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

    private void updateViewPort(List<RecordPanel> panels) {
        if (!panels.isEmpty()) {
            final int index = 0;
            setTitleName(panels.get(index).getShiftRecord().getEmployee().getFullName());
            currentPanel = panels.get(index);
        }
        contentPane.setViewportView(currentPanel);
    }

    private RecordPanel getEvidencePanel(ShfitPlan shfitPlan, int month, int id) {
        return new RecordPanel(
                shfitPlan.getRecord(month, id),
                shfitPlan.getRecordOvertime(month, id));
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
                    RecordWindow.this,
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
        Dao<List<Record>> io = new RecordDao();
        final int year = date.getYear();
        final int month = date.getMonthValue();

        final Path shiftsFile = Paths.get(String.format(nameFormat, recordStr, year, month, suffix));
        final Path overtimesFile = Paths.get(String.format(nameFormat, overtimeStr, year, month, suffix));

        List<Record> recordList = null;

        try {
            recordList = io.load(ConfigPaths.RECORDS_PATH.resolve(shiftsFile));
        } catch (IOException e) {
            showErrorMessage(
                    String.format("Nepodařilo se nalézt požadovaný soubor evidence " +
                            "nebo je soubor poškozen: %s/%s", month, year)
            );
            this.dispose();
        }

        List<Record> overtimeList;
        try {
            overtimeList = io.load(ConfigPaths.RECORDS_PATH.resolve(overtimesFile));
        } catch (IOException e) {
            showErrorMessage(
                    String.format("Nepodařilo se nalézt požadovaný soubor přesčasů " +
                            "nebo je soubor poškozen: %s/%s", month, year)

            );
            overtimeList = createEmptyOvertimeList(recordList);
        }

        pnlEmployeeList.clear();
        for (int i = 0; i < recordList.size(); i++) {
            final Record record = recordList.get(i);
            final Record prescasy = overtimeList.get(i);
            pnlEmployeeList.add(
                    new RecordPanel(record, prescasy)
            );
        }
        updateEmployeesMenu(menuEmployees, pnlEmployeeList);
        updateViewPort(pnlEmployeeList);
        isSaved = false;
    }

    private List<Record> createEmptyOvertimeList(List<Record> recordList) {
        List<Record> list = new ArrayList<>();

        for (final Record record : recordList) {
            list.add(
                    new DefaultRecord(
                            record.getEmployee(),
                            record.getMonth(),
                            record.getYear(),
                            record.getTypeOfWeeklyWorkingTime(),
                            record.getLastMonth(),
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
        final Dao<List<Record>> io = new RecordDao();

        final int year = currentPanel.getShiftRecord().getYear();
        final int month = currentPanel.getShiftRecord().getMonth().getNumber();

        final Path shiftsFile = Paths.get(String.format(nameFormat, recordStr, year, month, suffix));
        final Path overtimesFile = Paths.get(String.format(nameFormat, overtimeStr, year, month, suffix));

        final List<Record> recordList = this.pnlEmployeeList.stream()
                .map(RecordPanel::getShiftRecord)
                .collect(Collectors.toList());

        final List<Record> prescasyList = this.pnlEmployeeList.stream()
                .map(RecordPanel::getOvertimesRecord)
                .collect(Collectors.toList());

        try {
            io.save(ConfigPaths.RECORDS_PATH.resolve(shiftsFile), recordList);
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
        showDoneMessage(String.format("Record %s/%s byly úspěšně uloženy", month, year));
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

    private void updateEmployeesMenu(JMenu menu, List<RecordPanel> recordPanels) {
        menu.removeAll();
        recordPanels.forEach(recordPanel -> {
            recordPanel.addPropertyChangeListener("dataChange", evt -> isSaved = false);

            final String fullName = recordPanel.getShiftRecord().getEmployee().getFullName();

            final JMenuItem mniEmployee = new JMenuItem(fullName);
            mniEmployee.addActionListener(e -> {
                setTitleName(fullName);
                this.currentPanel = recordPanel;
                this.contentPane.setViewportView(recordPanel);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            final Dao<XSSFWorkbook> io = new XlsxDao();

            XSSFWorkbook workbook = null;
            try {
                workbook = io.load(Path.of("test_4_straznici.xlsx"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            ShfitPlan plan = new ShfitPlan(workbook, MULTISHIFT_CONTINUOUS);
            new RecordWindow(plan, 1).setVisible(true);
        });
    }

}
