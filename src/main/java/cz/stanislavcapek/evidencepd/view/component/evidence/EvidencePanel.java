package cz.stanislavcapek.evidencepd.view.component.evidence;


import cz.stanislavcapek.evidencepd.evidence.Evidence;
import cz.stanislavcapek.evidencepd.model.*;
import cz.stanislavcapek.evidencepd.pdf.EvidenceDocument;
import cz.stanislavcapek.evidencepd.smeny.sluzebnici.DvanactiHodSmenyPocitadloPracovniDoby;
import cz.stanislavcapek.evidencepd.smeny.sluzebnici.PocitadloPracovniDoby;
import cz.stanislavcapek.evidencepd.smeny.sluzebnici.PocitadloPriplatku;
import cz.stanislavcapek.evidencepd.smeny.sluzebnici.ZakladniPocitatdloPriplatku;
import cz.stanislavcapek.evidencepd.smeny.*;
import cz.stanislavcapek.evidencepd.model.FondPracovniDoby;
import cz.stanislavcapek.evidencepd.smeny.ZakladniTovarnaNaSmeny;
import cz.stanislavcapek.evidencepd.utils.Zaokrouhlovac;
import cz.stanislavcapek.evidencepd.view.component.util.ObarvovacVikenduPrescasTableCellRenderer;
import cz.stanislavcapek.evidencepd.view.component.util.ObarvovacVikenduSmenyTableCellRenderer;
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
 * Instance třídy {@code EvidencePanel}
 *
 * @author Stanislav Čapek
 */
class EvidencePanel extends JPanel {

    private final Evidence evidence;
    private final Evidence prescasy;
    private final PrescasyTableModelEvidence prescasyTableModelEvidence;
    private final SmenyTableModelEvidence smenyTableModelEvidence;
    private final JButton pridatBtn;
    private final JButton odebratBtn;
    private final JTable prescasyTable;

    private final JLabel lblOdpracovano = new JLabel();
    private final JLabel lblCelkemOdpracovano = new JLabel();
    private final JLabel lblNeodpracovano = new JLabel();
    private final JLabel lblNocni = new JLabel();
    private final JLabel lblVikend = new JLabel();
    private final JLabel lblPrevodDal = new JLabel();
    private final JLabel lblSvatek = new JLabel();
    private final JLabel lblDovolena = new JLabel();
    private final JLabel lblOdprPrescas = new JLabel();
    private final JLabel lblNocniPrescas = new JLabel();
    private final JLabel lblVikendPrescas = new JLabel();
    private final JLabel lblSvatekPrescas = new JLabel();
    private final double fondPracovniDoby;
    private final int rok;

