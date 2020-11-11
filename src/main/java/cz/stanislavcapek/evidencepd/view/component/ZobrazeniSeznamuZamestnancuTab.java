/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.stanislavcapek.evidencepd.view.component;


import cz.stanislavcapek.evidencepd.zamestnanec.ZamestnanciDao;
import cz.stanislavcapek.evidencepd.zamestnanec.ZamestnanciListModel;
import cz.stanislavcapek.evidencepd.zamestnanec.Zamestnanec;
import cz.stanislavcapek.evidencepd.view.component.util.ZamestnanecRenderer;
import jiconfont.IconCode;
import jiconfont.icons.elusive.Elusive;
import jiconfont.swing.IconFontSwing;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * GUI pro práci se seznamem strážníků. Umožňuje vytvářet nového zaměstnance, mazat nebo upravovat zaměstnance a
 * načíst externí soubor XML se seznamem zaměstnanců.
 *
 * @author Stanislav Čapek
 */
public class ZobrazeniSeznamuZamestnancuTab extends JPanel {

    private final ZamestnanciListModel zamestnanciListModel;
    private final JList<Zamestnanec> zamestnanecJList;
    private final JTextField txgtId, txtJmeno, txtPrijmeni;
    private final JButton btnPridej;
    private final JButton btnOdeber;
    private Action odeberAction;
    private Action upravAciton;

    /**
     * Konstruktor bez parametru.
     */
    public ZobrazeniSeznamuZamestnancuTab() {
        super(true);
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.zamestnanciListModel = ZamestnanciListModel.getInstance();

        // vytvoření akcí
        Action pridejAction = new PridejAction("Přidej", "Přidá nového strážníka", KeyEvent.VK_P);
        odeberAction = new OdeberAction("Odeber", "Odebere vybraného strážníka", KeyEvent.VK_O);
        upravAciton = new UpravAciton("Uprav", "Upraví vybraného strážníka", KeyEvent.VK_U);
        Action nactiAction = new NactiAction("Načti", "Načíst nový seznam strážníků", KeyEvent.VK_N);

        // JPanel paddning //
        this.setBorder(new EmptyBorder(5, 5, 5, 5));

        JPanel wrapPanel;

//        první wrapPanel
        wrapPanel = new JPanel();
        wrapPanel.setLayout(new BoxLayout(wrapPanel, BoxLayout.LINE_AXIS));


        // první - nový zaměstnanec panel //
        JPanel zamPanel = new JPanel();
        zamPanel.setLayout(new BoxLayout(zamPanel, BoxLayout.PAGE_AXIS));
        zamPanel.setBorder(new TitledBorder("Nový strážník"));


        // obecné nastavení //
        JPanel panel = new JPanel();
        Border border5x5 = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        panel.setBorder(border5x5);
        JLabel lbl;
        int sizeOfTextField = 15;

        // první řádek //
        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
        lbl = new JLabel("Služ. číslo:");
        panel.add(lbl);

        panel.add(Box.createHorizontalGlue());

        txgtId = new JTextField(sizeOfTextField);
        txgtId.setMaximumSize(txgtId.getPreferredSize());
        txgtId.setHorizontalAlignment(JTextField.LEFT);
        panel.add(txgtId);
        zamPanel.add(panel);


        // druhý řádek //
        panel = new JPanel();
        panel.setBorder(border5x5);
        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

        lbl = new JLabel("Jméno:");
        panel.add(lbl);

        panel.add(Box.createHorizontalGlue());

        txtJmeno = new JTextField(sizeOfTextField);
        txtJmeno.setMaximumSize(txtJmeno.getPreferredSize());
        txtJmeno.setHorizontalAlignment(JTextField.LEFT);
        panel.add(txtJmeno);
        zamPanel.add(panel);

        // třetí řádek //
        panel = new JPanel();
        panel.setBorder(border5x5);
        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

        lbl = new JLabel("Příjmení:");
        panel.add(lbl);

        panel.add(Box.createHorizontalGlue());

        txtPrijmeni = new JTextField(sizeOfTextField);
        txtPrijmeni.setMaximumSize(txtPrijmeni.getPreferredSize());
        txtPrijmeni.setHorizontalAlignment(JTextField.LEFT);
        panel.add(txtPrijmeni);
        zamPanel.add(panel);

        zamPanel.add(Box.createVerticalGlue());

        wrapPanel.add(zamPanel);

        // druhý - seznam zamestnancu //


        this.zamestnanecJList = new JList<>(zamestnanciListModel);
        this.zamestnanecJList.setCellRenderer(new ZamestnanecRenderer());
        //LIST.setFixedCellWidth(150);
        zamestnanecJList.setVisibleRowCount(6);
        zamestnanecJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        zamestnanecJList.setComponentPopupMenu(new ContextMenuJList());

        JScrollPane sp = new JScrollPane(zamestnanecJList);
        sp.setWheelScrollingEnabled(true);
        sp.setPreferredSize(new Dimension(180, 120));
        sp.setBorder(new TitledBorder("Seznam strážníků"));
        wrapPanel.add(sp);

        this.add(wrapPanel);

        // druhý wrapPanel //
        wrapPanel = new JPanel();
        wrapPanel.setLayout(new BoxLayout(wrapPanel, BoxLayout.PAGE_AXIS));
        wrapPanel.setBorder(new EmptyBorder(10, 0, 10, 0));


        // tlačítka na přidání a odebrání //
        panel = new JPanel();
        panel.setLayout(new GridLayout(0, 4, 15, 0));
        btnPridej = new JButton(pridejAction);
        panel.add(btnPridej);

        btnOdeber = new JButton(odeberAction);
        setEnableDisableOdeber(zamestnanciListModel.getSize());
        panel.add(btnOdeber);

        JButton btnZmen = new JButton(upravAciton);
        panel.add(btnZmen);

        JButton btn = new JButton(nactiAction);
        panel.add(btn);
        panel.setMaximumSize(panel.getPreferredSize());

        wrapPanel.add(panel);
        wrapPanel.add(Box.createVerticalGlue());
        this.add(wrapPanel);

        zamestnanciListModel.addListDataListener(new ListDataListener() {
            @Override
            public void intervalAdded(ListDataEvent e) {
                setEnableDisableOdeber(zamestnanciListModel.getSize());
            }

            @Override
            public void intervalRemoved(ListDataEvent e) {
                setEnableDisableOdeber(zamestnanciListModel.getSize());
            }

            @Override
            public void contentsChanged(ListDataEvent e) {
                setEnableDisableOdeber(zamestnanciListModel.getSize());
            }
        });
    }

