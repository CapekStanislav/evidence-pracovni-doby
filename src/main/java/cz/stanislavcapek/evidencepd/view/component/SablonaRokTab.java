/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.stanislavcapek.evidencepd.view.component;

import cz.stanislavcapek.evidencepd.plansmen.XlsxDao;
import cz.stanislavcapek.evidencepd.zamestnanec.ZamestnanciListModel;
import cz.stanislavcapek.evidencepd.zamestnanec.Zamestnanec;
import cz.stanislavcapek.evidencepd.plansmen.VytvoreniSablonyXlsx;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * GUI Generování šablony pro zadaný ROK. Výstupní formát je excelový soubor xlsx.
 *
 * @author Stanislav Čapek
 */
public class SablonaRokTab extends JPanel {
    private final JButton btnGeneruj;
    private final JTextField txtRok;
    private final ZamestnanciListModel zamestnanciListModel;
    private final JViewport viewport;
    private List<Zamestnanec> vybraniZamestnanci;

    /**
     * Konstruktor bez parametru.
     */
    public SablonaRokTab() {
        super(true);
        this.zamestnanciListModel = ZamestnanciListModel.getInstance();
        vybraniZamestnanci = new ArrayList<>();

        // JPanel padding //
        this.setBorder(new EmptyBorder(10, 5, 20, 5));
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        JLabel lbl;
        Border borderLeftAndRight = BorderFactory.createEmptyBorder(0, 75, 0, 75);
        JPanel wrapPanel = new JPanel();
        wrapPanel.setLayout(new BoxLayout(wrapPanel, BoxLayout.LINE_AXIS));
        wrapPanel.setBorder(borderLeftAndRight);

        // první panel //
        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder("Nastavení roku"));

        // první řádek //
        lbl = new JLabel("Rok:");
        panel.add(lbl);

