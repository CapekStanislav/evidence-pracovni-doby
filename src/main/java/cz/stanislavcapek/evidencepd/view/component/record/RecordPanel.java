package cz.stanislavcapek.evidencepd.view.component.record;


import cz.stanislavcapek.evidencepd.pdf.RecordDocument;
import cz.stanislavcapek.evidencepd.record.Record;
import cz.stanislavcapek.evidencepd.model.*;
import cz.stanislavcapek.evidencepd.shift.servants.TwelveHoursShiftWorkingTimeCounter;
import cz.stanislavcapek.evidencepd.shift.servants.WorkingTimeCounter;
import cz.stanislavcapek.evidencepd.shift.servants.PremiumPaymentsCounter;
import cz.stanislavcapek.evidencepd.shift.servants.DefaultPremiumPaymentsCounter;
import cz.stanislavcapek.evidencepd.shift.*;
import cz.stanislavcapek.evidencepd.model.WorkingTimeFund;
import cz.stanislavcapek.evidencepd.shift.DefaultShiftFactory;
import cz.stanislavcapek.evidencepd.utils.Rounder;
import cz.stanislavcapek.evidencepd.view.component.utils.ColorerWeekendOvertimeTableCellRenderer;
import cz.stanislavcapek.evidencepd.view.component.utils.ColorerWeekendShiftTableCellRenderer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.print.PrinterJob;
import java.io.File;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.UUID;

/**
 * Instance třídy {@code RecordPanel}
 *
 * @author Stanislav Čapek
 */
class RecordPanel extends JPanel {

    private final Record shiftsRecord;
    private final Record overtimesRecord;
    private final OverTimeTableModelRecord overTimeTableModelRecord;
    private final ShiftTableModelRecord shiftTableModelRecord;
    private final JButton btnAdd;
    private final JButton btnRemove;
    private final JTable tableOvertime;

    private final JLabel lblWorkedOut = new JLabel();
    private final JLabel lblSumWorkedOut = new JLabel();
    private final JLabel lblNotWorkedOut = new JLabel();
    private final JLabel lblNight = new JLabel();
    private final JLabel lblWeekend = new JLabel();
    private final JLabel lblToNextMonth = new JLabel();
    private final JLabel lblHoliday = new JLabel();
    private final JLabel lblWorkHoliday = new JLabel();
    private final JLabel lblWorkedOutOvertime = new JLabel();
    private final JLabel lblNightOvertime = new JLabel();
    private final JLabel lblWeekendOvertime = new JLabel();
    private final JLabel lblHolidayOvertime = new JLabel();
    private final double workingTimeFund;
    private final int year;

    public RecordPanel(Record shiftsRecord, Record overtimesRecord) {
        shiftTableModelRecord = new ShiftTableModelRecord(shiftsRecord);
        overTimeTableModelRecord = new OverTimeTableModelRecord(overtimesRecord);
        this.shiftsRecord = shiftTableModelRecord;
        this.overtimesRecord = overTimeTableModelRecord;


        year = this.shiftsRecord.getYear();
        workingTimeFund = WorkingTimeFund
                .calculateWorkingTimeFund(LocalDate.of(year, shiftsRecord.getMonth().getNumber(), 1));

        final JPanel wrapperMain = new JPanel();
        wrapperMain.setPreferredSize(new Dimension(800, 800));
        wrapperMain.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        wrapperMain.setLayout(new BoxLayout(wrapperMain, BoxLayout.PAGE_AXIS));

//        standard shift section
        final JPanel pnlShiftWrapper = new JPanel();
        pnlShiftWrapper.setLayout(new BoxLayout(pnlShiftWrapper, BoxLayout.PAGE_AXIS));
        pnlShiftWrapper.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("Odpracované směny"),
                        BorderFactory.createEmptyBorder(10, 5, 10, 5)
                )
        );
        pnlShiftWrapper.add(createHeader());

        // table
        JTable tableShift = new JTable(shiftTableModelRecord) {

            // confirmation change shift type
            @Override
            public void setValueAt(Object aValue, int row, int column) {
                final Object origValue = this.getModel().getValueAt(row, column);
                if (!origValue.equals(aValue)) {
                    final Object value = shiftTableModelRecord.getValueAt(row, column);
                    if (value != TypeOfShiftTwelveHours.NONE && column != 3) {
                        super.setValueAt(aValue, row, column);
                        return;
                    }
                    final int v = JOptionPane.showConfirmDialog(null,
                            "Chystáte se změnit typ směny. Provést změnu?",
                            "Změna typu směny",
                            JOptionPane.YES_NO_OPTION);
                    if (v == JOptionPane.YES_OPTION) {
                        super.setValueAt(aValue, row, column);
                    }
                }
            }
        };

        // color differentiation
        final ColorerWeekendShiftTableCellRenderer weekendShiftTableCellRenderer = new ColorerWeekendShiftTableCellRenderer(shiftsRecord);
        tableShift.setDefaultRenderer(Object.class, weekendShiftTableCellRenderer);


        // custom cell editor
        final JComboBox<TypeOfShiftTwelveHours> cmbTypeOfShift = new JComboBox<>(TypeOfShiftTwelveHours.values());
        final DefaultCellEditor cmbCellEditor = new DefaultCellEditor(cmbTypeOfShift);
        tableShift.getColumnModel().getColumn(3).setCellEditor(cmbCellEditor);
        tableShift.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(tableShift);
        pnlShiftWrapper.add(scrollPane);

        pnlShiftWrapper.add(createSummarizationStandard());
        wrapperMain.add(pnlShiftWrapper);