    /**
     * Obsluha načtení seznamu strážníků, smaže původní seznam a nahradí jej novým
     */
    private void nactiStrazniky() {
        JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
        int volba = chooser.showOpenDialog(null);

        if (volba == JFileChooser.APPROVE_OPTION) {
            Path file = Paths.get(chooser.getSelectedFile().getAbsolutePath());
            ZamestnanciDao io = new ZamestnanciDao();

            try {
                boolean b = false;
                List<Zamestnanec> seznam;
                if ((seznam = io.nacti(file)) != null) {
                    zamestnanciListModel.vymazSeznam();
                    for (Zamestnanec s :
                            seznam) {
                        zamestnanciListModel.pridejZamestnance(s);
                    }
                    b = true;
                }
                ukazVysledekNacteni(b);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Nepodařilo se načíst seznam strážníků. " +
                                "Při náčítání souboru došlo k chybě",
                        "Chyba při náčítání", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    /**
     * Ukáže dialogové okno s výsledkem načtení souboru
     *
     * @param b výsledek
     */
    private void ukazVysledekNacteni(boolean b) {
        if (b) {
            JOptionPane.showMessageDialog(null, "Seznam úspěšně načten."
                    , "Načtení v pořádku", JOptionPane.PLAIN_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Seznam se nepodařilo načíst. " +
                            "Nesprávný formát souboru"
                    , "Chyba při načítání", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Metoda validuje textové pole ID. Nesmí být prázdné a musí být číslo.
     *
     * @return false, prázdné nebo to není číslo
     */
    private boolean validaceID() {
        boolean v = false;
        try {
            Integer.parseInt(txgtId.getText());
            txgtId.setBackground(Color.white);
            v = true;
        } catch (NumberFormatException e) {
            txgtId.setBackground(Color.YELLOW);
        }
        return v;
    }

    /**
     * Metoda validuje textová pole jména a příjmení, nesmí být prázdná
     *
     * @param txtField textové pole
     * @return false, když je alespoň jedno pole prázdné
     */
    private boolean validaceJmenaAPrijmeni(JTextField txtField) {
        boolean v = false;
        if (txtField.getText().equalsIgnoreCase("")) {
            txtField.setBackground(Color.YELLOW);
        } else {
            txtField.setBackground(Color.WHITE);
            v = true;
        }
        return v;
    }

    /**
     * Metoda kontroluje zda jsou všechny pole (Služební číslo, jméno a
     * příjmení) správně vyplněné. Pokud není žádá o doplnění.
     *
     * @return {@code true} - v případě, kdy je vše vyplněno<br>
     * {@code false} - alespoň jedno pole je prázdné
     */
    private Boolean jeVseVyplneno() {
        boolean valId = validaceID();
        boolean valJmeno = validaceJmenaAPrijmeni(txtJmeno);
        boolean valPrijmeni = validaceJmenaAPrijmeni(txtPrijmeni);

        // návratová hodnota - výchozí
        boolean vseVyplneno = true;
        if (!valId || !valJmeno || !valPrijmeni) {
            vseVyplneno = false;
        }
        if (!vseVyplneno) {
            ukazDialogPotrebnychHodnot(valId, valJmeno, valPrijmeni);
        }
        return vseVyplneno;
    }

    /**
     * Ukáže dialogové okno, které ukáže co je ještě třeba vyplnit.
     *
     * @param id       boolean
     * @param jmeno    boolean
     * @param prijmeni boolean
     */
    private void ukazDialogPotrebnychHodnot(boolean id, boolean jmeno, boolean prijmeni) {
        String zprava = "";
        if (!id) {
            zprava += "Služební číslo je buď prázdné nebo neobsahuje číslo \n";
        }
        if (!jmeno) {
            zprava += "Jméno nesmí být prázdné \n";
        }
        if (!prijmeni) {
            zprava += "Příjmení nesmí být prázdné \n";
        }
        JOptionPane.showMessageDialog(btnPridej,
                "Vyskytla se chyba při zadání u těchto položek: \n"
                        + zprava,
                "Nesprávné údaje",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Zneplatnění tlačítka odeber, když není co odebírat. Seznam je prázdný.
     *
     * @param size velikost seznamu
     */
    private void setEnableDisableOdeber(int size) {
        if (size > 0) {
            btnOdeber.setEnabled(true);
        } else {
            btnOdeber.setEnabled(false);
        }
    }

    /**
     * Akce PŘIDEJ
     */
    private class PridejAction extends AbstractAction {

        PridejAction(String name, String popis, int mnemonic) {
            super(name);
            IconFontSwing.register(Elusive.getIconFont());
            final Elusive plusIcon = Elusive.PLUS;
            Icon addIconSmall = IconFontSwing.buildIcon(plusIcon, 12);
            Icon addIconLarge = IconFontSwing.buildIcon(plusIcon, 16);

            putValue(SHORT_DESCRIPTION, popis);
            putValue(MNEMONIC_KEY, mnemonic);
            putValue(Action.SMALL_ICON, addIconSmall);
            putValue(Action.LARGE_ICON_KEY, addIconLarge);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (jeVseVyplneno()) {
                // vytvoří nového strážníka
                if (zamestnanciListModel.pridejZamestnance(new Zamestnanec(Integer.parseInt(txgtId.getText()), txtJmeno.getText(), txtPrijmeni.getText()))) {
                    // vymazání pole
                    txgtId.setText("");
                    txtJmeno.setText("");
                    txtPrijmeni.setText("");

                } else {
                    JOptionPane.showMessageDialog(btnPridej, "Strážník nebyl přidán! Již existuje strážník " +
                            "se stejným služebním číslem \n", "Nelze přidat strážníka", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /**
     * Akce ODEBER
     */
    private class OdeberAction extends AbstractAction {

        OdeberAction(String name, String popis, int mnemonic) {
            super(name);
            IconFontSwing.register(Elusive.getIconFont());
            final IconCode removIcon = Elusive.REMOVE;
            Icon removeIconSmall = IconFontSwing.buildIcon(removIcon, 12);
            Icon removeIconLarge = IconFontSwing.buildIcon(removIcon, 16);

            putValue(SHORT_DESCRIPTION, popis);
            putValue(MNEMONIC_KEY, mnemonic);
            putValue(Action.SMALL_ICON, removeIconSmall);
            putValue(Action.LARGE_ICON_KEY, removeIconLarge);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // uložení indexu vybrané položky
            int indexKodebrani = zamestnanecJList.getSelectedIndex();

            if (!(indexKodebrani < 0)) {
                Object[] anoNe = {"Ano", "Ne"};
                final String message = "Opravdu odebrat: " +
                        zamestnanciListModel.getElementAt(indexKodebrani).getCeleJmeno();
                int odpoved = JOptionPane.showOptionDialog(btnOdeber,
                        message,
                        "Odebrání strážníka ze seznamu",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, anoNe, anoNe[1]);

                // jestliže ano pak odebrat
                if (odpoved == JOptionPane.YES_OPTION) {
                    Zamestnanec kOdebrani = zamestnanciListModel.getElementAt(indexKodebrani);
                    zamestnanciListModel.odeberZamestnance(kOdebrani);
                }
            }

            // uložení velikosti seznamu (počet položek)
            int size = zamestnanciListModel.getSize();

            // kontrola, zda je co ještě odebíra. Zneplatnění tlačítka ODEBER
            if (size == 0) {
                btnOdeber.setEnabled(false);
            }
        }
    }

    /**
     * Akce UPRAV
     */
    private class UpravAciton extends AbstractAction {

        UpravAciton(String name, String popis, int mnemonic) {
            super(name);
            IconFontSwing.register(Elusive.getIconFont());

            final Elusive wrench = Elusive.WRENCH;
            Icon editSmall = IconFontSwing.buildIcon(wrench, 12);
            Icon editLarge = IconFontSwing.buildIcon(wrench, 16);

            putValue(SHORT_DESCRIPTION, popis);
            putValue(MNEMONIC_KEY, mnemonic);
            putValue(SMALL_ICON, editSmall);
            putValue(LARGE_ICON_KEY, editLarge);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int index;
            if ((index = zamestnanecJList.getSelectedIndex()) == -1) {
                JOptionPane.showMessageDialog(null, "Nebyl vybrán zaměstnanec k editaci",
                        "Žádný vybraný zaměstnanec",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            Zamestnanec z = zamestnanecJList.getModel().getElementAt(index);
            EditaceZamestnancePanel edit = new EditaceZamestnancePanel(z);
            Object[] options = {"Ulož", "Zruš"};
            int volba = JOptionPane.showOptionDialog(null, edit, "Editace zaměstnance", JOptionPane.YES_NO_OPTION,
                    JOptionPane.PLAIN_MESSAGE, null, options, options[1]);
            if (volba == 0) {
                Zamestnanec upraveny = edit.getNovyZamestnanec();
                z.setJmeno(upraveny.getJmeno());
                z.setPrijmeni(upraveny.getPrijmeni());
                zamestnanciListModel.obnovModel();
            }


        }
    }

    /**
     * NAČTI zaměstnance
     */
    private class NactiAction extends AbstractAction {

        NactiAction(String name, String popis, int mnemonic) {
            super(name);
            IconFontSwing.register(Elusive.getIconFont());
            final Elusive folderOpen = Elusive.FOLDER_OPEN;
            Icon loadSmall = IconFontSwing.buildIcon(folderOpen, 12);
            Icon loadLarge = IconFontSwing.buildIcon(folderOpen, 16);

            putValue(Action.SHORT_DESCRIPTION, popis);
            putValue(Action.MNEMONIC_KEY, mnemonic);
            putValue(Action.SMALL_ICON, loadSmall);
            putValue(Action.LARGE_ICON_KEY, loadLarge);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            nactiStrazniky();
        }
    }

    /**
     * Contextové menu pro JList.
     */
    private class ContextMenuJList extends JPopupMenu {
        ContextMenuJList() {
            super();
            add(upravAciton);
            add(odeberAction);
        }
    }
}