    public EvidencePanel(Evidence evidence, Evidence prescasy) {
        smenyTableModelEvidence = new SmenyTableModelEvidence(evidence);
        prescasyTableModelEvidence = new PrescasyTableModelEvidence(prescasy);
        this.evidence = smenyTableModelEvidence;
        this.prescasy = prescasyTableModelEvidence;


        rok = this.evidence.getRok();
        fondPracovniDoby = FondPracovniDoby
                .vypoctiFondPracovniDoby(LocalDate.of(rok, evidence.getMesic().getCislo(), 1));

        final JPanel wrapperMain = new JPanel();
        wrapperMain.setPreferredSize(new Dimension(800, 800));
        wrapperMain.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        wrapperMain.setLayout(new BoxLayout(wrapperMain, BoxLayout.PAGE_AXIS));

//        sekce standardních směn
        final JPanel wrapperSmeny = new JPanel();
        wrapperSmeny.setLayout(new BoxLayout(wrapperSmeny, BoxLayout.PAGE_AXIS));
        wrapperSmeny.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("Odpracované směny"),
                        BorderFactory.createEmptyBorder(10, 5, 10, 5)
                )
        );
        wrapperSmeny.add(vytvorZahlavi());

        // tabulka
        JTable smenyTable = new JTable(smenyTableModelEvidence) {

            // potvrzení změny typu směny
            @Override
            public void setValueAt(Object aValue, int row, int column) {
                final Object origValue = this.getModel().getValueAt(row, column);
                if (!origValue.equals(aValue)) {
                    final Object value = smenyTableModelEvidence.getValueAt(row, column);
                    if (value != TypSmeny.ZADNA && column != 3) {
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

        // barevné odlišení víkendů
        final ObarvovacVikenduSmenyTableCellRenderer obarvovacRenderer = new ObarvovacVikenduSmenyTableCellRenderer(evidence);
        smenyTable.setDefaultRenderer(Object.class, obarvovacRenderer);


        // vlastní editor tabulky
        final JComboBox<TypSmeny> typSmenyJComboBox = new JComboBox<>(TypSmeny.values());
        final DefaultCellEditor cmbCellEditor = new DefaultCellEditor(typSmenyJComboBox);
        smenyTable.getColumnModel().getColumn(3).setCellEditor(cmbCellEditor);
        smenyTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(smenyTable);
        wrapperSmeny.add(scrollPane);

        wrapperSmeny.add(vytvorSumarizaciStandardni());
        wrapperMain.add(wrapperSmeny);

//        sekce přesčasy
        final JPanel wrapperPrescasy = new JPanel();
        wrapperPrescasy.setLayout(new BoxLayout(wrapperPrescasy, BoxLayout.PAGE_AXIS));
        wrapperPrescasy.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("Přesčasy"),
                        BorderFactory.createEmptyBorder(10, 5, 10, 5)
                ));
        // tabulka přesčasů
        prescasyTable = new JTable(prescasyTableModelEvidence);
        prescasyTable.setDefaultRenderer(Object.class, new ObarvovacVikenduPrescasTableCellRenderer(this.rok));
        prescasyTable.setFillsViewportHeight(true);
        scrollPane = new JScrollPane(prescasyTable);
        wrapperPrescasy.add(scrollPane);

        // sumarizace
        wrapperPrescasy.add(vytvorSumarizaciPrescasy());

        // tlačítka
        final JPanel wrapperButtnos = new JPanel();
        pridatBtn = new JButton("Přidat");
        odebratBtn = new JButton("Odebrat");
        wrapperButtnos.add(pridatBtn);
        wrapperButtnos.add(odebratBtn);

        wrapperPrescasy.add(wrapperButtnos);
        wrapperMain.add(wrapperPrescasy);
        this.add(wrapperMain);

//        aktualizace hodin a listeneru
        aktualizujHodinyStandardni();
        aktualizujHodinyPrescasy();
        initListeners();

    }

    public Evidence getSmeny() {
        return evidence;
    }

    public Evidence getPrescasy() {
        return prescasy;
    }

    /**
     * Vytvoří záhlaví složeného ze jména, roku, měsíce a fondu pracovní doby na daný měsíc
     *
     * @return záhlaví
     */
    private JPanel vytvorZahlavi() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 4, 10, 5));

        JLabel lbl = new JLabel("Jméno: " + evidence.getZamestnanec().getCeleJmeno());
        panel.add(lbl);

        lbl = new JLabel("Rok: " + rok);
        panel.add(lbl);
        final Mesic mesic = evidence.getMesic();
        lbl = new JLabel("Měsíc: " + mesic.getNazev());
        panel.add(lbl);
        lbl = new JLabel("Fond prac. doby: " +
                fondPracovniDoby
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
    private JPanel vytvorSumarizaciStandardni() {
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

        panel.add(lblOdpracovano, c);

        lbl = new JLabel("Dovolená: ");
        panel.add(lbl, c);

        panel.add(lblDovolena, c);

        lbl = new JLabel("Neodpracované hodiny: ");
        panel.add(lbl, c);

        panel.add(lblNeodpracovano, c);

        lbl = new JLabel("Celkem odpracováno: ");
        panel.add(lbl, c);

        panel.add(lblCelkemOdpracovano, c);


        //        New panel line 2
        c.gridy = 1;
        c.gridx = GridBagConstraints.RELATIVE;
        lbl = new JLabel("Noční hodiny: ");
        panel.add(lbl, c);

        panel.add(lblNocni, c);

        lbl = new JLabel("Víkendové hodiny: ");
        panel.add(lbl, c);

        panel.add(lblVikend, c);

        lbl = new JLabel("Svátek hodiny: ");
        panel.add(lbl, c);

        panel.add(lblSvatek, c);

        //        New panel line 3
        c.gridy = 2;
        c.gridx = GridBagConstraints.RELATIVE;
        lbl = new JLabel("Fond: ");
        panel.add(lbl, c);

        JLabel lblFond = new JLabel(Double.toString(fondPracovniDoby));
        panel.add(lblFond, c);

        lbl = new JLabel("Převod z min. měsíce:");
        panel.add(lbl, c);

        JLabel lblPrevodMin = new JLabel(Double.toString(evidence.getPredchoziMesic()));
        panel.add(lblPrevodMin, c);

        lbl = new JLabel("Převod do dal. měsíce: ");
        panel.add(lbl, c);

        panel.add(lblPrevodDal, c);

        return panel;
    }

    /**
     * Vytvoří sumarizaci pro přesčasy
     *
     * @return sumarizace pro přesčasy
     */
    private JPanel vytvorSumarizaciPrescasy() {
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
        panel.add(lblOdprPrescas, c);

        lbl = new JLabel("Noční hodiny: ");
        panel.add(lbl, c);
        panel.add(lblNocniPrescas, c);

        lbl = new JLabel("Víkend hodiny: ");
        panel.add(lbl, c);
        panel.add(lblVikendPrescas, c);

        lbl = new JLabel("Svátek hodiny: ");
        panel.add(lbl, c);
        panel.add(lblSvatekPrescas, c
        );

        return panel;
    }

    private void initListeners() {
        // aktualizace view
        smenyTableModelEvidence.addTableModelListener(e -> aktualizujHodinyStandardni());
        prescasyTableModelEvidence.addTableModelListener(e -> aktualizujHodinyPrescasy());

        // propagace změny dat v modelu
        smenyTableModelEvidence
                .addTableModelListener(e -> firePropertyChange("dataChange", false, true));
        prescasyTableModelEvidence
                .addTableModelListener(e -> firePropertyChange("dataChange", false, true));


        pridatBtn.addActionListener(e -> pridejPrescas());

        odebratBtn.addActionListener(e -> {
            final int selectedRow = prescasyTable.getSelectedRow();

            if (selectedRow >= 0) {
                prescasyTableModelEvidence.removePrescas(selectedRow);
            }
        });
    }

    // TODO: 01.03.2020 návratová hodnota false, když dojde ke zrušení dialogu
    boolean saveDocument() throws Exception {
        final JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
        chooser.setDialogType(JFileChooser.SAVE_DIALOG);
        chooser.setFileFilter(new FileNameExtensionFilter("Dokument PDF(*.pdf)", "pdf"));
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        String defaultFileName = String.format(
                "%s_%s_%d",
                evidence.getZamestnanec().getCeleJmeno(), evidence.getMesic().getNazev(), evidence.getRok()
        );

        chooser.setSelectedFile(new File(defaultFileName));
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

    boolean printDocument() throws Exception {
        PDDocument document = null;
        File file;
        try {
            file = new File(UUID.randomUUID().toString() + ".temp");
            document = getDocument();
            document.save(file);
            document.close();
        } finally {
            if (document != null) {
                document.close();
            }
        }

        final boolean isPrint;
        try (PDDocument docToPrint = PDDocument.load(file)) {
            final PrinterJob printerJob = PrinterJob.getPrinterJob();
            printerJob.setPageable(new PDFPageable(docToPrint));
            isPrint = printerJob.printDialog();
            if (isPrint) {
                printerJob.print();
            }
        } finally {
            file.delete();
        }
        return isPrint;
    }

    private PDDocument getDocument() throws Exception {
        final EvidenceDocument smenyModel = new EvidenceDocument(
                evidence,
                smenyTableModelEvidence
        );
        final EvidenceDocument prescasyModel = new EvidenceDocument(
                evidence,
                prescasyTableModelEvidence
        );
        final DocumentCreatingTask task = new DocumentCreatingTask(
                smenyModel, prescasyModel);
        return task.doInBackground();
    }

    private void pridejPrescas() {
        final LocalDate obdobi = this.evidence.getSmeny().get(1).getZacatek().toLocalDate();
        final int lenghtOfMonth = obdobi.getMonth().length(obdobi.isLeapYear());
        final String zprava = String.format(
                "Zadejte den přesčasu. Den musí být v rozmezí %d - %d",
                1,
                lenghtOfMonth
        );
        final String hodnota = JOptionPane.showInputDialog(
                this,
                zprava,
                "Den přesčasu",
                JOptionPane.PLAIN_MESSAGE
        );

        try {
            final int den = Integer.parseInt(hodnota);
            final LocalDate date = obdobi.withDayOfMonth(den);
            final Smena smena = new ZakladniTovarnaNaSmeny().vytvorSmenu(date);
            prescasyTableModelEvidence.addPrescas(smena);
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
    private void aktualizujHodinyStandardni() {
        double hOdpracovano = 0;
        double hNeodpracovano = 0;
        double hDovolena = 0;
        double hNocni = 0;
        double hVikend = 0;
        double hSvatek = 0;
        double hPrevodDalsi = 0;

        // služebníci
        final PocitadloPracovniDoby pocitadloPracovniDoby = new DvanactiHodSmenyPocitadloPracovniDoby();
        final PocitadloPriplatku pocitatdloPriplatku = new ZakladniPocitatdloPriplatku();

        final Collection<Smena> smeny = evidence.getSmeny().values();
        for (Smena smena : smeny) {
            // počítání s pracovní dobou
            final PracovniDoba pracovniDoba = pocitadloPracovniDoby.vypoctiPracovniDobu(smena);
            hOdpracovano += pracovniDoba.getOdpracovano();
            hNeodpracovano += pracovniDoba.getNeodpracovano();
            hDovolena += pracovniDoba.getDovolena();

            // počítání příplatků
            final Priplatky priplatky = pocitatdloPriplatku.vypoctiPriplatky(smena);
            hNocni += priplatky.getNocni();
            hVikend += priplatky.getVikend();
            hSvatek += priplatky.getSvatek();
        }

        // mezivýpočty
//        hCelkemOdpracovano = smeny.getOdpracovaneHodiny();
        final double prevodZMinMesice = evidence.getPredchoziMesic();
        double hCelkemOdpracovano = hOdpracovano + hDovolena + hNeodpracovano;
        hPrevodDalsi = hCelkemOdpracovano - fondPracovniDoby + prevodZMinMesice;

        // zaokrouhlení
        final int places = 2;
        final Zaokrouhlovac zaok = new Zaokrouhlovac() {
        };
        hCelkemOdpracovano = zaok.getRoundedDouble(hCelkemOdpracovano, places);
        hOdpracovano = zaok.getRoundedDouble(hOdpracovano, places);
        hDovolena = zaok.getRoundedDouble(hDovolena, places);
        hNeodpracovano = zaok.getRoundedDouble(hNeodpracovano, places);
        hNocni = zaok.getRoundedDouble(hNocni, places);
        hVikend = zaok.getRoundedDouble(hVikend, places);
        hSvatek = zaok.getRoundedDouble(hSvatek, places);
        hPrevodDalsi = zaok.getRoundedDouble(hPrevodDalsi, places);

        // dosazení čísel
        lblCelkemOdpracovano.setText(Double.toString(hCelkemOdpracovano));
        lblOdpracovano.setText(Double.toString(hOdpracovano));
        lblDovolena.setText(Double.toString(hDovolena));
        lblNeodpracovano.setText(Double.toString(hNeodpracovano));
        lblNocni.setText(Double.toString(hNocni));
        lblVikend.setText(Double.toString(hVikend));
        lblSvatek.setText(Double.toString(hSvatek));
        lblPrevodDal.setText(Double.toString(hPrevodDalsi));
    }

    private void aktualizujHodinyPrescasy() {

        final PocitadloPriplatku pocitatdloPriplatku = new ZakladniPocitatdloPriplatku();
        final PocitadloPracovniDoby pocitadloPracovniDoby = new DvanactiHodSmenyPocitadloPracovniDoby();

        double hNocni = 0;
        double hVikend = 0;
        double hSvatek = 0;
        double hOdpracPrescas = 0;

        for (Smena prescas : prescasy.getSmeny().values()) {
            prescas.setPracovniDoba(pocitadloPracovniDoby.vypoctiPracovniDobu(prescas));
            prescas.setPriplatky(pocitatdloPriplatku.vypoctiPriplatky(prescas));

            final Priplatky priplatky = prescas.getPriplatky();
            hOdpracPrescas += prescas.getPracovniDoba().getOdpracovano();
            hNocni += priplatky.getNocni();
            hVikend += priplatky.getVikend();
            hSvatek += priplatky.getSvatek();
        }

        final Zaokrouhlovac zaok = new Zaokrouhlovac() {
        };
        lblOdprPrescas.setText(String.valueOf(zaok.getRoundedDouble(hOdpracPrescas, 2)));
        lblNocniPrescas.setText(String.valueOf(zaok.getRoundedDouble(hNocni, 2)));
        lblVikendPrescas.setText(String.valueOf(zaok.getRoundedDouble(hVikend, 2)));
        lblSvatekPrescas.setText(String.valueOf(zaok.getRoundedDouble(hSvatek, 2)));

    }


}