//        overtime section
        final JPanel pnlOvertimeWrapper = new JPanel();
        pnlOvertimeWrapper.setLayout(new BoxLayout(pnlOvertimeWrapper, BoxLayout.PAGE_AXIS));
        pnlOvertimeWrapper.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("Přesčasy"),
                        BorderFactory.createEmptyBorder(10, 5, 10, 5)
                ));
        // tabulka přesčasů
        tableOvertime = new JTable(overTimeTableModelRecord);
        tableOvertime.setDefaultRenderer(Object.class, new ColorerWeekendOvertimeTableCellRenderer(this.year));
        tableOvertime.setFillsViewportHeight(true);
        scrollPane = new JScrollPane(tableOvertime);
        pnlOvertimeWrapper.add(scrollPane);

        // sumarizace
        pnlOvertimeWrapper.add(createSummarizationOvertime());

        // tlačítka
        final JPanel pnlButtonsWrapper = new JPanel();
        btnAdd = new JButton("Přidat");
        btnRemove = new JButton("Odebrat");
        pnlButtonsWrapper.add(btnAdd);
        pnlButtonsWrapper.add(btnRemove);

        pnlOvertimeWrapper.add(pnlButtonsWrapper);
        wrapperMain.add(pnlOvertimeWrapper);
        this.add(wrapperMain);