        txtRok = new JTextField(10);
        txtRok.setInputVerifier(new ValidaceRok());
        Set<AWTKeyStroke> keys = txtRok.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
        Set<AWTKeyStroke> newKeys = new HashSet<>(keys);
        newKeys.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ENTER, 0));
        txtRok.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, newKeys);
        panel.add(txtRok);

        wrapPanel.add(panel);
        this.add(wrapPanel);


        // druhý panel //
        wrapPanel = new JPanel();
        wrapPanel.setLayout(new BoxLayout(wrapPanel, BoxLayout.LINE_AXIS));
        wrapPanel.setBorder(borderLeftAndRight);

        JScrollPane sp = new JScrollPane();
        sp.setViewportView(vytvorZaskrtavaciSeznam());
        viewport = sp.getViewport();
        sp.setWheelScrollingEnabled(true);
        sp.setBorder(new TitledBorder("Výběr strážníků"));
        wrapPanel.add(sp);
        this.add(wrapPanel);

        // poslední řádek
        wrapPanel = new JPanel();
        wrapPanel.setLayout(new BoxLayout(wrapPanel, BoxLayout.LINE_AXIS));

        panel = new JPanel();

        btnGeneruj = new JButton("Generuj šablonu");
        btnGeneruj.addActionListener(e -> vytvorSablonu());
        btnGeneruj.setEnabled(false);
        panel.add(btnGeneruj);

        wrapPanel.add(panel);
        this.add(wrapPanel);

        this.add(Box.createVerticalGlue());

        zamestnanciListModel.addListDataListener(new ListDataListener() {
            @Override
            public void intervalAdded(ListDataEvent e) {
                sp.setViewportView(vytvorZaskrtavaciSeznam());
            }

            @Override
            public void intervalRemoved(ListDataEvent e) {
                sp.setViewportView(vytvorZaskrtavaciSeznam());
            }

            @Override
            public void contentsChanged(ListDataEvent e) {
                sp.setViewportView(vytvorZaskrtavaciSeznam());
            }
        });

    }

    /**
     * Podpůrná metoda, která synchronizuje {@link ZamestnanciListModel} s vybranými (zaškrtnutými) zaměstnanci.
     */
    private void aktualizujSeznamVybranychStrazniku() {
        vybraniZamestnanci.clear();
        Component[] comp = ((JPanel) viewport.getView()).getComponents();
        for (int i = 0; i < comp.length; i++) {
            JCheckBox box = (JCheckBox) comp[i];
            if (box.isSelected()) {
                vybraniZamestnanci.add(zamestnanciListModel.getElementAt(i));
            }
        }
    }

    /**
     * Vytvoří panel s aktualizovaný seznamem strážníků. Reaguje na základě {@link ListDataListener}.
     *
     * @return CheckBox seznam
     */
    private JPanel vytvorZaskrtavaciSeznam() {
        JPanel panel = new JPanel(new GridBagLayout(), true);
        GridBagConstraints dpg = new GridBagConstraints();

        // obecné nastavení
        dpg.insets = new Insets(5, 5, 5, 5);
        dpg.gridx = 0;
        dpg.gridy = 0;
        dpg.anchor = GridBagConstraints.LINE_START;

        // první řádek //
        JCheckBox box;
        for (int i = 0; i < zamestnanciListModel.getSize(); i++) {
            Zamestnanec zamestnanec = zamestnanciListModel.getElementAt(i);
            box = new JCheckBox(zamestnanec.getId() + " " + zamestnanec.getCeleJmeno());
            box.setSelected(true);
            panel.add(box, dpg);
            dpg.gridy++;
        }
        return panel;
    }

    /**
     * Ukáže chybovou hlášku o špatně vyplněném poli ROK.
     */
    private void ukazNeplatnyRokDialog() {
        JOptionPane.showMessageDialog(btnGeneruj, "Není buď vyplněn ROK, nebo není správně zadaný",
                "Nevyplněný ROK", JOptionPane.INFORMATION_MESSAGE);

    }

    /**
     * Metoda, která vytvoří šablonu a poté ji uloží.
     */
    private void vytvorSablonu() {

        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Excel (*.xlsx)", "xlsx"));
        Path homeDir = Paths.get(System.getProperty("user.dir"));
        chooser.setCurrentDirectory(homeDir.toFile());

        boolean hotovo = false;
        while (!hotovo) {
            int rokInt;
            if (!txtRok.getInputVerifier().verify(txtRok)) {
                ukazNeplatnyRokDialog();
                break;
            } else {
                rokInt = Integer.parseInt(txtRok.getText());
            }

            Path defFileName = Paths.get("Plán služeb " + rokInt);
            chooser.setSelectedFile(defFileName.toFile());

            int volba = chooser.showSaveDialog(null);

            if (volba == JFileChooser.APPROVE_OPTION) {
                Path zadanaCesta = Paths.get(chooser.getSelectedFile().getPath());
                Path kompletniCesta;

                if (!zadanaCesta.getFileName().toString().contains(".xlsx")) {
                    if (zadanaCesta.getFileName().toString().equals("")) {
                        zadanaCesta = defFileName;
                    }
                    String temp = zadanaCesta.toString() + ".xlsx";
                    kompletniCesta = Paths.get(temp);
                } else {
                    kompletniCesta = zadanaCesta;
                }
                // aktualizuje seznam vybraných zaměstnanců dle zaškrtnutí
                aktualizujSeznamVybranychStrazniku();

                // Vytvoření vedlejšího úkolu/TASK
                VytvorSablonuTask task = new VytvorSablonuTask(kompletniCesta);

                if (Files.exists(kompletniCesta)) {
                    String[] options = {"Ano", "Ne"};
                    int v = JOptionPane.showOptionDialog(
                            null, "Soubor již existuje, chcete ho přepsat?",
                            "Přepsat soubor?",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null, options, options[1]);

                    if (v == JOptionPane.YES_OPTION) {
                        btnGeneruj.setEnabled(false);
                        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                        task.execute();
                        hotovo = true;
                    }
                } else {
                    btnGeneruj.setEnabled(false);
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    task.execute();
                    hotovo = true;
                }
            } else {
                hotovo = true;
            }
        }
    }

    /**
     * Zobrazí výsledek uložení na základě pravdivosti.
     *
     * @param b hodnota dle výsledku
     */
    private void ukazVysledekUlozeni(boolean b) {
        if (b) {
            JOptionPane.showMessageDialog(null, "Šablona úspěšně vytvořena."
                    , "Šablona vytvořena", JOptionPane.PLAIN_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Šablonu se nepodařilo vytvořit."
                    , "Chyba při generování šablony", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Vnitřní třída, která validuje zadaný rok.
     */
    private class ValidaceRok extends InputVerifier {

        @Override
        public boolean verify(JComponent input) {
            try {
                Integer.parseInt(txtRok.getText());
            } catch (NumberFormatException e) {
                return false;
            }
            return true;
        }

        @Override
        public boolean shouldYieldFocus(JComponent source) {

            if (verify(source)) {
                txtRok.setBackground(Color.WHITE);
                btnGeneruj.setEnabled(true);
                btnGeneruj.doClick();
                return true;
            } else {
                txtRok.setBackground(Color.YELLOW);
                btnGeneruj.setEnabled(false);
                return false;
            }
        }
    }

    /**
     * Vnitřní třída, která vytvoří podpůrné vlákno a uloží vygenerovanou šablonu.
     */
    private class VytvorSablonuTask extends SwingWorker<Boolean, Void> {
        private Path cesta;
        private XlsxDao io;
        Boolean v = false;


        VytvorSablonuTask(Path kompletniCesta) {
            this.cesta = kompletniCesta;
            this.io = new XlsxDao();
        }

        @Override
        protected Boolean doInBackground() throws Exception {
            XSSFWorkbook workbook = VytvoreniSablonyXlsx.vytvor(vybraniZamestnanci, Integer.parseInt(txtRok.getText()));
            try {
                io.uloz(cesta, workbook);
                return true;
            } catch (IOException e) {
                return false;
            }
        }

        @Override
        protected void done() {
            super.done();
            btnGeneruj.setEnabled(true);
            setCursor(null);
            ukazVysledekUlozeni(v);
        }

    }
}
