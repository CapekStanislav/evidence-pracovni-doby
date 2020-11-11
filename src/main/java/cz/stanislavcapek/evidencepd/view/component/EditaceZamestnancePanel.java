package cz.stanislavcapek.evidencepd.view.component;

import cz.stanislavcapek.evidencepd.zamestnanec.Zamestnanec;

import javax.swing.*;
import java.awt.Dimension;

/**
 * GUI pro editaci zaměstnance předaného v parametru konstruktoru. Třída na požádání vrací nového upraveného
 * {@link Zamestnanec}. Původně vložený zaměstnanec zůstává beze změn.
 *
 * @author Stanislav Čapek
 */
class EditaceZamestnancePanel extends JPanel {

    private final JTextField txtJmeno;
    private final JTextField txtPrijmeni;
    private Zamestnanec novyZamestnanec;

    /**
     * Konstruktor
     *
     * @param zamestnanec zaměstnanec k úpravě
     */
    EditaceZamestnancePanel(Zamestnanec zamestnanec) {
        novyZamestnanec = new Zamestnanec(zamestnanec.getId(),
                zamestnanec.getJmeno(),
                zamestnanec.getPrijmeni());

        StringVerifier stringVerifier = new StringVerifier();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        JLabel lbl = new JLabel("Identifikační číslo: " + zamestnanec.getId());
        panel.add(lbl);
        panel.add(Box.createRigidArea(new Dimension(10, 10)));

        lbl = new JLabel("Jméno");
        panel.add(lbl);

        txtJmeno = new JTextField(zamestnanec.getJmeno());
        txtJmeno.setInputVerifier(stringVerifier);
        panel.add(txtJmeno);

        panel.add(Box.createRigidArea(new Dimension(5, 5)));

        lbl = new JLabel("Příjmení");
        panel.add(lbl);

        txtPrijmeni = new JTextField(zamestnanec.getPrijmeni());
        txtPrijmeni.setInputVerifier(stringVerifier);
        panel.add(txtPrijmeni);
        panel.add(Box.createRigidArea(new Dimension(10, 10)));

        this.add(panel);
    }

    /**
     * Vrátí nově upraveného zaměstnance.
     *
     * @return {@link Zamestnanec} upravený zaměstnanec
     */
    Zamestnanec getNovyZamestnanec() {
        return novyZamestnanec;
    }

    /**
     * Validátor textového řetězce
     */
    private class StringVerifier extends InputVerifier {

        @Override
        public boolean verify(JComponent input) {
            if (input instanceof JTextField) {
                return !((JTextField) input).getText().equals("");
            }
            return false;
        }

        @Override
        public boolean shouldYieldFocus(JComponent source) {

            if (verify(source)) {
                if (source.equals(txtJmeno)) {
                    String jmeno = ((JTextField) source).getText();
                    novyZamestnanec.setJmeno(jmeno);
                } else if (source.equals(txtPrijmeni)) {
                    String prijmeni = ((JTextField) source).getText();
                    novyZamestnanec.setPrijmeni(prijmeni);
                }
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Nesmí obsahovat prázdné pole",
                        "Chyba při zadávání", JOptionPane.WARNING_MESSAGE);
            }
            return false;
        }
    }

}

