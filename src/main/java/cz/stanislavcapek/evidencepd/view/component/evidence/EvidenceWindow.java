package cz.stanislavcapek.evidencepd.view.component.evidence;

import cz.stanislavcapek.evidencepd.appconfig.ConfigPaths;
import cz.stanislavcapek.evidencepd.dao.Dao;
import cz.stanislavcapek.evidencepd.evidence.EvidenceDao;
import cz.stanislavcapek.evidencepd.evidence.ZakladniEvidence;
import cz.stanislavcapek.evidencepd.plansmen.XlsxDao;
import cz.stanislavcapek.evidencepd.evidence.Evidence;
import cz.stanislavcapek.evidencepd.plansmen.PlanSmen;
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

import static cz.stanislavcapek.evidencepd.model.FondPracovniDoby.DruhTydenniPracDoby.VICESMENNY_NEPRETRZITY;

/**
 * Instance třídy {@code EvidenceWindow}
 *
 * @author Stanislav Čapek
 */
public class EvidenceWindow extends JFrame {
    private static final String EVIDENCE_PRACOVNI_DOBY = "Evidence pracovní doby";

    private JMenuItem ulozitMenuItem;
    private JMenuItem ulozitJakoMenuItem;
    private JMenuItem nacistMenuItem;
    private JMenuItem tiskMenuItem;
    private JMenuItem ukoncitMenuItem;
    private final List<EvidencePanel> zamestnanciPanels = new ArrayList<>();
    private EvidencePanel current;
    private final JScrollPane contentPane = new JScrollPane();
    private final String formatNazvu = "%s-%s-%s.%s";
    private final String evidence = "evidence";
    private final String prescasy = "prescasy";
    private final String pripona = "json";
    private final JMenu zamestnanciMenu = new JMenu("Zaměstnanci");
    private boolean saved = false;

    public EvidenceWindow(PlanSmen planSmen, int mesic) {

        zamestnanciPanels.clear();
        zamestnanciPanels.addAll(
                planSmen.getIdZamestnancu()
                        .stream()
                        .filter(id -> planSmen.isZamestnanec(id, mesic))
                        .map(id -> getEvidencePanel(planSmen, mesic, id))
                        .collect(Collectors.toList())
        );

        initClass();
    }

    public EvidenceWindow(String nazevSouboru) {
        nactiStavZeSouboru(nazevSouboru);
        initClass();
    }

    private void initClass() {
        this.setTitle(EVIDENCE_PRACOVNI_DOBY);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);


        // menu
        final JMenuBar menubar = vytvorMenuBar();
        aktualizujZamestnanceMenu(zamestnanciMenu, zamestnanciPanels);
        menubar.add(zamestnanciMenu);
        this.setJMenuBar(menubar);

        aktualizujViewPort(zamestnanciPanels);

        this.setContentPane(contentPane);
        this.pack();

