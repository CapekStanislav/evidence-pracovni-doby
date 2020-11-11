/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.stanislavcapek.evidencepd.view.component;

import cz.stanislavcapek.evidencepd.model.Mesic;
import cz.stanislavcapek.evidencepd.plansmen.PlanSmen;
import cz.stanislavcapek.evidencepd.zamestnanec.ZamestnanciListModel;
import cz.stanislavcapek.evidencepd.zamestnanec.Zamestnanec;
import cz.stanislavcapek.evidencepd.view.component.evidence.EvidenceWindow;
import cz.stanislavcapek.evidencepd.view.component.evidence.HistorieEvidenciPanel;
import jiconfont.icons.elusive.Elusive;
import jiconfont.swing.IconFontSwing;

import javax.swing.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * GUI Generování evidence pracovní doby. Umožňuje načíst excelový soubor
 * .xlsx s plány směn. Pokud je ve správném formátu, dojde k
 * načtení a jeho zpracování. Vyskytuje-li se v souboru zaměstnanec,
 * který není součástí lokálních dat, upozorní na tuto skutečnost.
 * <p>
 * Dále umožňuje načíst již uložené evidence včetně přečasů.
 *
 * @author Stanislav Čapek
 */
public class GenEvidenceTab extends JPanel {
    private final JLabel lblValidaceNacteni = new JLabel();
    private final JButton btnNacti = new JButton();
    private final JButton btnZobraz = new JButton("Otevřít");
    private final JFileChooser fileChooser = new JFileChooser();
    private final ZamestnanciListModel zamestnanciListModel;

    //JComboBox
    private final JComboBox<Mesic> comboboxMesice = new JComboBox<>(Mesic.values());
    private PlanSmen planSmen;
    private boolean isLoaded = false;
    private final JPanel zobrazPanel;

    /**
     * konstruktor bez parametru.
     */
    public GenEvidenceTab() {
        super();
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.setBorder(BorderFactory.createEmptyBorder(10, 0, 40, 0));

        // seznam strážníků
        this.zamestnanciListModel = ZamestnanciListModel.getInstance();

        final JPanel nacistHistoriiPanel = getNacistHistoriiPanel();
        final JPanel nacistSablonuPanel = getNacistSablonuPanel();
        zobrazPanel = getZobrazPanel();
        nacistHistoriiPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nacistSablonuPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(nacistHistoriiPanel);
        this.add(Box.createVerticalStrut(10));
        this.add(nacistSablonuPanel);
        this.add(zobrazPanel);
        zobrazPanel.setVisible(false);

        this.add(Box.createVerticalGlue());
    }


//    Soukromé metody instance

    private JPanel getNacistSablonuPanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        this.btnNacti.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.lblValidaceNacteni.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(this.btnNacti);
        panel.add(Box.createVerticalStrut(10));
        panel.add(lblValidaceNacteni);