//        update hours
        updateStandardHours();
        updateOvertimeHours();

        initListeners();

    }

    public Record getShiftRecord() {
        return shiftsRecord;
    }

    public Record getOvertimesRecord() {
        return overtimesRecord;
    }

    /**
     * Vytvoří záhlaví složeného ze jména, roku, měsíce a fondu pracovní doby na daný měsíc
     *
     * @return záhlaví
     */
    private JPanel createHeader() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 4, 10, 5));

        JLabel lbl = new JLabel("Jméno: " + shiftsRecord.getEmployee().getFullName());
        panel.add(lbl);

        lbl = new JLabel("Rok: " + year);
        panel.add(lbl);
        final Month month = shiftsRecord.getMonth();
        lbl = new JLabel("Měsíc: " + month.getName());
        panel.add(lbl);
        lbl = new JLabel("Fond prac. doby: " +
                workingTimeFund
        );
        panel.add(lbl);

        return panel;
    }

    /**
     * Vytvoří sumarizaci složenou z odpracovaných hodin, počet nočních hodin,
     * počet víkendových hodin, fondu pracovní doby, převodu hodin z minulého
     * měsíce a převodu hodin do dalšího měsíce.
     *
     * @return sumarizace pro standardni směny
     */
    private JPanel createSummarizationStandard() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Sumarizace"),
                BorderFactory.createEmptyBorder(15, 10, 15, 10)
        ));

        //        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.setLayout(new GridBagLayout());
        final GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipadx += 10;
        c.ipady += 5;


        //        New panel line 1
        c.gridx = GridBagConstraints.RELATIVE;
        c.gridy = 0;


        JLabel lbl = new JLabel("Odpracováno: ");
        panel.add(lbl, c);

        panel.add(lblWorkedOut, c);

        lbl = new JLabel("Dovolená: ");
        panel.add(lbl, c);

        panel.add(lblWorkHoliday, c);

        lbl = new JLabel("Neodpracované hodiny: ");
        panel.add(lbl, c);

        panel.add(lblNotWorkedOut, c);

        lbl = new JLabel("Celkem odpracováno: ");
        panel.add(lbl, c);

        panel.add(lblSumWorkedOut, c);


        //        New panel line 2
        c.gridy = 1;
        c.gridx = GridBagConstraints.RELATIVE;
        lbl = new JLabel("Noční hodiny: ");
        panel.add(lbl, c);

        panel.add(lblNight, c);

        lbl = new JLabel("Víkendové hodiny: ");
        panel.add(lbl, c);

        panel.add(lblWeekend, c);

        lbl = new JLabel("Svátek hodiny: ");
        panel.add(lbl, c);

        panel.add(lblHoliday, c);

        //        New panel line 3
        c.gridy = 2;
        c.gridx = GridBagConstraints.RELATIVE;
        lbl = new JLabel("Fond: ");
        panel.add(lbl, c);

        JLabel lblFond = new JLabel(Double.toString(workingTimeFund));
        panel.add(lblFond, c);

        lbl = new JLabel("Převod z min. měsíce:");
        panel.add(lbl, c);

        JLabel lblPrevodMin = new JLabel(Double.toString(shiftsRecord.getLastMonth()));
        panel.add(lblPrevodMin, c);

        lbl = new JLabel("Převod do dal. měsíce: ");
        panel.add(lbl, c);

        panel.add(lblToNextMonth, c);

        return panel;
    }

    /**
     * Vytvoří sumarizaci pro přesčasy
     *
     * @return sumarizace pro přesčasy
     */
    private JPanel createSummarizationOvertime() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Sumarizace"),
                BorderFactory.createEmptyBorder(15, 10, 15, 10)
        ));

        panel.setLayout(new GridBagLayout());
        final GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipadx += 10;
        c.ipady += 5;

        // new Panel line 1
        c.gridx = GridBagConstraints.RELATIVE;
        c.gridy = 0;

        JLabel lbl = new JLabel("Odpracováno: ");
        panel.add(lbl, c);
        panel.add(lblWorkedOutOvertime, c);

        lbl = new JLabel("Noční hodiny: ");
        panel.add(lbl, c);
        panel.add(lblNightOvertime, c);

        lbl = new JLabel("Víkend hodiny: ");
        panel.add(lbl, c);
        panel.add(lblWeekendOvertime, c);

        lbl = new JLabel("Svátek hodiny: ");
        panel.add(lbl, c);
        panel.add(lblHolidayOvertime, c
        );

        return panel;
    }

    private void initListeners() {
        // update view
        shiftTableModelRecord.addTableModelListener(e -> updateStandardHours());
        overTimeTableModelRecord.addTableModelListener(e -> updateOvertimeHours());

        // notify data change
        shiftTableModelRecord
                .addTableModelListener(e -> firePropertyChange("dataChange", false, true));
        overTimeTableModelRecord
                .addTableModelListener(e -> firePropertyChange("dataChange", false, true));


        btnAdd.addActionListener(e -> addOvertime());

        btnRemove.addActionListener(e -> {
            final int selectedRow = tableOvertime.getSelectedRow();

            if (selectedRow >= 0) {
                overTimeTableModelRecord.removeOvertime(selectedRow);
            }
        });
    }

    boolean saveDocument() throws Exception {
        final JFileChooser chooser = getChooserForDocument();

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            if (!selectedFile.getName().toLowerCase().contains(".pdf")) {
                selectedFile = new File(selectedFile.getPath() + ".pdf");
            }

            try (PDDocument document = getDocument()) {
                document.save(selectedFile);
            }
            return true;
        }

        return false;
    }

    private JFileChooser getChooserForDocument() {
        final JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
        chooser.setDialogType(JFileChooser.SAVE_DIALOG);
        chooser.setFileFilter(new FileNameExtensionFilter("Dokument PDF(*.pdf)", "pdf"));
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        String defaultFileName = String.format(
                "%s_%s_%d",
                shiftsRecord.getEmployee().getFullName(), shiftsRecord.getMonth().getName(), shiftsRecord.getYear()
        );

        chooser.setSelectedFile(new File(defaultFileName));
        return chooser;
    }

    boolean printDocument() throws Exception {
        PDDocument document = null;
        File tempFile;
        try {
            tempFile = new File(UUID.randomUUID().toString() + ".temp");
            document = getDocument();
            document.save(tempFile);
            document.close();
        } finally {
            if (document != null) {
                document.close();
            }
        }

        final boolean isPrint;
        try (PDDocument docToPrint = PDDocument.load(tempFile)) {
            final PrinterJob printerJob = PrinterJob.getPrinterJob();
            printerJob.setPageable(new PDFPageable(docToPrint));
            isPrint = printerJob.printDialog();
            if (isPrint) {
                printerJob.print();
            }
        } finally {
            tempFile.delete();
        }
        return isPrint;
    }

    private PDDocument getDocument() throws Exception {
        final RecordDocument shiftsRecordDocument = new RecordDocument(
                shiftsRecord,
                shiftTableModelRecord
        );
        final RecordDocument overTimeRecordDocument = new RecordDocument(
                shiftsRecord,
                overTimeTableModelRecord
        );
        final DocumentCreatingTask task = new DocumentCreatingTask(
                shiftsRecordDocument, overTimeRecordDocument);
        return task.doInBackground();
    }

    private void addOvertime() {
        final LocalDate period = this.shiftsRecord.getShifts().get(1).getStart().toLocalDate();
        final int lengthOfMonth = period.getMonth().length(period.isLeapYear());
        final String message = String.format(
                "Zadejte den přesčasu. Den musí být v rozmezí %d - %d",
                1,
                lengthOfMonth
        );
        final String dayValue = JOptionPane.showInputDialog(
                this,
                message,
                "Den přesčasu",
                JOptionPane.PLAIN_MESSAGE
        );

        try {
            final int den = Integer.parseInt(dayValue);
            final LocalDate date = period.withDayOfMonth(den);
            final Shift shift = new DefaultShiftFactory().createShift(date);
            overTimeTableModelRecord.addOvertime(shift);
        } catch (NumberFormatException | DateTimeException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Chybně zadaný den!"
            );
        }
    }

    /**
     * Aktualizuje odpracované, noční a víkendové hodiny v závislosti
     * na změnách v tabulce
     */
    private void updateStandardHours() {
        double hWorkedOut = 0;
        double hNotWorkedOut = 0;
        double hWorkHoliday = 0;
        double hNight = 0;
        double hWeekend = 0;
        double hHoliday = 0;
        double hNextMonth = 0;

        // servants
        final WorkingTimeCounter workingTimeCounter = new TwelveHoursShiftWorkingTimeCounter();
        final PremiumPaymentsCounter premiumPaymentsCounter = new DefaultPremiumPaymentsCounter();

        final Collection<Shift> shifts = shiftsRecord.getShifts().values();
        for (Shift shift : shifts) {
            // counting with working time
            final WorkingTime workingTime = workingTimeCounter.calulate(shift);
            hWorkedOut += workingTime.getWorkedOut();
            hNotWorkedOut += workingTime.getNotWorkedOut();
            hWorkHoliday += workingTime.getHoliday();

            // counting premium payments
            final PremiumPayments premiumPayments = premiumPaymentsCounter.calculate(shift);
            hNight += premiumPayments.getNight();
            hWeekend += premiumPayments.getWeekend();
            hHoliday += premiumPayments.getHoliday();
        }

        // intermediate calculation
        final double fromLastMonth = shiftsRecord.getLastMonth();
        double hSumWorkedOut = hWorkedOut + hWorkHoliday + hNotWorkedOut;
        hNextMonth = hSumWorkedOut - workingTimeFund + fromLastMonth;

        // rounding
        hSumWorkedOut = round(hSumWorkedOut);
        hWorkedOut = round(hWorkedOut);
        hWorkHoliday = round(hWorkHoliday);
        hNotWorkedOut = round(hNotWorkedOut);
        hNight = round(hNight);
        hWeekend = round(hWeekend);
        hHoliday = round(hHoliday);
        hNextMonth = round(hNextMonth);

        // setting labels
        lblSumWorkedOut.setText(Double.toString(hSumWorkedOut));
        lblWorkedOut.setText(Double.toString(hWorkedOut));
        lblWorkHoliday.setText(Double.toString(hWorkHoliday));
        lblNotWorkedOut.setText(Double.toString(hNotWorkedOut));
        lblNight.setText(Double.toString(hNight));
        lblWeekend.setText(Double.toString(hWeekend));
        lblHoliday.setText(Double.toString(hHoliday));
        lblToNextMonth.setText(Double.toString(hNextMonth));
    }

    private void updateOvertimeHours() {

        final WorkingTimeCounter workingTimeCounter = new TwelveHoursShiftWorkingTimeCounter();
        final PremiumPaymentsCounter premiumPaymentsCounter = new DefaultPremiumPaymentsCounter();

        double hNight = 0;
        double hWeekend = 0;
        double hHoliday = 0;
        double hWorkedOut = 0;

        for (Shift overtime : overtimesRecord.getShifts().values()) {
            overtime.setWorkingHours(workingTimeCounter.calulate(overtime));
            overtime.setPremiumPayments(premiumPaymentsCounter.calculate(overtime));

            final PremiumPayments premiumPayments = overtime.getPremiumPayments();
            hWorkedOut += overtime.getWorkingHours().getWorkedOut();
            hNight += premiumPayments.getNight();
            hWeekend += premiumPayments.getWeekend();
            hHoliday += premiumPayments.getHoliday();
        }

        lblWorkedOutOvertime.setText(String.valueOf(round(hWorkedOut)));
        lblNightOvertime.setText(String.valueOf(round(hNight)));
        lblWeekendOvertime.setText(String.valueOf(round(hWeekend)));
        lblHolidayOvertime.setText(String.valueOf(round(hHoliday)));

    }

    private double round(double value) {
        final int places = 2;
        final Rounder rounder = new Rounder() {
        };
        return rounder.getRoundedDouble(value, places);
    }


}