        initListeners();
    }

    private void aktualizujViewPort(List<EvidencePanel> panels) {
        if (!panels.isEmpty()) {
            setTitleName(panels.get(0).getSmeny().getZamestnanec().getCeleJmeno());
            current = panels.get(0);
        }
        contentPane.setViewportView(current);
    }

    private EvidencePanel getEvidencePanel(PlanSmen planSmen, int mesic, int id) {
        return new EvidencePanel(
                planSmen.getEvidenci(mesic, id),
                planSmen.getEvidenciPrescasu(mesic, id));
    }

    private void setTitleName(String text) {
        this.setTitle(EVIDENCE_PRACOVNI_DOBY + " " + text);
    }

    private void initListeners() {
        nacistMenuItem.addActionListener(e -> nactiStavZeSouboru());
        ulozitMenuItem.addActionListener(e -> ulozStavDoSouboru());
        ulozitJakoMenuItem.addActionListener(e -> ulozJako());
        tiskMenuItem.addActionListener(e -> tiskni());
        ukoncitMenuItem.addActionListener(e -> setVisible(false));

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (!saved) {
                    int volba = JOptionPane.showConfirmDialog(
                            null,
                            "Práce nebyla uložena. Chcete jí nyní uložit?",
                            "Ukončení bez uložení",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                    );
                    if (volba == JOptionPane.YES_OPTION) {
                        ulozStavDoSouboru();
                        e.getWindow().dispose();
                    } else if (volba == JOptionPane.NO_OPTION) {
                        e.getWindow().dispose();
                    }
                } else {
                    e.getWindow().dispose();
                }
            }
        });
    }

    /**
     * @param nazevSouboru název souboru uložené evidence. Při vložení více názvů se bere
     *                     vždy jen první. Vynechání názvů souboru zobrazí nabídku uložených
     *                     evidencí.
     */
    private void nactiStavZeSouboru(String... nazevSouboru) {

        if (nazevSouboru.length > 0) {
            final LocalDate datum = extrahujDatum(nazevSouboru[0]);
            nactiStav(datum);
        } else {
            final HistorieEvidenciPanel nabidka = new HistorieEvidenciPanel().zobrazNabidku();
            if (nabidka.jeVybrano()) {
                final LocalDate datum = nabidka.map(this::extrahujDatum);
                nactiStav(datum);
                showDoneMessage(
                        String.format("Načtení evidencí %s/%s", datum.getMonthValue(), datum.getYear())
                );
            }
        }
    }

    /**
     * @param datum
     */
    private void nactiStav(LocalDate datum) {
        Dao<List<Evidence>> io = new EvidenceDao();
        final int rok = datum.getYear();
        final int mesic = datum.getMonthValue();

        final Path smenySoubor = Paths.get(String.format(formatNazvu, evidence, rok, mesic, pripona));
        final Path prescasySoubor = Paths.get(String.format(formatNazvu, prescasy, rok, mesic, pripona));

        List<Evidence> evidenceList = null;

        try {
            evidenceList = io.nacti(ConfigPaths.EVIDENCE.resolve(smenySoubor));
        } catch (IOException e) {
            showErrorMessage(
                    String.format("Nepodařilo se nalézt požadovaný soubor evidence " +
                            "nebo je soubor poškozen: %s/%s", mesic, rok)
            );
            this.dispose();
        }

        List<Evidence> prescasyList;
        try {
            prescasyList = io.nacti(ConfigPaths.EVIDENCE.resolve(prescasySoubor));
        } catch (IOException e) {
            showErrorMessage(
                    String.format("Nepodařilo se nalézt požadovaný soubor přesčasů " +
                            "nebo je soubor poškozen: %s/%s", mesic, rok)

            );
            prescasyList = vytvorPrazdnySeznamPrescasu(evidenceList);
        }

        zamestnanciPanels.clear();
        for (int i = 0; i < evidenceList.size(); i++) {
            final Evidence evidence = evidenceList.get(i);
            final Evidence prescasy = prescasyList.get(i);
            zamestnanciPanels.add(
                    new EvidencePanel(evidence, prescasy)
            );
        }
        aktualizujZamestnanceMenu(zamestnanciMenu, zamestnanciPanels);
        aktualizujViewPort(zamestnanciPanels);
        saved = false;
    }

    private List<Evidence> vytvorPrazdnySeznamPrescasu(List<Evidence> evidenceList) {
        List<Evidence> list = new ArrayList<>();

        for (final Evidence evidence : evidenceList) {
            list.add(
                    new ZakladniEvidence(
                            evidence.getZamestnanec(),
                            evidence.getMesic(),
                            evidence.getRok(),
                            evidence.getTydenniPracDoba(),
                            evidence.getPredchoziMesic(),
                            new TreeMap<>()
                    )
            );
        }
        return list;
    }

    private LocalDate extrahujDatum(String nazevSouboru) {
        final String[] split = nazevSouboru.split("-");
        int rok = Integer.parseInt(split[1]);
        int mesi = Integer.parseInt(split[2]);
        return LocalDate.of(rok, mesi, 1);
    }

    private void ulozStavDoSouboru() {
        final Dao<List<Evidence>> io = new EvidenceDao();

        final int rok = current.getSmeny().getRok();
        final int mesic = current.getSmeny().getMesic().getCislo();

        final Path smenySoubor = Paths.get(String.format(formatNazvu, evidence, rok, mesic, pripona));
        final Path prescasySoubor = Paths.get(String.format(formatNazvu, prescasy, rok, mesic, pripona));

        final List<Evidence> evidenceList = this.zamestnanciPanels.stream()
                .map(EvidencePanel::getSmeny)
                .collect(Collectors.toList());

        final List<Evidence> prescasyList = this.zamestnanciPanels.stream()
                .map(EvidencePanel::getPrescasy)
                .collect(Collectors.toList());

        try {
            io.uloz(ConfigPaths.EVIDENCE.resolve(smenySoubor), evidenceList);
        } catch (IOException e) {
            showErrorMessage("Nastala neočekávaná chyba při ukládání směn");
            e.printStackTrace();
        }
        try {
            io.uloz(ConfigPaths.EVIDENCE.resolve(prescasySoubor), prescasyList);
        } catch (IOException e) {
            showErrorMessage("Nastala neočekávaná chyba při ukládání přesčasů");
            e.printStackTrace();
        }

        saved = true;
        showDoneMessage(String.format("Evidence %s/%s byly úspěšně uloženy", mesic, rok));
    }

    private void tiskni() {
        if (current == null) {
            showErrorMessage("Není co tisknout");
        }
        try {
            if (current.printDocument()) {
                showDoneMessage("Dokument odeslán k tisku.");
            }
        } catch (Exception ex) {
            showErrorMessage("Během tisku došlo k neočekávané chybě");
            ex.printStackTrace();
        }
    }

    private void ulozJako() {
        if (current == null) {
            showErrorMessage("Není co ukládat");
        }
        try {
            if (current.saveDocument()) {
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
    private JMenuBar vytvorMenuBar() {
        final JMenuBar jMenuBar = new JMenuBar();
        final JMenu souborMenu = new JMenu("Soubor");
        nacistMenuItem = new JMenuItem("Načíst");
        ulozitMenuItem = new JMenuItem("Uložit");
        ulozitJakoMenuItem = new JMenuItem("Uložit jako...");
        tiskMenuItem = new JMenuItem("Tisk");
        ukoncitMenuItem = new JMenuItem("Ukončit");
        souborMenu.add(nacistMenuItem);
        souborMenu.add(ulozitMenuItem);
        souborMenu.add(ulozitJakoMenuItem);
        souborMenu.add(tiskMenuItem);
        souborMenu.add(new JSeparator());
        souborMenu.add(ukoncitMenuItem);
        jMenuBar.add(souborMenu);

        return jMenuBar;
    }

    private void aktualizujZamestnanceMenu(JMenu menu, List<EvidencePanel> evidencePanels) {
        menu.removeAll();
        evidencePanels.forEach(evidencePanel -> {
            evidencePanel.addPropertyChangeListener("dataChange", evt -> saved = false);

            final String jmenoZamestnance = evidencePanel.getSmeny().getZamestnanec().getCeleJmeno();

            final JMenuItem zam = new JMenuItem(jmenoZamestnance);
            zam.addActionListener(e -> {            // přiřazení posluchače pro výběr
                setTitleName(jmenoZamestnance);
                this.current = evidencePanel;
                this.contentPane.setViewportView(evidencePanel);
            });

            menu.add(zam);
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
                workbook = io.nacti(Path.of("test_4_straznici.xlsx"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            PlanSmen plan = new PlanSmen(workbook, VICESMENNY_NEPRETRZITY);
            new EvidenceWindow(plan, 1).setVisible(true);
        });
    }

}