        final NacistSablonuAction action = new NacistSablonuAction("Načíst šablonu");
        btnNacti.setAction(action);
        btnNacti.addPropertyChangeListener(
                "loaded",
                evt -> validaceNacteniSablony(action, evt)
        );
        return panel;
    }

    private JPanel getNacistHistoriiPanel() {
        final JButton nacistZUlozenychButton = new JButton("Načíst z uložených");
        nacistZUlozenychButton.addActionListener(this::zobrazEvidenceZArchivu);

        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
        panel.add(nacistZUlozenychButton);

        return panel;
    }

    public void validaceNacteniSablony(NacistSablonuAction action, PropertyChangeEvent evt) {
        IconFontSwing.register(Elusive.getIconFont());
        Icon goodIcon = IconFontSwing.buildIcon(Elusive.OK, 12, Color.GREEN);
        Icon wrongIcon = IconFontSwing.buildIcon(Elusive.REMOVE, 12, Color.RED);
        if (((boolean) evt.getNewValue())) {
            planSmen = action.getPlanSmen();
            final String txt = String.format("Plán služeb %d", planSmen.getRok());
            lblValidaceNacteni.setText(txt);
            lblValidaceNacteni.setIcon(goodIcon);
            zobrazPanel.setVisible(true);
            final Set<Integer> ids = porovnejSeznamy();
            if (!ids.isEmpty()) {
                zobrazUpozorneni(ids);
            }
        } else {
            lblValidaceNacteni.setText("Není načten správný soubor");
            lblValidaceNacteni.setIcon(wrongIcon);
            zobrazPanel.setVisible(false);
        }
    }

    private JPanel getZobrazPanel() {
        final JPanel panel = new JPanel();
        panel.add(comboboxMesice);
        panel.add(btnZobraz);
        btnZobraz.addActionListener(this::zobrazEvidenceZeSablony);
        return panel;
    }

    private void zobrazEvidenceZeSablony(ActionEvent e) {
        final EvidenceWindow window = new EvidenceWindow(
                planSmen,
                comboboxMesice.getSelectedIndex() + 1
        );
        window.setVisible(true);
    }

    private void zobrazEvidenceZArchivu(ActionEvent e) {
        final HistorieEvidenciPanel nabidka = new HistorieEvidenciPanel().zobrazNabidku();
        if (nabidka.jeVybrano()) {
            new EvidenceWindow(nabidka.ziskejNazev()).setVisible(true);
        }
    }

    /**
     * Metoda zobrazí upozornění, že existují v načteném plánu směn zaměstnanci, kteří nejsou
     * ještě součástí lokálních dat.
     *
     * @param ids množnina ID zaměstnanců
     */
    private void zobrazUpozorneni(Set<Integer> ids) {
//        final int length = ids.size();
        List<JPanel> panels = new ArrayList<>();
        for (Integer id : ids) {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
            final String jmenoZamestnance = planSmen.getZamestnance(id).getCeleJmeno();
            final String[] split = jmenoZamestnance.split(" ");
            final Zamestnanec zamestnanec = new Zamestnanec(id, split[0], split[1]);
            panel.setBorder(BorderFactory.createEmptyBorder(5, 50, 5, 50));
            panel.add(new JLabel(zamestnanec.getCeleJmeno()));
            panel.add(Box.createHorizontalGlue());
            final JButton pridej = new JButton("přidej");
            panel.add(pridej);
            final JButton upravAPridej = new JButton("uprav a přidej");
            panel.add(upravAPridej);

            upravAPridej.addActionListener(e -> {
                EditaceZamestnancePanel edit = new EditaceZamestnancePanel(zamestnanec);
                Object[] options = {"Ulož", "Zruš"};
                int volba = JOptionPane.showOptionDialog(null, edit, "Editace zaměstnance", JOptionPane.YES_NO_OPTION,
                        JOptionPane.PLAIN_MESSAGE, null, options, options[1]);
                if (volba == 0) {
                    Zamestnanec upraveny = edit.getNovyZamestnanec();
                    zamestnanec.setJmeno(upraveny.getJmeno());
                    zamestnanec.setPrijmeni(upraveny.getPrijmeni());
                    zamestnanciListModel.pridejZamestnance(zamestnanec);
                    pridej.setEnabled(false);
                    upravAPridej.setEnabled(false);
                }
            });
            pridej.addActionListener(e -> {
                zamestnanciListModel.pridejZamestnance(zamestnanec);
                pridej.setEnabled(false);
                upravAPridej.setEnabled(false);
            });

            panels.add(panel);
        }

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
        JPanel panel = new JPanel();
        final JLabel label = new JLabel(
                "V plánu služeb se vyskytují zaměstnanci,kteří ještě nejsou součástí seznamu.");
        panel.add(label);
        contentPane.add(panel);
        panels.forEach(contentPane::add);

        JOptionPane.showMessageDialog(null, contentPane,
                "Nalezeni nový zaměstnanci",
                JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Vrátí množinu ID, které jsou v plánu směn, ale nejsou v načteném
     * seznamu v aplikaci.
     *
     * @return množinu ID, které nejsou v aplikaci
     */
    private Set<Integer> porovnejSeznamy() {
        final Set<Integer> zamestnanci = planSmen.getIdZamestnancu();
        return zamestnanci.stream()
                .filter(integer ->
                        zamestnanciListModel.vyhledejPodleID(integer) == null
                ).collect(Collectors.toSet());
    }

    /**
     * Vytvoří nové okno s dodatečnou možností upravovat načtené služby. NOT implemented yet.
     */
    private void generujEvidenci() {
//        if (inputVerifier.verify(txtStr)) {
//            if (!inputVerifier.shouldYieldFocus(btnGeneruj)) {
//                return;
//            }
//        }
//        if (!isLoaded) {
//            JOptionPane.showMessageDialog(null,
//                    "Není načten plán směn. Vyhledejte a načtěte plán směn",
//                    "Chybí plán směn",
//                    JOptionPane.ERROR_MESSAGE);
//            return;
//        }
//
//        final int mesic = comboboxMesice.getSelectedIndex() + 1;
//        final int id = Integer.parseInt(txtStr.getText());
//
//        if (!planSluzeb.isZamestnanec(id, mesic)) {
//            String msg = String.format("Zaměstnanec s ID %d se nenachází v plánu služeb.", id);
//            JOptionPane.showMessageDialog(null, msg,
//                    "Zaměstnanec nenalezen",
//                    JOptionPane.ERROR_MESSAGE);
//            return;
//        }
//
//        final EvidenceWindow evidenceWindow = new EvidenceWindow(
//                planSluzeb,
//                mesic
//        );
//        evidenceWindow.setVisible(true);

    }

}
